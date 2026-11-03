package org.example.grupo_4_gameeducator.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/** Habilita @Async: consumidores de eventos rodam fora da thread da requisicao HTTP. */
@Configuration
@EnableAsync
public class AsyncConfig {
}
