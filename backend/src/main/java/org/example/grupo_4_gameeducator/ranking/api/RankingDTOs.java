package org.example.grupo_4_gameeducator.ranking.api;

public class RankingDTOs {

    public record RankingItemResponse(
            int posicao,
            Long alunoId,
            String nome,
            int totalCursos,
            int saldoMoedas,
            int pontuacao
    ) {}
}
