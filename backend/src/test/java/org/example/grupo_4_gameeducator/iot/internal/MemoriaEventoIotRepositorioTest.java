package org.example.grupo_4_gameeducator.iot.internal;

import org.example.grupo_4_gameeducator.iot.api.IotDTOs.EventoIotResponse;
import org.example.grupo_4_gameeducator.iot.domain.TipoEventoIot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemoriaEventoIotRepositorioTest {

    private EventoIotResponse evento(String id) {
        return new EventoIotResponse(id, "sensor-03", "lab1", TipoEventoIot.PRESENCA, 1L,
                "2026-11-02T10:00:00Z", "2026-11-02T10:00:01Z");
    }

    @Test
    void grava_somente_se_o_evento_ainda_nao_existe() {
        var repo = new MemoriaEventoIotRepositorio();

        assertTrue(repo.salvarSeNovo(evento("e-1")));
        assertFalse(repo.salvarSeNovo(evento("e-1")));
        assertEquals(1, repo.listar().size());
    }

    @Test
    void lista_na_ordem_de_chegada() {
        var repo = new MemoriaEventoIotRepositorio();
        repo.salvarSeNovo(evento("e-2"));
        repo.salvarSeNovo(evento("e-1"));

        assertEquals("e-2", repo.listar().get(0).eventoId());
        assertEquals("e-1", repo.listar().get(1).eventoId());
    }
}
