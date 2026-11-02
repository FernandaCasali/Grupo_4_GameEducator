package org.example.grupo_4_gameeducator.iot.application;

import org.example.grupo_4_gameeducator.iot.api.IotDTOs.EventoIotRequest;
import org.example.grupo_4_gameeducator.iot.api.IotDTOs.EventoIotResponse;
import org.example.grupo_4_gameeducator.iot.api.IotDTOs.RegistroIotResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * RF06 - recebe eventos IoT ja normalizados pelo gateway. Idempotente por eventoId:
 * MQTT com QoS 1 entrega "pelo menos uma vez", entao duplicatas sao esperadas e inofensivas.
 */
@Service
public class IotAppService {

    public static final String REGISTRADO = "REGISTRADO";
    public static final String DUPLICADO = "DUPLICADO";

    private final EventoIotRepositorio repositorio;

    public IotAppService(EventoIotRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public RegistroIotResponse registrar(EventoIotRequest req) {
        EventoIotResponse evento = new EventoIotResponse(
                req.eventoId().trim(), req.dispositivoId(), req.laboratorio(), req.tipo(),
                req.alunoId(), req.ocorridoEm(), Instant.now().toString());
        boolean novo = repositorio.salvarSeNovo(evento);
        return new RegistroIotResponse(evento.eventoId(), novo ? REGISTRADO : DUPLICADO);
    }

    public List<EventoIotResponse> listar() {
        return repositorio.listar();
    }
}
