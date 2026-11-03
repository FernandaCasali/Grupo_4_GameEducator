package org.example.grupo_4_gameeducator.ranking.api;

import org.example.grupo_4_gameeducator.ranking.api.RankingDTOs.RankingItemResponse;
import org.example.grupo_4_gameeducator.ranking.application.RankingAppService;
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
class RankingControllerTest {

    @Mock
    private RankingAppService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RankingController(service)).build();
    }

    @Test
    void deveRetornarRanking() throws Exception {
        when(service.listar()).thenReturn(List.of(new RankingItemResponse(1, 7L, "Bia", 3, 10, 310)));

        mockMvc.perform(get("/api/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].posicao").value(1))
                .andExpect(jsonPath("$[0].nome").value("Bia"))
                .andExpect(jsonPath("$[0].pontuacao").value(310));
    }
}
