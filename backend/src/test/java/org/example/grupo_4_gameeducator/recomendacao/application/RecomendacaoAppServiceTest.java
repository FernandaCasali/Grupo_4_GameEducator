package org.example.grupo_4_gameeducator.recomendacao.application;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.CriarAlunoRequest;
import org.example.grupo_4_gameeducator.aluno.application.AlunoAppService;
import org.example.grupo_4_gameeducator.aluno.domain.Curso;
import org.example.grupo_4_gameeducator.aluno.internal.persistence.InMemoryAlunoRepositorio;
import org.example.grupo_4_gameeducator.recomendacao.application.RecomendadorPort.Sugestao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RecomendacaoAppServiceTest {

    private AlunoAppService alunos;
    private Long alunoId;

    @BeforeEach
    void setUp() {
        alunos = new AlunoAppService(new InMemoryAlunoRepositorio());
        alunoId = alunos.criar(new CriarAlunoRequest("Carolina", "Basico", 10)).id();
    }

    @Test
    void deveRetornarRecomendacoesDaIaEnviandoOsCursosDoAluno() {
        alunos.atualizar(alunoId, a -> a.getCursosDesbloqueados().add(new Curso("Logica")));
        AtomicReference<List<String>> recebidos = new AtomicReference<>();
        RecomendadorPort ia = (id, cursos) -> {
            recebidos.set(cursos);
            return List.of(new Sugestao("Java Basico", "Proximo passo apos Logica"));
        };

        var resp = new RecomendacaoAppService(alunos, ia).recomendar(alunoId);

        assertFalse(resp.degradado());
        assertEquals(RecomendacaoAppService.ORIGEM_IA, resp.origem());
        assertEquals("Java Basico", resp.recomendacoes().get(0).curso());
        assertEquals(List.of("Logica"), recebidos.get());
    }

    @Test
    void deveDegradarSemQuebrarQuandoIaEstaIndisponivel() {
        RecomendadorPort iaFora = (id, cursos) -> {
            throw new RecomendadorIndisponivelException("fora do ar", new RuntimeException("connection refused"));
        };

        var resp = new RecomendacaoAppService(alunos, iaFora).recomendar(alunoId);

        assertTrue(resp.degradado());
        assertEquals(RecomendacaoAppService.ORIGEM_FALLBACK, resp.origem());
        assertTrue(resp.recomendacoes().isEmpty());
    }

    @Test
    void naoDeveChamarIaQuandoAlunoNaoExiste() {
        RecomendadorPort ia = (id, cursos) -> {
            fail("A IA nao deveria ser chamada");
            return List.of();
        };

        assertThrows(IllegalArgumentException.class, () -> new RecomendacaoAppService(alunos, ia).recomendar(99L));
    }
}
