package org.example.grupo_4_gameeducator.recomendacao.api;

import java.util.List;

public class RecomendacaoDTOs {

    private RecomendacaoDTOs() {
        // so agrupa records; nao deve ser instanciada (JaCoCo ignora construtor privado vazio)
    }

    public record RecomendacaoItem(String curso, String motivo) {}

    /** degradado=true indica que a IA esta indisponivel e a lista veio vazia (comportamento degradado aceito). */
    public record RecomendacaoResponse(List<RecomendacaoItem> recomendacoes, boolean degradado, String origem) {}
}
