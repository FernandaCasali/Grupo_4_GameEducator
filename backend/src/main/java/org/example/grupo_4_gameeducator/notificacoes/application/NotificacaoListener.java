package org.example.grupo_4_gameeducator.notificacoes.application;

import org.example.grupo_4_gameeducator.cursos.api.CursoConcluidoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/** Consumidor ASSINCRONO: roda em outra thread, entao nao atrasa nem derruba a requisicao original. */
@Component
public class NotificacaoListener {

    private final NotificacaoAppService service;

    public NotificacaoListener(NotificacaoAppService service) {
        this.service = service;
    }

    @Async
    @EventListener
    public void aoConcluirCurso(CursoConcluidoEvent evento) {
        service.tratar(evento);
    }
}
