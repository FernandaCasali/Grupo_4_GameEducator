package org.example.grupo_4_gameeducator.moedas.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class MoedasDTOs {

    public record CreditarMoedasRequest(
            @Min(value = 1, message = "quantidade deve ser maior que zero") int quantidade,
            @NotBlank(message = "motivo e obrigatorio") String motivo
    ) {}

    public record ConverterCriptoRequest(
            @Min(value = 1, message = "quantidadeMoedas deve ser maior que zero") int quantidadeMoedas,
            @DecimalMin(value = "0.0", message = "taxaCambio nao pode ser negativa") double taxaCambio
    ) {}
}
