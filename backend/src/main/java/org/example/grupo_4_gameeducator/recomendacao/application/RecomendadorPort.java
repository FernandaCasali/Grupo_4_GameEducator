package org.example.grupo_4_gameeducator.recomendacao.application;

import java.util.List;

/**
 * PORTA para o servico de IA (ML). O Core nao sabe se a implementacao e HTTP, fila ou regra local.
 * Deve lancar RecomendadorIndisponivelException quando o servico nao responde.
 */
public interface RecomendadorPort {

    List<Sugestao> recomendar(Long alunoId, List<String> cursosDoAluno);

    record Sugestao(String curso, String motivo) {}
}
