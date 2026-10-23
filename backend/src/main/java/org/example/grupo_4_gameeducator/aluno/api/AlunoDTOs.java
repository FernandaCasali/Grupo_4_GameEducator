package org.example.grupo_4_gameeducator.aluno.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Set;

public class AlunoDTOs {

    public record CriarAlunoRequest(
            @NotBlank(message = "nome e obrigatorio") String nome,
            String planoNome,
            @Min(value = 0, message = "limite do plano nao pode ser negativo") int planoLimiteCursos
    ) {}

    public record AlunoResponse(
            Long id,
            String nome,
            String planoNome,
            int totalCursos,
            boolean premium,
            int saldoMoedas,
            boolean notificado,
            List<String> cursosDesbloqueados,
            Set<String> notificacoes,
            List<String> historicoMoedas
    ) {}
}
