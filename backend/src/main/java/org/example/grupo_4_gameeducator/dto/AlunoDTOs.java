package org.example.grupo_4_gameeducator.dto;

import jakarta.validation.constraints.DecimalMin;
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

    public record ConcluirCursoRequest(
            @NotBlank(message = "nomeCurso e obrigatorio") String nomeCurso,
            @DecimalMin(value = "0.0", message = "media nao pode ser negativa") double media
    ) {}

    public record CreditarMoedasRequest(
            @Min(value = 1, message = "quantidade deve ser maior que zero") int quantidade,
            @NotBlank(message = "motivo e obrigatorio") String motivo
    ) {}

    public record ConverterCriptoRequest(
            @Min(value = 1, message = "quantidadeMoedas deve ser maior que zero") int quantidadeMoedas,
            @DecimalMin(value = "0.0", message = "taxaCambio nao pode ser negativa") double taxaCambio
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