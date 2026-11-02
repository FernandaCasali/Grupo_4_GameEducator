package org.example.grupo_4_gameeducator.conquistas.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Regra de dominio do RF02 (badges). As conquistas sao derivadas do progresso do aluno,
 * sem estado proprio: trocar um limite ou criar um badge afeta somente este modulo.
 */
public final class RegrasConquistas {

    public static final int CURSOS_PRIMEIRO = 1;
    public static final int CURSOS_CINCO = 5;
    public static final int CURSOS_DOZE = 12;
    public static final int MOEDAS_COLECIONADOR = 100;

    private RegrasConquistas() {
    }

    public static List<Badge> badgesPara(int totalCursos, int saldoMoedas) {
        if (totalCursos < 0 || saldoMoedas < 0) {
            throw new IllegalArgumentException("Cursos e moedas nao podem ser negativos.");
        }
        List<Badge> badges = new ArrayList<>();
        if (totalCursos >= CURSOS_PRIMEIRO) {
            badges.add(Badge.PRIMEIRO_CURSO);
        }
        if (totalCursos >= CURSOS_CINCO) {
            badges.add(Badge.CINCO_CURSOS);
        }
        if (totalCursos >= CURSOS_DOZE) {
            badges.add(Badge.DOZE_CURSOS);
        }
        if (saldoMoedas >= MOEDAS_COLECIONADOR) {
            badges.add(Badge.COLECIONADOR_DE_MOEDAS);
        }
        return badges;
    }
}
