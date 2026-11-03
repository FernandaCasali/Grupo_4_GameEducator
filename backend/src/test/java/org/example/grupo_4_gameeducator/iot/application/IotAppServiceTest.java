package org.example.grupo_4_gameeducator.iot.application;

import org.example.grupo_4_gameeducator.iot.api.IotDTOs.EventoIotRequest;
import org.example.grupo_4_gameeducator.iot.domain.TipoEventoIot;
import org.example.grupo_4_gameeducator.iot.internal.MemoriaEventoIotRepositorio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Casos de uso rodando com o adapter em memoria: sem Spring, sem banco, sem broker. */
class IotAppServiceTest {

    private final IotAppService service = new IotAppService(new MemoriaEventoIotRepositorio());

    private EventoIotRequest req(String id, TipoEventoIot tipo) {
        return new EventoIotRequest(id, "sensor-03", "lab1", tipo, 1L, "2026-11-02T10:00:00Z");
    }

    @Test
    void primeiroEnvioEhRegistrado() {
        var resposta = service.registrar(req("e-1", TipoEventoIot.PRESENCA));

        assertEquals("REGISTRADO", resposta.status());
        assertEquals("e-1", resposta.eventoId());
        assertEquals(1, service.listar().size());
    }

    @Test
    void mesmoEventoIdEhDuplicadoESemEfeito() {
        service.registrar(req("e-1", TipoEventoIot.CONCLUSAO_ATIVIDADE));

        var repetido = service.registrar(req("e-1", TipoEventoIot.CONCLUSAO_ATIVIDADE));

        assertEquals("DUPLICADO", repetido.status());
        assertEquals(1, service.listar().size());
    }

    @Test
    void eventosDiferentesSaoTodosRegistrados() {
        service.registrar(req("e-1", TipoEventoIot.PRESENCA));
        service.registrar(req("e-2", TipoEventoIot.INICIO_ATIVIDADE));
        service.registrar(req("e-3", TipoEventoIot.CONCLUSAO_ATIVIDADE));

        assertEquals(3, service.listar().size());
    }

    @Test
    void eventoIdComEspacosEhNormalizado() {
        service.registrar(req(" e-1 ", TipoEventoIot.PRESENCA));

        assertEquals("DUPLICADO", service.registrar(req("e-1", TipoEventoIot.PRESENCA)).status());
    }
}
