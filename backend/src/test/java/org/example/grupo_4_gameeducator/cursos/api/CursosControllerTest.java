package org.example.grupo_4_gameeducator.cursos.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.cursos.api.CursosDTOs.ConcluirCursoRequest;
import org.example.grupo_4_gameeducator.cursos.application.CursosAppService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CursosControllerTest {

    @Mock
    private CursosAppService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CursosController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void deveConcluirCurso() throws Exception {
        var resposta = new AlunoResponse(1L, "Carolina", "Basico", 0, false, 0, false, List.of(), Set.of(), List.of());
        when(service.concluirCurso(eq(1L), any())).thenReturn(resposta);
        var body = objectMapper.writeValueAsString(new ConcluirCursoRequest("Logica", 8.5));

        mockMvc.perform(post("/api/alunos/1/concluir-curso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}
