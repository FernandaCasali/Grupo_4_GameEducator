package org.example.grupo_4_gameeducator.recomendacao.internal;

import org.example.grupo_4_gameeducator.recomendacao.application.RecomendadorIndisponivelException;
import org.example.grupo_4_gameeducator.recomendacao.application.RecomendadorPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/** ADAPTER HTTP/JSON para o ia-service (FastAPI). Timeouts curtos: a IA nunca pode travar o Core. */
@Component
public class IaServiceRecomendador implements RecomendadorPort {

    private final RestClient client;

    public IaServiceRecomendador(
            @Value("${ia.service.url:http://localhost:8000}") String baseUrl,
            @Value("${ia.service.timeout-ms:1500}") int timeoutMs) {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_1_1)
                        .connectTimeout(Duration.ofMillis(timeoutMs))
                        .build());
        factory.setReadTimeout(Duration.ofMillis(timeoutMs));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    @Override
    public List<Sugestao> recomendar(Long alunoId, List<String> cursosDoAluno) {
        try {
            IaResposta resposta = client.post()
                    .uri("/recomendacoes")
                    .body(Map.of("alunoId", alunoId, "cursosConcluidos", cursosDoAluno))
                    .retrieve()
                    .body(IaResposta.class);
            if (resposta == null || resposta.recomendacoes() == null) {
                return List.of();
            }
            return resposta.recomendacoes().stream()
                    .map(i -> new Sugestao(i.curso(), i.motivo()))
                    .toList();
        } catch (RestClientException ex) {
            throw new RecomendadorIndisponivelException("ia-service indisponivel: " + ex.getMessage(), ex);
        }
    }

    public record IaResposta(List<IaItem> recomendacoes, String modelo) {}

    public record IaItem(String curso, String motivo) {}
}
