package org.example.grupo_4_gameeducator.aluno.internal.persistence;

import org.example.grupo_4_gameeducator.aluno.domain.Aluno;
import org.example.grupo_4_gameeducator.aluno.domain.Curso;
import org.example.grupo_4_gameeducator.aluno.domain.Plano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Teste do ADAPTER JPA: traducao entidade <-> dominio (cobre os ramos do antigo toDominio). */
@ExtendWith(MockitoExtension.class)
class JpaAlunoRepositorioTest {

    @Mock
    private AlunoRepository jpa;

    @InjectMocks
    private JpaAlunoRepositorio adapter;

    private AlunoEntity entity;

    @BeforeEach
    void setUp() {
        entity = new AlunoEntity("Carolina", "Basico", 10);
    }

    @Test
    void deveCriarPersistindoPlanoDoDominio() {
        when(jpa.save(any(AlunoEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        var aluno = new Aluno("Carolina");
        aluno.setPlano(new Plano("Basico", 10));

        var registro = adapter.criar(aluno);

        assertEquals("Carolina", registro.aluno().getNome());
        assertEquals("Basico", registro.aluno().getPlano().getNome());
        assertEquals(10, registro.aluno().getPlano().getLimiteCursos());
    }

    @Test
    void deveCriarSemPlano() {
        when(jpa.save(any(AlunoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        var registro = adapter.criar(new Aluno("Ana"));

        assertNull(registro.aluno().getPlano());
    }

    @Test
    void deveListar() {
        when(jpa.findAll()).thenReturn(List.of(entity));

        assertEquals("Carolina", adapter.listar().get(0).aluno().getNome());
    }

    @Test
    void deveBuscarPorId() {
        when(jpa.findById(1L)).thenReturn(Optional.of(entity));

        assertTrue(adapter.buscar(1L).isPresent());
    }

    @Test
    void deveRetornarVazioQuandoNaoExiste() {
        when(jpa.findById(99L)).thenReturn(Optional.empty());

        assertTrue(adapter.buscar(99L).isEmpty());
    }

    @Test
    void deveLancarExcecaoAoSalvarAlunoInexistente() {
        when(jpa.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> adapter.salvar(99L, new Aluno("X")));
    }

    @Test
    void naoDeveCriarPlanoNoDominioQuandoNomeVazio() {
        when(jpa.findById(1L)).thenReturn(Optional.of(new AlunoEntity("Carol", "", 0)));

        assertNull(adapter.buscar(1L).orElseThrow().aluno().getPlano());
    }

    @Test
    void naoDeveCriarPlanoNoDominioQuandoNomeNulo() {
        when(jpa.findById(1L)).thenReturn(Optional.of(new AlunoEntity("Carol", null, 0)));

        assertNull(adapter.buscar(1L).orElseThrow().aluno().getPlano());
    }

    @Test
    void deveManterNotificadoQuandoEntidadeJaEstavaNotificada() {
        entity.setNotificado(true);
        when(jpa.findById(1L)).thenReturn(Optional.of(entity));

        assertTrue(adapter.buscar(1L).orElseThrow().aluno().foiNotificado());
    }

    @Test
    void devePreservarCursosDesbloqueadosAoMapearParaDominio() {
        entity.getCursosDesbloqueados().add("Logica");
        entity.getCursosDesbloqueados().add("Java");
        when(jpa.findById(1L)).thenReturn(Optional.of(entity));

        var aluno = adapter.buscar(1L).orElseThrow().aluno();

        assertEquals(List.of(new Curso("Logica"), new Curso("Java")), aluno.getCursosDesbloqueados());
    }

    @Test
    void deveSalvarEstadoDoDominioNaEntidade() {
        entity.setTotalCursos(0);
        when(jpa.findById(1L)).thenReturn(Optional.of(entity));
        when(jpa.save(any(AlunoEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        var aluno = new Aluno("Carolina");
        aluno.setTotalCursos(3);
        aluno.adicionarMoedas(20);
        aluno.getHistoricoMoedas().add("forum: +20 moeda(s)");
        aluno.getCursosDesbloqueados().add(new Curso("Logica (bonus 1)"));
        aluno.getNotificacoes().add("LIMITE_ATINGIDO");
        aluno.marcarComoNotificado();

        adapter.salvar(1L, aluno);

        assertEquals(3, entity.getTotalCursos());
        assertEquals(20, entity.getSaldoMoedas());
        assertTrue(entity.isNotificado());
        assertEquals(List.of("Logica (bonus 1)"), entity.getCursosDesbloqueados());
        assertTrue(entity.getNotificacoes().contains("LIMITE_ATINGIDO"));
        assertEquals(1, entity.getHistoricoMoedas().size());
        verify(jpa).save(entity);
    }
}
