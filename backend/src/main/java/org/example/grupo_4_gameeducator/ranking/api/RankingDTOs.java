package org.example.grupo_4_gameeducator.ranking.api;

public class RankingDTOs {

    private RankingDTOs() {
        // so agrupa records; nao deve ser instanciada (JaCoCo ignora construtor privado vazio)
    }

    public record RankingItemResponse(
            int posicao,
            Long alunoId,
            String nome,
            int totalCursos,
            int saldoMoedas,
            int pontuacao
    ) {}
}
