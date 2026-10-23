package org.example.grupo_4_gameeducator.cursos.api;

/** Evento de dominio publicado quando um curso e concluido. Contrato publico do modulo cursos. */
public record CursoConcluidoEvent(
        Long alunoId,
        String alunoNome,
        String nomeCurso,
        double media,
        int cursosLiberados,
        boolean limiteAtingido
) {}
