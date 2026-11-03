package org.example.grupo_4_gameeducator.conquistas.api;

import java.util.List;

public class ConquistasDTOs {

    public record BadgeResponse(String codigo, String descricao) {}

    public record ConquistasResponse(Long alunoId, String nome, List<BadgeResponse> badges) {}
}
