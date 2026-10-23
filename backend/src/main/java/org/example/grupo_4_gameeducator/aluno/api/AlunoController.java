package org.example.grupo_4_gameeducator.aluno.api;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.CriarAlunoRequest;
import org.example.grupo_4_gameeducator.aluno.application.AlunoAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Alunos", description = "Cadastro de alunos")
@CrossOrigin(origins = "*")
public class AlunoController {

    private final AlunoAppService service;

    public AlunoController(AlunoAppService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cria um novo aluno")
    public ResponseEntity<AlunoResponse> criar(@Valid @RequestBody CriarAlunoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping
    @Operation(summary = "Lista todos os alunos")
    public List<AlunoResponse> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um aluno pelo id")
    public AlunoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}
