package org.example.grupo_4_gameeducator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.grupo_4_gameeducator.dto.AlunoDTOs.*;
import org.example.grupo_4_gameeducator.service.AlunoAppService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlunoController.class)
class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AlunoAppService service;

    private AlunoResponse respostaExemplo() {
        return new AlunoResponse(
                1L, "Carolina", "Basico", 0, false, 0, false,
                List.of(), Set.of(), List.of()
        );
    }

    @Test
    void deveCriarAlunoRetornando201() throws Exception {
        when(service.criar(any())).thenReturn(respostaExemplo());
        var body = objectMapper.writeValueAsString(
                new CriarAlunoRequest("Carolina", "Basico", 10));

        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Carolina"));
    }

    @Test
    void deveListarAlunos() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(respostaExemplo()));

        mockMvc.perform(get("/api/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Carolina"));
    }

    @Test
    void deveBuscarAlunoPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(respostaExemplo());

        mockMvc.perform(get("/api/alunos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveConcluirCurso() throws Exception {
        when(service.concluirCurso(eq(1L), any())).thenReturn(respostaExemplo());
        var body = objectMapper.writeValueAsString(
                new ConcluirCursoRequest("Logica", 8.5));

        mockMvc.perform(post("/api/alunos/1/concluir-curso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void deveCreditarMoedas() throws Exception {
        when(service.creditarMoedas(eq(1L), any())).thenReturn(respostaExemplo());
        var body = objectMapper.writeValueAsString(
                new CreditarMoedasRequest(10, "Forum"));

        mockMvc.perform(post("/api/alunos/1/moedas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void deveConverterCripto() throws Exception {
        when(service.converterCripto(eq(1L), any())).thenReturn(respostaExemplo());
        var body = objectMapper.writeValueAsString(
                new ConverterCriptoRequest(10, 0.01));

        mockMvc.perform(post("/api/alunos/1/converter-cripto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    // ==========================================================
    // Testes que exercitam o ApiExceptionHandler
    // ==========================================================

    @Test
    void deveRetornar400QuandoServiceLancaIllegalArgument() throws Exception {
        when(service.buscarPorId(99L))
                .thenThrow(new IllegalArgumentException("Aluno nao encontrado: 99"));

        mockMvc.perform(get("/api/alunos/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Aluno nao encontrado: 99"));
    }

    @Test
    void deveRetornar409QuandoServiceLancaIllegalState() throws Exception {
        when(service.converterCripto(eq(1L), any()))
                .thenThrow(new IllegalStateException("Somente alunos Premium"));
        var body = objectMapper.writeValueAsString(
                new ConverterCriptoRequest(10, 0.01));

        mockMvc.perform(post("/api/alunos/1/converter-cripto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value("Somente alunos Premium"));
    }
}