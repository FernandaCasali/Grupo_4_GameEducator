package org.example.grupo_4_gameeducator.conquistas.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegrasConquistasTest {

    @Test
    void semProgressoNaoTemBadges() {
        assertTrue(RegrasConquistas.badgesPara(0, 0).isEmpty());
    }

    @Test
    void primeiroCursoDaBadgeDePrimeiroCurso() {
        assertEquals(List.of(Badge.PRIMEIRO_CURSO), RegrasConquistas.badgesPara(1, 0));
    }

    @Test
    void cincoCursosAcumulaBadges() {
        assertEquals(List.of(Badge.PRIMEIRO_CURSO, Badge.CINCO_CURSOS), RegrasConquistas.badgesPara(5, 0));
    }

    @Test
    void dozeCursosDaTodosOsBadgesDeCursos() {
        assertEquals(List.of(Badge.PRIMEIRO_CURSO, Badge.CINCO_CURSOS, Badge.DOZE_CURSOS),
                RegrasConquistas.badgesPara(12, 0));
    }

    @Test
    void cemMoedasDaBadgeDeColecionador() {
        assertEquals(List.of(Badge.COLECIONADOR_DE_MOEDAS), RegrasConquistas.badgesPara(0, 100));
        assertTrue(RegrasConquistas.badgesPara(0, 99).isEmpty());
    }

    @Test
    void valoresNegativosSaoRejeitados() {
        assertThrows(IllegalArgumentException.class, () -> RegrasConquistas.badgesPara(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> RegrasConquistas.badgesPara(0, -1));
    }
}
