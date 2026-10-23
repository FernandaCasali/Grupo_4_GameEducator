package org.example.grupo_4_gameeducator.recomendacao.api;

import org.example.grupo_4_gameeducator.recomendacao.api.RecomendacaoDTOs.RecomendacaoItem;
import org.example.grupo_4_gameeducator.recomendacao.api.RecomendacaoDTOs.RecomendacaoResponse;
import org.example.grupo_4_gameeducator.recomendacao.application.RecomendacaoAppService;
import org.example.grupo_4_gameeducator.shared.web.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RecomendacaoControllerTest {

    @Mock
    private RecomendacaoAppService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RecomendacaoController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void deveRetornarRecomendacoes() throws Exception {
        when(service.recomendar(1L)).thenReturn(new RecomendacaoResponse(
                List.of(new RecomendacaoItem("Java Basico", "Proximo passo")), false, "IA"));

        mockMvc.perform(get("/api/alunos/1/recomendacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.degradado").value(false))
                .andExpect(jsonPath("$.recomendacoes[0].curso").value("Java Basico"));
    }

    @Test
    void deveRetornar200EmModoDegradadoQuandoIaForaDoAr() throws Exception {
        when(service.recomendar(1L)).thenReturn(new RecomendacaoResponse(List.of(), true, "FALLBACK_SEM_IA"));

        mockMvc.perform(get("/api/alunos/1/recomendacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.degradado").value(true))
                .andExpect(jsonPath("$.origem").value("FALLBACK_SEM_IA"));
    }

    @Test
    void deveRetornar400QuandoAlunoNaoExiste() throws Exception {
        when(service.recomendar(99L)).thenThrow(new IllegalArgumentException("Aluno nao encontrado: 99"));

        mockMvc.perform(get("/api/alunos/99/recomendacoes")).andExpect(status().isBadRequest());
    }
}
