package org.example.grupo_4_gameeducator.cursos.application;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.aluno.api.AlunoGateway;
import org.example.grupo_4_gameeducator.aluno.domain.Aluno;
import org.example.grupo_4_gameeducator.aluno.domain.Curso;
import org.example.grupo_4_gameeducator.aluno.domain.StatusCurso;
import org.example.grupo_4_gameeducator.cursos.api.CursoConcluidoEvent;
import org.example.grupo_4_gameeducator.cursos.api.CursosDTOs.ConcluirCursoRequest;
import org.example.grupo_4_gameeducator.cursos.domain.DesbloqueioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class CursosAppService {

    private static final Logger log = LoggerFactory.getLogger(CursosAppService.class);

    private final AlunoGateway alunos;
    private final ApplicationEventPublisher eventos;
    private final DesbloqueioService desbloqueio = new DesbloqueioService();

    public CursosAppService(AlunoGateway alunos, ApplicationEventPublisher eventos) {
        this.alunos = alunos;
        this.eventos = eventos;
    }

    /**
     * SINCRONO: o aluno precisa da resposta imediata (cursos liberados). 
     * ASSINCRONO: notificacao/analytics reagem ao evento e nunca podem impedir a conclusao.
     */
    public AlunoResponse concluirCurso(Long alunoId, ConcluirCursoRequest request) {
        Curso curso = new Curso(request.nomeCurso(), StatusCurso.CONCLUIDO);
        int[] liberados = {0};

        AlunoResponse resposta = alunos.atualizar(alunoId, aluno -> {
            int antes = aluno.getCursosDesbloqueados().size();
            desbloqueio.processarDesbloqueio(aluno, curso, request.media());
            liberados[0] = aluno.getCursosDesbloqueados().size() - antes;
        });

        publicar(new CursoConcluidoEvent(
                alunoId,
                resposta.nome(),
                request.nomeCurso(),
                request.media(),
                liberados[0],
                resposta.notificacoes().contains(Aluno.NOTIFICACAO_LIMITE_ATINGIDO)));
        return resposta;
    }

    private void publicar(CursoConcluidoEvent evento) {
        try {
            eventos.publishEvent(evento);
        } catch (RuntimeException ex) {
            // A conclusao ja foi persistida; falha de publicacao e registrada, nunca propagada.
            log.warn("Falha ao publicar CursoConcluidoEvent (conclusao mantida): {}", ex.getMessage());
        }
    }
}
