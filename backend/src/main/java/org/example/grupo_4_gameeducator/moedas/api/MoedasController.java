package org.example.grupo_4_gameeducator.moedas.api;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.moedas.api.MoedasDTOs.ConverterCriptoRequest;
import org.example.grupo_4_gameeducator.moedas.api.MoedasDTOs.CreditarMoedasRequest;
import org.example.grupo_4_gameeducator.moedas.application.MoedasAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Moedas", description = "Moedas por engajamento e conversao em cripto")
@CrossOrigin(origins = "*")
public class MoedasController {

    private final MoedasAppService service;

    public MoedasController(MoedasAppService service) {
        this.service = service;
    }

    @PostMapping("/{id}/moedas")
    @Operation(summary = "US Carolina: credita moedas por engajamento")
    public AlunoResponse creditarMoedas(@PathVariable Long id, @Valid @RequestBody CreditarMoedasRequest request) {
        return service.creditarMoedas(id, request);
    }

    @PostMapping("/{id}/converter-cripto")
    @Operation(summary = "US Fernanda: converte moedas em criptomoeda")
    public AlunoResponse converterCripto(@PathVariable Long id, @Valid @RequestBody ConverterCriptoRequest request) {
        return service.converterCripto(id, request);
    }
}
