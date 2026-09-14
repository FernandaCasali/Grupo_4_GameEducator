package org.example.grupo_4_gameeducator.controller;

import org.example.grupo_4_gameeducator.dto.AlunoDTOs.*;
import org.example.grupo_4_gameeducator.service.AlunoAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Alunos", description = "Gamificacao para engajamento de educacao continuada")
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

    @PostMapping("/{id}/concluir-curso")
    @Operation(summary = "US Eduarda: processa a conclusao de um curso")
    public AlunoResponse concluirCurso(@PathVariable Long id, @Valid @RequestBody ConcluirCursoRequest request) {
        return service.concluirCurso(id, request);
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