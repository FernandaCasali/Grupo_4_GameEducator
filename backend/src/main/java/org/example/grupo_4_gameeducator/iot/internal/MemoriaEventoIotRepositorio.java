package org.example.grupo_4_gameeducator.iot.internal;

import org.example.grupo_4_gameeducator.iot.api.IotDTOs.EventoIotResponse;
import org.example.grupo_4_gameeducator.iot.application.EventoIotRepositorio;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Adapter em memoria (laboratorio). Em producao: tabela com chave unica em eventoId. */
@Component
public class MemoriaEventoIotRepositorio implements EventoIotRepositorio {

    private final Map<String, EventoIotResponse> eventos = new LinkedHashMap<>();

    @Override
    public synchronized boolean salvarSeNovo(EventoIotResponse evento) {
        return eventos.putIfAbsent(evento.eventoId(), evento) == null;
    }

    @Override
    public synchronized List<EventoIotResponse> listar() {
        return new ArrayList<>(eventos.values());
    }
}
