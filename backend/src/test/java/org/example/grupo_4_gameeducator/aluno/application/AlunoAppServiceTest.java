package org.example.grupo_4_gameeducator.aluno.application;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.CriarAlunoRequest;
import org.example.grupo_4_gameeducator.aluno.internal.persistence.InMemoryAlunoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Roda com o adapter FAKE em memoria: nenhum JPA, nenhum Mockito. */
class AlunoAppServiceTest {

    private InMemoryAlunoRepositorio repositorio;
    private AlunoAppService service;

    @BeforeEach
    void setUp() {
        repositorio = new InMemoryAlunoRepositorio();
        service = new AlunoAppService(repositorio);
    }

    @Test
    void deveCriarAluno() {
        var resp = service.criar(new CriarAlunoRequest("Carolina", "Basico", 10));

        assertEquals(1L, resp.id());
        assertEquals("Carolina", resp.nome());
        assertEquals("Basico", resp.planoNome());
        assertFalse(resp.premium());
    }

    @Test
    void deveCriarAlunoSemPlanoQuandoNomeDoPlanoVazioOuNulo() {
        assertNull(service.criar(new CriarAlunoRequest("Ana", "", 0)).planoNome());
        assertNull(service.criar(new CriarAlunoRequest("Bia", null, 0)).planoNome());
    }

    @Test
    void deveListarTodos() {
        service.criar(new CriarAlunoRequest("Carolina", "Basico", 10));
        service.criar(new CriarAlunoRequest("Ana", "Basico", 10));

        var lista = service.listarTodos();

        assertEquals(2, lista.size());
        assertEquals("Carolina", lista.get(0).nome());
    }

    @Test
    void deveBuscarPorId() {
        var criado = service.criar(new CriarAlunoRequest("Carolina", "Basico", 10));

        assertEquals("Carolina", service.buscarPorId(criado.id()).nome());
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoEncontrado() {
        assertThrows(IllegalArgumentException.class, () -> service.buscarPorId(99L));
        assertThrows(IllegalArgumentException.class, () -> service.atualizar(99L, a -> {}));
    }

    @Test
    void deveAplicarOperacaoDeOutroModuloEPersistir() {
        var criado = service.criar(new CriarAlunoRequest("Carolina", "Basico", 10));

        var resp = service.atualizar(criado.id(), aluno -> aluno.adicionarMoedas(20));

        assertEquals(20, resp.saldoMoedas());
        assertEquals(20, service.buscarPorId(criado.id()).saldoMoedas());
    }

    @Test
    void devePropagarExcecaoDaRegraSemPersistir() {
        var criado = service.criar(new CriarAlunoRequest("Carolina", "Basico", 10));
        int antes = repositorio.salvamentos;

        assertThrows(IllegalStateException.class, () -> service.atualizar(criado.id(), aluno -> {
            throw new IllegalStateException("regra violada");
        }));

        assertEquals(antes, repositorio.salvamentos);
    }

    @Test
    void deveMarcarPremiumComDozeCursos() {
        var criado = service.criar(new CriarAlunoRequest("Carolina", "Premium", 20));

        var resp = service.atualizar(criado.id(), aluno -> aluno.setTotalCursos(12));

        assertTrue(resp.premium());
    }
}
