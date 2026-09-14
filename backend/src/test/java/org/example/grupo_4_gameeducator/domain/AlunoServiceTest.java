package org.example.grupo_4_gameeducator.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlunoServiceTest {

    @Test
    void naoDeveLiberarSeCursoEmAndamento() {
        var aluno = new Aluno("Fernanda");
        var curso = new Curso("Inteligência Artificial");
        curso.setStatus(StatusCurso.EM_ANDAMENTO);
        var service = new AlunoService();

        service.verificarElegibilidade(aluno, curso);

        assertEquals(0, aluno.getCursosDesbloqueados().size());
    }
}
