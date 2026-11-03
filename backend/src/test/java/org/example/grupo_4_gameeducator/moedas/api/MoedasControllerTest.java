package org.example.grupo_4_gameeducator.moedas.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.moedas.api.MoedasDTOs.ConverterCriptoRequest;
import org.example.grupo_4_gameeducator.moedas.api.MoedasDTOs.CreditarMoedasRequest;
import org.example.grupo_4_gameeducator.moedas.application.MoedasAppService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MoedasControllerTest {

    @Mock
    private MoedasAppService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MoedasController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    private AlunoResponse respostaExemplo() {
        return new AlunoResponse(1L, "Carolina", "Basico", 0, false, 0, false, List.of(), Set.of(), List.of());
    }

    @Test
    void deveCreditarMoedas() throws Exception {
        when(service.creditarMoedas(eq(1L), any())).thenReturn(respostaExemplo());
        var body = objectMapper.writeValueAsString(new CreditarMoedasRequest(10, "Forum"));

        mockMvc.perform(post("/api/alunos/1/moedas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void deveConverterCripto() throws Exception {
        when(service.converterCripto(eq(1L), any())).thenReturn(respostaExemplo());
        var body = objectMapper.writeValueAsString(new ConverterCriptoRequest(10, 0.01));

        mockMvc.perform(post("/api/alunos/1/converter-cripto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar409QuandoServiceLancaIllegalState() throws Exception {
        when(service.converterCripto(eq(1L), any()))
                .thenThrow(new IllegalStateException("Somente alunos Premium"));
        var body = objectMapper.writeValueAsString(new ConverterCriptoRequest(10, 0.01));

        mockMvc.perform(post("/api/alunos/1/converter-cripto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value("Somente alunos Premium"));
    }
}
