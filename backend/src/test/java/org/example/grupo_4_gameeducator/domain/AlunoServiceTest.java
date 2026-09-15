package org.example.grupo_4_gameeducator.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlunoServiceTest {

    private final AlunoService service = new AlunoService();

    @Test
    void deveLiberar3CursosQuandoMediaAcimaDe7() {
        var aluno = new Aluno("Carolina");
        var curso = new Curso("Logica de Programacao");
        curso.setStatus(StatusCurso.CONCLUIDO);
        service.processarDesbloqueio(aluno, curso, 8.5);
        assertEquals(3, aluno.getCursosDesbloqueados().size());
    }

    @Test
    void naoDeveLiberarQuandoMediaIgualOuMenorQue7() {
        var aluno = new Aluno("Carolina");
        var curso = new Curso("Logica de Programacao");
        curso.setStatus(StatusCurso.CONCLUIDO);
        service.processarDesbloqueio(aluno, curso, 6.9);
        assertEquals(0, aluno.getCursosDesbloqueados().size());
    }

    @Test
    void deveNotificarAlunoAposLiberacao() {
        var aluno = new Aluno("Carolina");
        var curso = new Curso("Logica de Programacao");
        curso.setStatus(StatusCurso.CONCLUIDO);
        service.processarDesbloqueio(aluno, curso, 9.0);
        assertTrue(aluno.foiNotificado());
    }

    @Test
    void naoDeveLiberarQuandoLimiteDoPlanoAtingido() {
        var aluno = new Aluno("Eduarda");
        aluno.setPlano(new Plano("Basico", 10));
        aluno.setTotalCursos(10);
        var curso = new Curso("Redes");
        curso.setStatus(StatusCurso.CONCLUIDO);
        service.processarDesbloqueio(aluno, curso, 8.0);
        assertEquals(0, aluno.getCursosDesbloqueados().size());
    }

    @Test
    void deveNotificarSobreLimiteAtingido() {
        var aluno = new Aluno("Eduarda");
        aluno.setPlano(new Plano("Basico", 10));
        aluno.setTotalCursos(10);
        var curso = new Curso("Redes");
        curso.setStatus(StatusCurso.CONCLUIDO);
        service.processarDesbloqueio(aluno, curso, 8.0);
        assertTrue(aluno.getNotificacoes().contains(Aluno.NOTIFICACAO_LIMITE_ATINGIDO));
    }

    @Test
    void deveLiberarQuandoAbaixoDoLimiteDoPlano() {
        var aluno = new Aluno("Eduarda");
        aluno.setPlano(new Plano("Basico", 10));
        aluno.setTotalCursos(5);
        var curso = new Curso("Redes");
        curso.setStatus(StatusCurso.CONCLUIDO);
        service.processarDesbloqueio(aluno, curso, 8.0);
        assertEquals(3, aluno.getCursosDesbloqueados().size());
    }

    // TDD1 Fernanda — Curso EM_ANDAMENTO nao deve liberar novos cursos,
    @Test
    void naoDeveLiberarSeCursoEmAndamento() {
        var aluno = new Aluno("Fernanda");
        var curso = new Curso("Inteligencia Artificial");
        curso.setStatus(StatusCurso.EM_ANDAMENTO);
        service.processarDesbloqueio(aluno, curso, 9.0);
        assertEquals(0, aluno.getCursosDesbloqueados().size());
    }

    // TDD2 Fernanda — Curso nao concluido nao deve gerar notificacao ao aluno.
    @Test
    void naoDeveNotificarSeCursoNaoConcluido() {
        var aluno = new Aluno("Fernanda");
        var curso = new Curso("Inteligencia Artificial");
        curso.setStatus(StatusCurso.EM_ANDAMENTO);
        service.processarDesbloqueio(aluno, curso, 9.0);
        assertFalse(aluno.foiNotificado());
    }

