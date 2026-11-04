package org.example.grupo_4_gameeducator.recomendacao.internal;

import org.example.grupo_4_gameeducator.recomendacao.application.RecomendadorIndisponivelException;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de contrato do ADAPTER contra um servidor HTTP falso (JDK), sem Docker e sem Python.
 * Tambem comprova a falha: porta fechada e HTTP 500 viram RecomendadorIndisponivelException.
 */
class IaServiceRecomendadorTest {

    private HttpServer servidor;

    @AfterEach
    void parar() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    private String subir(int status, String corpo) throws Exception {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/recomendacoes", troca -> {
            byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
            troca.getResponseHeaders().add("Content-Type", "application/json");
            troca.sendResponseHeaders(status, bytes.length);
            troca.getResponseBody().write(bytes);
            troca.close();
        });
        servidor.start();
        return "http://127.0.0.1:" + servidor.getAddress().getPort();
    }

    @Test
    void deveInterpretarRespostaDoIaService() throws Exception {
        String url = subir(200, "{\"recomendacoes\":[{\"curso\":\"Java Basico\",\"motivo\":\"Proximo passo\"}],\"modelo\":\"regras-trilha-v1\"}");

        var sugestoes = new IaServiceRecomendador(url, 1000).recomendar(1L, List.of("Logica"));

        assertEquals(1, sugestoes.size());
        assertEquals("Java Basico", sugestoes.get(0).curso());
        assertEquals("Proximo passo", sugestoes.get(0).motivo());
    }

    @Test
    void deveLancarIndisponivelQuandoServicoRetornaErro500() throws Exception {
        String url = subir(500, "{\"detail\":\"erro\"}");

        assertThrows(RecomendadorIndisponivelException.class,
                () -> new IaServiceRecomendador(url, 1000).recomendar(1L, List.of()));
    }

    @Test
    void deveLancarIndisponivelQuandoServicoEstaForaDoAr() {
        // porta 1: nada escutando = servico Python interrompido
        var adapter = new IaServiceRecomendador("http://127.0.0.1:1", 500);

        assertThrows(RecomendadorIndisponivelException.class, () -> adapter.recomendar(1L, List.of()));
    }

    @Test
    void deveRetornarListaVaziaQuandoIaServiceRespondeSemCorpo() throws Exception {
        String url = subir(200, "");

        var sugestoes = new IaServiceRecomendador(url, 1000).recomendar(1L, List.of("Logica"));

        assertTrue(sugestoes.isEmpty());
    }

    @Test
    void deveRetornarListaVaziaQuandoRespostaNaoTrazRecomendacoes() throws Exception {
        String url = subir(200, "{\"modelo\":\"regras-trilha-v1\"}");

        var sugestoes = new IaServiceRecomendador(url, 1000).recomendar(1L, List.of("Logica"));

        assertTrue(sugestoes.isEmpty());
    }
}
