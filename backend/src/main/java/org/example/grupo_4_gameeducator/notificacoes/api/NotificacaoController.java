package org.example.grupo_4_gameeducator.notificacoes.api;

import org.example.grupo_4_gameeducator.notificacoes.application.NotificacaoAppService;
import org.example.grupo_4_gameeducator.notificacoes.application.RegistroNotificacao;
import org.example.grupo_4_gameeducator.notificacoes.application.SimuladorDeFalha;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificacoes")
@Tag(name = "Notificacoes", description = "RF05 - Notificacoes assincronas disparadas por eventos")
@CrossOrigin(origins = "*")
public class NotificacaoController {

    private final NotificacaoAppService service;
    private final SimuladorDeFalha simulador;

    public NotificacaoController(NotificacaoAppService service, SimuladorDeFalha simulador) {
        this.service = service;
        this.simulador = simulador;
    }

    @GetMapping
    @Operation(summary = "Historico de notificacoes (ENVIADA / FALHOU)")
    public List<RegistroNotificacao> listar() {
        return service.listar();
    }

    @PostMapping("/reenviar")
    @Operation(summary = "Reprocessa as notificacoes que falharam")
    public Map<String, Integer> reenviar() {
        return Map.of("recuperadas", service.reenviarFalhas());
    }

    @PutMapping("/simulacao-falha/{ativa}")
    @Operation(summary = "LABORATORIO D4: liga/desliga a falha simulada do consumidor")
    public Map<String, Boolean> simularFalha(@PathVariable boolean ativa) {
        simulador.definir(ativa);
        return Map.of("falhaSimulada", simulador.ativa());
    }
}
