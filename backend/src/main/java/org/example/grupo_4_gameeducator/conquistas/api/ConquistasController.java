package org.example.grupo_4_gameeducator.conquistas.api;

import org.example.grupo_4_gameeducator.conquistas.api.ConquistasDTOs.ConquistasResponse;
import org.example.grupo_4_gameeducator.conquistas.application.ConquistasAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alunos/{id}/conquistas")
@Tag(name = "Conquistas", description = "RF02 - Badges do aluno")
@CrossOrigin(origins = "*")
public class ConquistasController {

    private final ConquistasAppService service;

    public ConquistasController(ConquistasAppService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista os badges conquistados pelo aluno")
    public ConquistasResponse consultar(@PathVariable Long id) {
        return service.consultar(id);
    }
}
