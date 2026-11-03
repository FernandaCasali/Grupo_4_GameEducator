package org.example.grupo_4_gameeducator.ranking.application;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.aluno.api.AlunoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingAppServiceTest {

    @Mock
    private AlunoGateway alunos;

    @InjectMocks
    private RankingAppService service;

    private AlunoResponse aluno(long id, String nome, int cursos, int moedas) {
        return new AlunoResponse(id, nome, "Basico", cursos, false, moedas, false, List.of(), Set.of(), List.of());
    }

    @Test
    void deveOrdenarPorPontuacaoDecrescente() {
        when(alunos.listarTodos()).thenReturn(List.of(
                aluno(1, "Ana", 0, 50),     // 50
                aluno(2, "Bia", 3, 10),     // 310
                aluno(3, "Caio", 1, 120))); // 220

        var ranking = service.listar();

        assertEquals(List.of("Bia", "Caio", "Ana"), ranking.stream().map(r -> r.nome()).toList());
        assertEquals(List.of(1, 2, 3), ranking.stream().map(r -> r.posicao()).toList());
        assertEquals(310, ranking.get(0).pontuacao());
    }

    @Test
    void deveDesempatarPorNome() {
        when(alunos.listarTodos()).thenReturn(List.of(
                aluno(1, "Zeca", 1, 0),
                aluno(2, "Ana", 1, 0)));

        var ranking = service.listar();

        assertEquals("Ana", ranking.get(0).nome());
        assertEquals("Zeca", ranking.get(1).nome());
    }

    @Test
    void deveRetornarListaVaziaSemAlunos() {
        when(alunos.listarTodos()).thenReturn(List.of());

        assertTrue(service.listar().isEmpty());
    }
}
