package org.example.grupo_4_gameeducator.notificacoes.application;

import org.example.grupo_4_gameeducator.cursos.api.CursoConcluidoEvent;
import org.example.grupo_4_gameeducator.notificacoes.application.RegistroNotificacao.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Consumidor do evento. Falha de envio e REGISTRADA (FALHOU) e pode ser reprocessada;
 * jamais volta para quem concluiu o curso.
 */
@Service
public class NotificacaoAppService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoAppService.class);

    private final NotificadorPort notificador;
    private final List<RegistroNotificacao> historico = new CopyOnWriteArrayList<>();

    public NotificacaoAppService(NotificadorPort notificador) {
        this.notificador = notificador;
    }

    public void tratar(CursoConcluidoEvent evento) {
        enviarERegistrar(evento.alunoId(), montarMensagem(evento));
    }

    public List<RegistroNotificacao> listar() {
        return List.copyOf(historico);
    }

    /** Recuperabilidade: reenvia o que falhou. Retorna quantas foram recuperadas. */
    public int reenviarFalhas() {
        int recuperadas = 0;
        for (RegistroNotificacao falha : List.copyOf(historico)) {
            if (falha.status() != Status.FALHOU) {
                continue;
            }
            try {
                notificador.enviar(falha.alunoId(), falha.mensagem());
                historico.remove(falha);
                historico.add(new RegistroNotificacao(falha.alunoId(), falha.mensagem(), Status.ENVIADA, "reenviada", Instant.now()));
                recuperadas++;
            } catch (RuntimeException ex) {
                log.warn("Reenvio falhou para aluno {}: {}", falha.alunoId(), ex.getMessage());
            }
        }
        return recuperadas;
    }

    private void enviarERegistrar(Long alunoId, String mensagem) {
        try {
            notificador.enviar(alunoId, mensagem);
            historico.add(new RegistroNotificacao(alunoId, mensagem, Status.ENVIADA, "ok", Instant.now()));
        } catch (RuntimeException ex) {
            log.warn("Notificacao falhou para aluno {} (conclusao do curso NAO foi afetada): {}", alunoId, ex.getMessage());
            historico.add(new RegistroNotificacao(alunoId, mensagem, Status.FALHOU, ex.getMessage(), Instant.now()));
        }
    }

    static String montarMensagem(CursoConcluidoEvent e) {
        if (e.limiteAtingido()) {
            return e.alunoNome() + ", voce concluiu " + e.nomeCurso() + ", mas atingiu o limite de cursos do seu plano.";
        }
        if (e.cursosLiberados() > 0) {
            return e.alunoNome() + ", voce concluiu " + e.nomeCurso() + " e liberou " + e.cursosLiberados() + " novos cursos!";
        }
        return e.alunoNome() + ", voce concluiu " + e.nomeCurso() + ".";
    }
}
