package org.example.grupo_4_gameeducator.conquistas.api;

import org.example.grupo_4_gameeducator.conquistas.api.ConquistasDTOs.BadgeResponse;
import org.example.grupo_4_gameeducator.conquistas.api.ConquistasDTOs.ConquistasResponse;
import org.example.grupo_4_gameeducator.conquistas.application.ConquistasAppService;
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
class ConquistasControllerTest {

    @Mock
    private ConquistasAppService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ConquistasController(service)).build();
    }

    @Test
    void deveRetornarBadgesDoAluno() throws Exception {
        when(service.consultar(7L)).thenReturn(new ConquistasResponse(7L, "Bia",
                List.of(new BadgeResponse("PRIMEIRO_CURSO", "Concluiu o primeiro curso"))));

        mockMvc.perform(get("/api/alunos/7/conquistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Bia"))
                .andExpect(jsonPath("$.badges[0].codigo").value("PRIMEIRO_CURSO"));
    }
}
