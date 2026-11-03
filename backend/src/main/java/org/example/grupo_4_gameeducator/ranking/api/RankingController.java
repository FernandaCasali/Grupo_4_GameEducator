package org.example.grupo_4_gameeducator.ranking.api;

import org.example.grupo_4_gameeducator.ranking.api.RankingDTOs.RankingItemResponse;
import org.example.grupo_4_gameeducator.ranking.application.RankingAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@Tag(name = "Ranking", description = "RF01 - Ranking de alunos")
@CrossOrigin(origins = "*")
public class RankingController {

    private final RankingAppService service;

    public RankingController(RankingAppService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista o ranking de alunos por pontuacao")
    public List<RankingItemResponse> listar() {
        return service.listar();
    }
}
