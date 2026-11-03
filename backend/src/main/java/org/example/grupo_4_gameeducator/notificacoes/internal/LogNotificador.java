package org.example.grupo_4_gameeducator.notificacoes.internal;

import org.example.grupo_4_gameeducator.notificacoes.application.NotificadorPort;
import org.example.grupo_4_gameeducator.notificacoes.application.SimuladorDeFalha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** ADAPTER de laboratorio: "envia" escrevendo no log; pode simular consumidor indisponivel. */
@Component
public class LogNotificador implements NotificadorPort {

    private static final Logger log = LoggerFactory.getLogger(LogNotificador.class);

    private final SimuladorDeFalha falha;

    public LogNotificador(SimuladorDeFalha falha) {
        this.falha = falha;
    }

    @Override
    public void enviar(Long alunoId, String mensagem) {
        if (falha.ativa()) {
            throw new IllegalStateException("Servico de notificacao indisponivel (simulado)");
        }
        log.info("NOTIFICACAO para aluno {}: {}", alunoId, mensagem);
    }
}
