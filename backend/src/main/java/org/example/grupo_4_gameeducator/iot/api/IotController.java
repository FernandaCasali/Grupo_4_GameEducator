package org.example.grupo_4_gameeducator.iot.api;

import org.example.grupo_4_gameeducator.iot.api.IotDTOs.EventoIotRequest;
import org.example.grupo_4_gameeducator.iot.api.IotDTOs.EventoIotResponse;
import org.example.grupo_4_gameeducator.iot.api.IotDTOs.RegistroIotResponse;
import org.example.grupo_4_gameeducator.iot.application.IotAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/iot/eventos")
@Tag(name = "IoT", description = "RF06 - Eventos de dispositivos dos laboratorios (via gateway MQTT)")
@CrossOrigin(origins = "*")
public class IotController {

    private final IotAppService service;

    public IotController(IotAppService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registra um evento IoT (201 novo; 200 se o eventoId ja foi recebido)")
    public ResponseEntity<RegistroIotResponse> registrar(@Valid @RequestBody EventoIotRequest request) {
        RegistroIotResponse resposta = service.registrar(request);
        HttpStatus status = IotAppService.REGISTRADO.equals(resposta.status()) ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(resposta);
    }

    @GetMapping
    @Operation(summary = "Lista os eventos IoT recebidos")
    public List<EventoIotResponse> listar() {
        return service.listar();
    }
}
