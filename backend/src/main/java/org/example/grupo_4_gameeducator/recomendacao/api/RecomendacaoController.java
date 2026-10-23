package org.example.grupo_4_gameeducator.recomendacao.api;

import org.example.grupo_4_gameeducator.recomendacao.api.RecomendacaoDTOs.RecomendacaoResponse;
import org.example.grupo_4_gameeducator.recomendacao.application.RecomendacaoAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Recomendacao", description = "RF03 - Recomendacao inteligente (servico Python)")
@CrossOrigin(origins = "*")
public class RecomendacaoController {

    private final RecomendacaoAppService service;

    public RecomendacaoController(RecomendacaoAppService service) {
        this.service = service;
    }

    @GetMapping("/{id}/recomendacoes")
    @Operation(summary = "Recomenda proximos cursos; responde em modo degradado se a IA estiver fora")
    public RecomendacaoResponse recomendar(@PathVariable Long id) {
        return service.recomendar(id);
    }
}
