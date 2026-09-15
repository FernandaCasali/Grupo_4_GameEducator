package org.example.grupo_4_gameeducator.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlunoServiceTest {

    // TDD1 Fernanda — Aluno com media acima de 7 em curso concluido deve desbloquear 3 cursos bonus.
    // Verifica que o metodo processarDesbloqueio libera exatamente 3 cursos quando a media e superior a 7.
    @Test
    void deveLiberar3CursosQuandoMediaAcimaDe7() {
        // Fernanda - Green tests
        var aluno = new Aluno("Fernanda");
        var curso = new Curso("Lógica de Programação");
        curso.setStatus(StatusCurso.CONCLUIDO);
        var service = new AlunoService();

        // Fernanda - Blue tests
        service.processarDesbloqueio(aluno, curso, 8.5);

        // Fernanda - Red tests
        assertEquals(3, aluno.getCursosDesbloqueados().size());
    }

    // TDD2 Fernanda — Aluno com media igual ou menor que 7 nao deve desbloquear nenhum curso.
    // Verifica que o metodo processarDesbloqueio nao libera cursos quando a media nao atinge o minimo.
    @Test
    void naoDeveLiberarQuandoMediaIgualOuMenorQue7() {
        // Cria aluno e curso com status CONCLUIDO
        var aluno = new Aluno("Fernanda");
        var curso = new Curso("Lógica de Programação");
        curso.setStatus(StatusCurso.CONCLUIDO);
        // Processa desbloqueio com media 6.9 (abaixo de 7)
        var service = new AlunoService();
        service.processarDesbloqueio(aluno, curso, 6.9);
        // Nao deve ter desbloqueado nenhum curso
        assertEquals(0, aluno.getCursosDesbloqueados().size());
    }

    // TDD3 Fernanda — Aluno deve ser notificado apos liberacao de cursos bonus.
    // Verifica que o aluno recebe notificacao quando cursos sao desbloqueados com media acima de 7.
    @Test
    void deveNotificarAlunoAposLiberacao() {
        // Cria aluno e curso com status CONCLUIDO
        var aluno = new Aluno("Fernanda");
        var curso = new Curso("Lógica de Programação");
        curso.setStatus(StatusCurso.CONCLUIDO);
        // Processa desbloqueio com media 9.0 (acima de 7)
        var service = new AlunoService();
        service.processarDesbloqueio(aluno, curso, 9.0);
        // Aluno deve ter sido notificado sobre os cursos desbloqueados
        assertTrue(aluno.foiNotificado());
    }

    //TDD -  Eduarda
    @Test
    public void naoDeveLiberarSeCursoEmAndamento() {
        // Eduarda - Green tests
        var aluno = new Aluno("Eduarda");
        var curso = new Curso("Inteligência Artificial");
        curso.setStatus(StatusCurso.EM_ANDAMENTO);
        var service = new AlunoService();

        // Eduarda - Blue tests
        service.verificarElegibilidade(aluno, curso);

        // Eduarda - Red tests
        assertEquals(0, aluno.getCursosDesbloqueados().size());
    }

    @Test
    public void naoDeveNotificarSeCursoNaoConcluido() {
        // Eduarda - Green tests
        var aluno = new Aluno("Eduarda");
        var curso = new Curso("Inteligência Artificial");
        curso.setStatus(StatusCurso.EM_ANDAMENTO);
        var service = new AlunoService();

        // Eduarda - Blue tests
        service.verificarElegibilidade(aluno, curso);

        // Eduarda - Red tests
        assertFalse(aluno.foiNotificado());
    }

    @Test
    public void deveRetornarElegibilidadeFalsaParaCursoEmAndamento() {
        // Eduarda - Green tests
        var aluno = new Aluno("Eduarda");
        var curso = new Curso("Inteligência Artificial");
        curso.setStatus(StatusCurso.EM_ANDAMENTO);
        var service = new AlunoService();

        // Eduarda - Blue tests
        boolean elegivel = service.verificarElegibilidade(aluno, curso);

        // Eduarda - Red tests
        assertFalse(elegivel);
    }
}