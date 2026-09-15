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
        // Fernanda - Green tests
        var aluno = new Aluno("Fernanda");
        var curso = new Curso("Lógica de Programação");
        curso.setStatus(StatusCurso.CONCLUIDO);
        var service = new AlunoService();

        // Fernanda - Blue tests
        service.processarDesbloqueio(aluno, curso, 6.9);

        // Fernanda - Red tests
        assertEquals(0, aluno.getCursosDesbloqueados().size());
    }

    // TDD3 Fernanda — Aluno deve ser notificado apos liberacao de cursos bonus.
    // Verifica que o aluno recebe notificacao quando cursos sao desbloqueados com media acima de 7.
    @Test
    void deveNotificarAlunoAposLiberacao() {
        // Fernanda - Green tests
        var aluno = new Aluno("Fernanda");
        var curso = new Curso("Lógica de Programação");
        curso.setStatus(StatusCurso.CONCLUIDO);
        var service = new AlunoService();

        // Fernanda - Blue tests
        service.processarDesbloqueio(aluno, curso, 9.0);

        // Fernanda - Red tests
        assertTrue(aluno.foiNotificado());
    }

    //------------------------------------------------------------------------------------------

    // TDD1 Eduarda — Aluno com curso em andamento nao deve ter cursos desbloqueados, mesmo com media alta.
    // Verifica que processarDesbloqueio respeita a checagem de elegibilidade para cursos EM_ANDAMENTO.
    @Test
    public void naoDeveLiberarSeCursoEmAndamento() {
        // Cria aluno e curso com status EM_ANDAMENTO
        var aluno = new Aluno("Eduarda");
        var curso = new Curso("Inteligência Artificial");
        curso.setStatus(StatusCurso.EM_ANDAMENTO);
        var service = new AlunoService();
        // Tenta processar desbloqueio mesmo com media acima do minimo
        service.processarDesbloqueio(aluno, curso, 9.0);
        // Nao deve ter desbloqueado nenhum curso, pois o curso nao foi concluido
        assertEquals(0, aluno.getCursosDesbloqueados().size());
    }

    // TDD2 Eduarda — Aluno nao deve ser notificado se o curso nao foi concluido, mesmo com media alta.
    // Verifica que processarDesbloqueio nao notifica quando o curso esta EM_ANDAMENTO.
    @Test
    public void naoDeveNotificarSeCursoNaoConcluido() {
        // Cria aluno e curso com status EM_ANDAMENTO
        var aluno = new Aluno("Eduarda");
        var curso = new Curso("Inteligência Artificial");
        curso.setStatus(StatusCurso.EM_ANDAMENTO);
        var service = new AlunoService();
        // Tenta processar desbloqueio mesmo com media acima do minimo
        service.processarDesbloqueio(aluno, curso, 9.0);
        // Nao deve ter notificado o aluno
        assertFalse(aluno.foiNotificado());
    }

    // TDD3 Eduarda — Elegibilidade deve retornar falso para curso em andamento.
    // Verifica que o metodo verificarElegibilidade retorna false quando o curso nao esta concluido.
    @Test
    public void deveRetornarElegibilidadeFalsaParaCursoEmAndamento() {
        // Cria aluno e curso com status EM_ANDAMENTO
        var aluno = new Aluno("Eduarda");
        var curso = new Curso("Inteligência Artificial");
        curso.setStatus(StatusCurso.EM_ANDAMENTO);
        var service = new AlunoService();
        // Verifica elegibilidade do curso ainda em andamento
        boolean elegivel = service.verificarElegibilidade(aluno, curso);
        // Deve retornar falso
        assertFalse(elegivel);
    }

    //-------------------------------------------------------------------------------------

    // Carolina

}