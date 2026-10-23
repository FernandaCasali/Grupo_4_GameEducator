package org.example.grupo_4_gameeducator.cursos.api;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.cursos.api.CursosDTOs.ConcluirCursoRequest;
import org.example.grupo_4_gameeducator.cursos.application.CursosAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Cursos", description = "Conclusao de cursos e desbloqueio automatico")
@CrossOrigin(origins = "*")
public class CursosController {

    private final CursosAppService service;

    public CursosController(CursosAppService service) {
        this.service = service;
    }

    @PostMapping("/{id}/concluir-curso")
    @Operation(summary = "US Eduarda: processa a conclusao de um curso")
    public AlunoResponse concluirCurso(@PathVariable Long id, @Valid @RequestBody ConcluirCursoRequest request) {
        return service.concluirCurso(id, request);
    }
}
