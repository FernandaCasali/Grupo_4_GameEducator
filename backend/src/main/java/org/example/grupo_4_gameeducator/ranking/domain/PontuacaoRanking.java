package org.example.grupo_4_gameeducator.ranking.domain;

/**
 * Regra de dominio do RF01 - Ranking. Java puro, sem Spring, JPA ou DTO:
 * pontuacao = PESO_CURSO * totalCursos + saldoMoedas (regra didatica, a validar com o grupo).
 * Trocar o peso ou a formula afeta somente esta classe.
 */
public final class PontuacaoRanking {

    public static final int PESO_CURSO = 100;

    private PontuacaoRanking() {
    }

    public static int calcular(int totalCursos, int saldoMoedas) {
        if (totalCursos < 0 || saldoMoedas < 0) {
            throw new IllegalArgumentException("Cursos e moedas nao podem ser negativos.");
        }
        return PESO_CURSO * totalCursos + saldoMoedas;
    }
}
