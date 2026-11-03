package org.example.grupo_4_gameeducator.notificacoes.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimuladorDeFalhaTest {

    @Test
    void deveIniciarComValorConfigurado() {
        assertTrue(new SimuladorDeFalha(true).ativa());
        assertFalse(new SimuladorDeFalha(false).ativa());
    }

    @Test
    void devePermitirAlternarEmTempoDeExecucao() {
        var s = new SimuladorDeFalha(false);
        s.definir(true);
        assertTrue(s.ativa());
    }
}
