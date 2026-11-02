package org.example.grupo_4_gameeducator.iot.api;

import org.example.grupo_4_gameeducator.iot.api.IotDTOs.EventoIotRequest;
import org.example.grupo_4_gameeducator.iot.api.IotDTOs.EventoIotResponse;
import org.example.grupo_4_gameeducator.iot.api.IotDTOs.RegistroIotResponse;
import org.example.grupo_4_gameeducator.iot.application.IotAppService;
import org.example.grupo_4_gameeducator.iot.domain.TipoEventoIot;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class IotControllerTest {

    private static final String CORPO = "{\"eventoId\":\"e-1\",\"dispositivoId\":\"sensor-03\",\"laboratorio\":\"lab1\","
            + "\"tipo\":\"CONCLUSAO_ATIVIDADE\",\"alunoId\":7,\"ocorridoEm\":\"2026-11-02T10:00:00Z\"}";

    @Mock
    private IotAppService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new IotController(service))
                .setControllerAdvice(new ApiExceptionHandler()).build();
    }

    @Test
    void eventoNovoRetorna201() throws Exception {
        when(service.registrar(any(EventoIotRequest.class))).thenReturn(new RegistroIotResponse("e-1", "REGISTRADO"));

        mockMvc.perform(post("/api/iot/eventos").contentType(MediaType.APPLICATION_JSON).content(CORPO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REGISTRADO"));
    }

    @Test
    void eventoDuplicadoRetorna200() throws Exception {
        when(service.registrar(any(EventoIotRequest.class))).thenReturn(new RegistroIotResponse("e-1", "DUPLICADO"));

        mockMvc.perform(post("/api/iot/eventos").contentType(MediaType.APPLICATION_JSON).content(CORPO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DUPLICADO"));
    }

    @Test
    void eventoSemCamposObrigatoriosRetorna400() throws Exception {
        mockMvc.perform(post("/api/iot/eventos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listaOsEventosRecebidos() throws Exception {
        when(service.listar()).thenReturn(List.of(new EventoIotResponse("e-1", "sensor-03", "lab1",
                TipoEventoIot.PRESENCA, 7L, "2026-11-02T10:00:00Z", "2026-11-02T10:00:01Z")));

        mockMvc.perform(get("/api/iot/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventoId").value("e-1"))
                .andExpect(jsonPath("$[0].tipo").value("PRESENCA"));
    }
}
