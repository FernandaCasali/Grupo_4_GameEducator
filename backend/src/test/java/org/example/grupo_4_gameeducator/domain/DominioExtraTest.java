package org.example.grupo_4_gameeducator.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DominioExtraTest {

    // ===== Plano =====

    @Test
    void planoDeveExporNomeEMLimite() {
        var plano = new Plano("Premium", 20);
        assertEquals("Premium", plano.getNome());
        assertEquals(20, plano.getLimiteCursos());
    }

    // ===== Curso: construtores =====

    @Test
    void cursoDeveNascerComoNaoIniciadoNoConstrutorSimples() {
        var curso = new Curso("Java");
        assertEquals("Java", curso.getNome());
        assertEquals(StatusCurso.NAO_INICIADO, curso.getStatus());
    }

    @Test
    void cursoDeveNascerComStatusInformadoNoConstrutorCompleto() {
        var curso = new Curso("Java", StatusCurso.CONCLUIDO);
        assertEquals(StatusCurso.CONCLUIDO, curso.getStatus());
    }

    // ===== Curso: equals/hashCode (fecha o 0% de branch) =====

    @Test
    void cursosComMesmoNomeDevemSerIguaisEterOMesmoHash() {
        var c1 = new Curso("Java");
        var c2 = new Curso("Java", StatusCurso.CONCLUIDO);
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void cursosComNomesDiferentesDevemSerDiferentes() {
        assertNotEquals(new Curso("Java"), new Curso("Python"));
    }

    @Test
    void cursoDeveSerIgualASiMesmo() {
        var c1 = new Curso("Java");
        assertEquals(c1, c1);
    }

    @Test
    void cursoNaoDeveSerIgualANull() {
        assertNotEquals(null, new Curso("Java"));
    }

    @Test
    void cursoNaoDeveSerIgualAUmTipoDiferente() {
        assertNotEquals("Java", new Curso("Java"));
    }

    // ===== Aluno: metodos ainda nao cobertos =====

    @Test
    void alunoDeveRemoverMoedas() {
        var aluno = new Aluno("Carolina");
        aluno.adicionarMoedas(50);
        aluno.removerMoedas(20);
        assertEquals(30, aluno.getSaldoMoedas());
    }

    @Test
    void alunoDeveExporPlanoDefinido() {
        var aluno = new Aluno("Carolina");
        var plano = new Plano("Basico", 10);
        aluno.setPlano(plano);
        assertSame(plano, aluno.getPlano());
    }

    @Test
    void alunoDeveComecarSemNotificacaoENaoNotificado() {
        var aluno = new Aluno("Carolina");
        assertFalse(aluno.foiNotificado());
        assertTrue(aluno.getNotificacoes().isEmpty());
        assertTrue(aluno.getHistoricoMoedas().isEmpty());
        assertEquals(0, aluno.getTotalCursos());
    }
}