package org.example.grupo_4_gameeducator.iot.api;

import org.example.grupo_4_gameeducator.iot.domain.TipoEventoIot;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IotDTOs {

    private IotDTOs() {
        // so agrupa records; nao deve ser instanciada (JaCoCo ignora construtor privado vazio)
    }

    /** Evento JA normalizado pelo gateway; o Core nao conhece o formato compacto do dispositivo. */
    public record EventoIotRequest(
            @NotBlank(message = "eventoId e obrigatorio") String eventoId,
            @NotBlank(message = "dispositivoId e obrigatorio") String dispositivoId,
            @NotBlank(message = "laboratorio e obrigatorio") String laboratorio,
            @NotNull(message = "tipo e obrigatorio") TipoEventoIot tipo,
            Long alunoId,
            @NotBlank(message = "ocorridoEm e obrigatorio") String ocorridoEm
    ) {}

    public record EventoIotResponse(
            String eventoId,
            String dispositivoId,
            String laboratorio,
            TipoEventoIot tipo,
            Long alunoId,
            String ocorridoEm,
            String recebidoEm
    ) {}

    /** status: REGISTRADO (novo) ou DUPLICADO (eventoId ja visto; sem efeito). */
    public record RegistroIotResponse(String eventoId, String status) {}
}
