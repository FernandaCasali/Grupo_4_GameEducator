package org.example.grupo_4_gameeducator.notificacoes.application;

import org.example.grupo_4_gameeducator.cursos.api.CursoConcluidoEvent;
import org.example.grupo_4_gameeducator.notificacoes.application.RegistroNotificacao.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificacaoAppServiceTest {

    /** Consumidor falso, controlavel pelo teste (equivale ao SimuladorDeFalha do lab). */
    static class NotificadorFalso implements NotificadorPort {
        boolean fora = false;
        int enviadas = 0;

        @Override
        public void enviar(Long alunoId, String mensagem) {
            if (fora) {
                throw new IllegalStateException("consumidor indisponivel");
            }
            enviadas++;
        }
    }

    private NotificadorFalso notificador;
    private NotificacaoAppService service;

    private final CursoConcluidoEvent evento =
            new CursoConcluidoEvent(1L, "Carolina", "Logica", 8.5, 3, false);

    @BeforeEach
    void setUp() {
        notificador = new NotificadorFalso();
        service = new NotificacaoAppService(notificador);
    }

    @Test
    void deveRegistrarNotificacaoEnviada() {
        service.tratar(evento);

        assertEquals(1, service.listar().size());
        assertEquals(Status.ENVIADA, service.listar().get(0).status());
        assertEquals(1, notificador.enviadas);
    }

    @Test
    void consumidorIndisponivelRegistraFalhaSemLancarExcecao() {
        notificador.fora = true;

        assertDoesNotThrow(() -> service.tratar(evento));

        assertEquals(Status.FALHOU, service.listar().get(0).status());
        assertEquals(0, notificador.enviadas);
    }

    @Test
    void deveRecuperarNotificacoesFalhasQuandoConsumidorVolta() {
        notificador.fora = true;
        service.tratar(evento);
        notificador.fora = false;

        int recuperadas = service.reenviarFalhas();

        assertEquals(1, recuperadas);
        assertEquals(1, service.listar().size());
        assertEquals(Status.ENVIADA, service.listar().get(0).status());
    }

    @Test
    void reenvioContinuaFalhandoEnquantoConsumidorEstiverFora() {
        notificador.fora = true;
        service.tratar(evento);

        assertEquals(0, service.reenviarFalhas());
        assertEquals(Status.FALHOU, service.listar().get(0).status());
    }

    @Test
    void deveMontarMensagemConformeResultado() {
        assertTrue(NotificacaoAppService.montarMensagem(evento).contains("liberou 3 novos cursos"));
        assertTrue(NotificacaoAppService.montarMensagem(
                new CursoConcluidoEvent(1L, "Carolina", "Redes", 8.0, 0, true)).contains("limite"));
        assertEquals("Carolina, voce concluiu Redes.", NotificacaoAppService.montarMensagem(
                new CursoConcluidoEvent(1L, "Carolina", "Redes", 5.0, 0, false)));
    }
}
