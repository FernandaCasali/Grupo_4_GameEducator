package org.example.grupo_4_gameeducator.dto;

import org.example.grupo_4_gameeducator.dto.AlunoDTOs.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AlunoDTOsTest {

    @Test
    void criarAlunoRequestDeveExporTodosOsCampos() {
        var r = new CriarAlunoRequest("Ana", "Basico", 10);
        assertEquals("Ana", r.nome());
        assertEquals("Basico", r.planoNome());
        assertEquals(10, r.planoLimiteCursos());
    }

    @Test
    void concluirCursoRequestDeveExporTodosOsCampos() {
        var r = new ConcluirCursoRequest("Java", 8.5);
        assertEquals("Java", r.nomeCurso());
        assertEquals(8.5, r.media());
    }

    @Test
    void creditarMoedasRequestDeveExporTodosOsCampos() {
        var r = new CreditarMoedasRequest(10, "forum");
        assertEquals(10, r.quantidade());
        assertEquals("forum", r.motivo());
    }

    @Test
    void converterCriptoRequestDeveExporTodosOsCampos() {
        var r = new ConverterCriptoRequest(5, 0.02);
        assertEquals(5, r.quantidadeMoedas());
        assertEquals(0.02, r.taxaCambio());
    }

    @Test
    void alunoResponseDeveExporTodosOsCampos() {
        var r = new AlunoResponse(
                1L, "Ana", "Basico", 3, false, 10, true,
                List.of("Java"), Set.of("LIMITE_ATINGIDO"), List.of("forum: +10 moeda(s)")
        );
        assertEquals(1L, r.id());
        assertEquals("Ana", r.nome());
        assertEquals("Basico", r.planoNome());
        assertEquals(3, r.totalCursos());
        assertFalse(r.premium());
        assertEquals(10, r.saldoMoedas());
        assertTrue(r.notificado());
        assertEquals(List.of("Java"), r.cursosDesbloqueados());
        assertTrue(r.notificacoes().contains("LIMITE_ATINGIDO"));
        assertEquals(1, r.historicoMoedas().size());
    }
}