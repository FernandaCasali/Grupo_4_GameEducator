package org.example.grupo_4_gameeducator.cursos.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public class CursosDTOs {

    private CursosDTOs() {
        // so agrupa records; nao deve ser instanciada (JaCoCo ignora construtor privado vazio)
    }

    public record ConcluirCursoRequest(
            @NotBlank(message = "nomeCurso e obrigatorio") String nomeCurso,
            @DecimalMin(value = "0.0", message = "media nao pode ser negativa") double media
    ) {}
}
