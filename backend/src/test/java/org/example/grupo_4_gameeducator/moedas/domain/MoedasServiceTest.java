package org.example.grupo_4_gameeducator.moedas.domain;

import org.example.grupo_4_gameeducator.aluno.domain.Aluno;
import org.example.grupo_4_gameeducator.aluno.domain.Curso;
import org.example.grupo_4_gameeducator.aluno.domain.Plano;
import org.example.grupo_4_gameeducator.aluno.domain.StatusCurso;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoedasServiceTest {

    // ==========================================================
    // Testes complementares — moedas, premium e conversao cripto
    // ==========================================================

    // --- creditarMoedasPorEngajamento ---

    @Test
    void deveCreditarMoedasEregistrarHistorico() {
        var aluno = new Aluno("Carolina");
        var service = new MoedasService();
        service.creditarMoedasPorEngajamento(aluno, 10, "Conclusao de curso");
        assertEquals(10, aluno.getSaldoMoedas());
        assertEquals(1, aluno.getHistoricoMoedas().size());
        assertTrue(aluno.getHistoricoMoedas().get(0).contains("+10"));
    }

    @Test
    void deveLancarExcecaoAoCreditarMoedasZero() {
        var aluno = new Aluno("Carolina");
        var service = new MoedasService();
        assertThrows(IllegalArgumentException.class,
                () -> service.creditarMoedasPorEngajamento(aluno, 0, "qualquer"));
    }

    @Test
    void deveLancarExcecaoAoCreditarMoedasNegativas() {
        var aluno = new Aluno("Carolina");
        var service = new MoedasService();
        assertThrows(IllegalArgumentException.class,
                () -> service.creditarMoedasPorEngajamento(aluno, -5, "qualquer"));
    }

    // --- isPremium ---

    @Test
    void deveSerPremiumComDozeCursos() {
        var aluno = new Aluno("Fernanda");
        aluno.setTotalCursos(12);
        assertTrue(new MoedasService().isPremium(aluno));
    }

    @Test
    void naoDeveSerPremiumComMenosDeDozeCursos() {
        var aluno = new Aluno("Fernanda");
        aluno.setTotalCursos(11);
        assertFalse(new MoedasService().isPremium(aluno));
    }

    // --- converterMoedasParaCripto ---

    @Test
    void deveConverterMoedasParaCriptoQuandoPremium() {
        var aluno = new Aluno("Fernanda");
        aluno.setTotalCursos(12);
        aluno.adicionarMoedas(100);
        var service = new MoedasService();
        double cripto = service.converterMoedasParaCripto(aluno, 50, 0.01);
        assertEquals(0.5, cripto);
        assertEquals(50, aluno.getSaldoMoedas());
        assertEquals(1, aluno.getHistoricoMoedas().size());
        assertTrue(aluno.getHistoricoMoedas().get(0).contains("Conversao para cripto"));
    }

    @Test
    void naoDeveConverterQuandoNaoPremium() {
        var aluno = new Aluno("Carolina");
        aluno.setTotalCursos(5);
        aluno.adicionarMoedas(100);
        var service = new MoedasService();
        assertThrows(IllegalStateException.class,
                () -> service.converterMoedasParaCripto(aluno, 10, 0.01));
    }

    @Test
    void naoDeveConverterComQuantidadeZeroOuNegativa() {
        var aluno = new Aluno("Fernanda");
        aluno.setTotalCursos(12);
        aluno.adicionarMoedas(50);
        var service = new MoedasService();
        assertThrows(IllegalArgumentException.class,
                () -> service.converterMoedasParaCripto(aluno, 0, 0.01));
    }

    @Test
    void naoDeveConverterQuandoQuantidadeMaiorQueSaldo() {
        var aluno = new Aluno("Fernanda");
        aluno.setTotalCursos(12);
        aluno.adicionarMoedas(50);
        var service = new MoedasService();
        assertThrows(IllegalArgumentException.class,
                () -> service.converterMoedasParaCripto(aluno, 100, 0.01));
    }

}