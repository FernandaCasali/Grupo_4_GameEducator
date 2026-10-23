package org.example.grupo_4_gameeducator.notificacoes.api;

import org.example.grupo_4_gameeducator.notificacoes.application.NotificacaoAppService;
import org.example.grupo_4_gameeducator.notificacoes.application.RegistroNotificacao;
import org.example.grupo_4_gameeducator.notificacoes.application.SimuladorDeFalha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificacaoControllerTest {

    @Mock
    private NotificacaoAppService service;

    private SimuladorDeFalha simulador;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        simulador = new SimuladorDeFalha(false);
        mockMvc = MockMvcBuilders.standaloneSetup(new NotificacaoController(service, simulador)).build();
    }

    @Test
    void deveListarHistorico() throws Exception {
        when(service.listar()).thenReturn(List.of(new RegistroNotificacao(
                1L, "msg", RegistroNotificacao.Status.FALHOU, "indisponivel", Instant.now())));

        mockMvc.perform(get("/api/notificacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("FALHOU"));
    }

    @Test
    void deveReenviarFalhas() throws Exception {
        when(service.reenviarFalhas()).thenReturn(2);

        mockMvc.perform(post("/api/notificacoes/reenviar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recuperadas").value(2));
    }

    @Test
    void deveLigarSimulacaoDeFalha() throws Exception {
        mockMvc.perform(put("/api/notificacoes/simulacao-falha/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.falhaSimulada").value(true));

        assertTrue(simulador.ativa());
    }
}
