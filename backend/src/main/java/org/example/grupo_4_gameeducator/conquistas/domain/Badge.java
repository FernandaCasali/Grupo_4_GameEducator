package org.example.grupo_4_gameeducator.conquistas.domain;

/** Badges (conquistas) do RF02. Java puro: sem Spring, JPA ou DTO. */
public enum Badge {
    PRIMEIRO_CURSO("Concluiu o primeiro curso"),
    CINCO_CURSOS("Concluiu 5 cursos"),
    DOZE_CURSOS("Concluiu 12 cursos"),
    COLECIONADOR_DE_MOEDAS("Acumulou 100 moedas");

    private final String descricao;

    Badge(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
