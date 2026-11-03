package org.example.grupo_4_gameeducator.recomendacao.application;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.aluno.api.AlunoGateway;
import org.example.grupo_4_gameeducator.recomendacao.api.RecomendacaoDTOs.RecomendacaoItem;
import org.example.grupo_4_gameeducator.recomendacao.api.RecomendacaoDTOs.RecomendacaoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecomendacaoAppService {

    public static final String ORIGEM_IA = "IA";
    public static final String ORIGEM_FALLBACK = "FALLBACK_SEM_IA";

    private static final Logger log = LoggerFactory.getLogger(RecomendacaoAppService.class);

    private final AlunoGateway alunos;
    private final RecomendadorPort recomendador;

    public RecomendacaoAppService(AlunoGateway alunos, RecomendadorPort recomendador) {
        this.alunos = alunos;
        this.recomendador = recomendador;
    }

    public RecomendacaoResponse recomendar(Long alunoId) {
        AlunoResponse aluno = alunos.buscarPorId(alunoId);
        try {
            List<RecomendacaoItem> itens = recomendador.recomendar(alunoId, aluno.cursosDesbloqueados()).stream()
                    .map(s -> new RecomendacaoItem(s.curso(), s.motivo()))
                    .toList();
            return new RecomendacaoResponse(itens, false, ORIGEM_IA);
        } catch (RecomendadorIndisponivelException ex) {
            log.warn("Servico de IA indisponivel; respondendo em modo degradado: {}", ex.getMessage());
            return new RecomendacaoResponse(List.of(), true, ORIGEM_FALLBACK);
        }
    }
}
