package org.example.grupo_4_gameeducator.notificacoes.application;

import org.example.grupo_4_gameeducator.cursos.api.CursoConcluidoEvent;
import org.example.grupo_4_gameeducator.notificacoes.application.RegistroNotificacao.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** O listener so repassa o evento para o servico (o @Async e testado pelo Spring, nao aqui). */
class NotificacaoListenerTest {

    @Test
    void deveRepassarOEventoParaOServicoDeNotificacao() {
        var notificador = new NotificacaoAppServiceTest.NotificadorFalso();
        var service = new NotificacaoAppService(notificador);
        var listener = new NotificacaoListener(service);

        listener.aoConcluirCurso(new CursoConcluidoEvent(1L, "Carolina", "Logica", 8.5, 3, false));

        assertEquals(1, notificador.enviadas);
        assertEquals(Status.ENVIADA, service.listar().get(0).status());
    }
}
