package org.example.grupo_4_gameeducator.notificacoes.application;

import org.example.grupo_4_gameeducator.notificacoes.internal.LogNotificador;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogNotificadorTest {

    @Test
    void deveEnviarQuandoSemFalhaSimulada() {
        assertDoesNotThrow(() -> new LogNotificador(new SimuladorDeFalha(false)).enviar(1L, "ola"));
    }

    @Test
    void deveFalharQuandoFalhaSimuladaAtiva() {
        var notificador = new LogNotificador(new SimuladorDeFalha(true));

        assertThrows(IllegalStateException.class, () -> notificador.enviar(1L, "ola"));
    }
}
