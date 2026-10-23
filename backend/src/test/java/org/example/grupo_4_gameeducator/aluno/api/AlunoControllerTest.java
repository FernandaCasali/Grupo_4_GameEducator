package org.example.grupo_4_gameeducator.aluno.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.*;
import org.example.grupo_4_gameeducator.aluno.application.AlunoAppService;
import org.example.grupo_4_gameeducator.shared.web.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AlunoControllerTest {

    @Mock
    private AlunoAppService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AlunoController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    private AlunoResponse respostaExemplo() {
        return new AlunoResponse(
                1L, "Carolina", "Basico", 0, false, 0, false,
                List.of(), Set.of(), List.of()
        );
    }

    @Test
    void deveCriarAlunoRetornando201() throws Exception {
        when(service.criar(any())).thenReturn(respostaExemplo());
        var body = objectMapper.writeValueAsString(new CriarAlunoRequest("Carolina", "Basico", 10));

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
    void deveRetornar400QuandoServiceLancaIllegalArgument() throws Exception {
        when(service.buscarPorId(99L))
                .thenThrow(new IllegalArgumentException("Aluno nao encontrado: 99"));

        mockMvc.perform(get("/api/alunos/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Aluno nao encontrado: 99"));
    }
}
