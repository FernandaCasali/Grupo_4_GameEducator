package org.example.grupo_4_gameeducator.conquistas.application;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.aluno.api.AlunoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConquistasAppServiceTest {

    @Mock
    private AlunoGateway alunos;

    @InjectMocks
    private ConquistasAppService service;

    private AlunoResponse aluno(int cursos, int moedas) {
        return new AlunoResponse(1L, "Ana", "Basico", cursos, false, moedas, false, List.of(), Set.of(), List.of());
    }

    @Test
    void deveRetornarBadgesConformeProgressoDoAluno() {
        when(alunos.buscarPorId(1L)).thenReturn(aluno(5, 100));

        var resposta = service.consultar(1L);

        assertEquals("Ana", resposta.nome());
        assertEquals(List.of("PRIMEIRO_CURSO", "CINCO_CURSOS", "COLECIONADOR_DE_MOEDAS"),
                resposta.badges().stream().map(b -> b.codigo()).toList());
    }

    @Test
    void alunoSemProgressoNaoTemBadges() {
        when(alunos.buscarPorId(1L)).thenReturn(aluno(0, 0));

        assertTrue(service.consultar(1L).badges().isEmpty());
    }
}
