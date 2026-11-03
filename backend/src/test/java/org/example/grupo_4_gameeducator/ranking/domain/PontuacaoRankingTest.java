package org.example.grupo_4_gameeducator.ranking.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PontuacaoRankingTest {

    @Test
    void calculaCursosComPesoMaisMoedas() {
        assertEquals(310, PontuacaoRanking.calcular(3, 10));
    }

    @Test
    void semCursosNemMoedasValeZero() {
        assertEquals(0, PontuacaoRanking.calcular(0, 0));
    }

    @Test
    void valoresNegativosSaoRejeitados() {
        assertThrows(IllegalArgumentException.class, () -> PontuacaoRanking.calcular(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> PontuacaoRanking.calcular(0, -5));
    }
}
