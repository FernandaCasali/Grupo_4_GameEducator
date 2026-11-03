package org.example.grupo_4_gameeducator.iot.application;

import org.example.grupo_4_gameeducator.iot.api.IotDTOs.EventoIotResponse;

import java.util.List;

/** Porta de persistencia dos eventos IoT. O adapter atual e em memoria (laboratorio). */
public interface EventoIotRepositorio {

    /** Grava o evento somente se o eventoId ainda nao existe. @return true se gravou; false se era duplicado. */
    boolean salvarSeNovo(EventoIotResponse evento);

    List<EventoIotResponse> listar();
}
