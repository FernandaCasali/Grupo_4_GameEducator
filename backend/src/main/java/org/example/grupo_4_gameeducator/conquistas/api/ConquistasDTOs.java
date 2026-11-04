package org.example.grupo_4_gameeducator.conquistas.api;

import java.util.List;

public class ConquistasDTOs {

    private ConquistasDTOs() {
        // so agrupa records; nao deve ser instanciada (JaCoCo ignora construtor privado vazio)
    }

    public record BadgeResponse(String codigo, String descricao) {}

    public record ConquistasResponse(Long alunoId, String nome, List<BadgeResponse> badges) {}
}
