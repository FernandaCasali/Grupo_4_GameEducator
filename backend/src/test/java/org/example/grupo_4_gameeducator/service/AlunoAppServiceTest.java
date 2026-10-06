package org.example.grupo_4_gameeducator.service;

import org.example.grupo_4_gameeducator.dto.AlunoDTOs.*;
import org.example.grupo_4_gameeducator.entity.AlunoEntity;
import org.example.grupo_4_gameeducator.repository.AlunoRepository;
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

@ExtendWith(MockitoExtension.class)
class AlunoAppServiceTest {

    @Mock
    private AlunoRepository repository;

    @InjectMocks
    private AlunoAppService service;

    private AlunoEntity entityExemplo;

    @BeforeEach
    void setUp() {
        entityExemplo = new AlunoEntity("Carolina", "Basico", 10);
    }

    @Test
    void deveCriarAluno() {
        when(repository.save(any(AlunoEntity.class))).thenReturn(entityExemplo);
        var req = new CriarAlunoRequest("Carolina", "Basico", 10);

        var resp = service.criar(req);

        assertEquals("Carolina", resp.nome());
        assertEquals("Basico", resp.planoNome());
        verify(repository).save(any(AlunoEntity.class));
    }

    @Test
    void deveListarTodos() {
        when(repository.findAll()).thenReturn(List.of(entityExemplo));

        var lista = service.listarTodos();

        assertEquals(1, lista.size());
        assertEquals("Carolina", lista.get(0).nome());
    }

    @Test
    void deveBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(entityExemplo));

        var resp = service.buscarPorId(1L);

        assertEquals("Carolina", resp.nome());
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void deveConcluirCursoLiberar3BonusEnotificar() {
        when(repository.findById(1L)).thenReturn(Optional.of(entityExemplo));
        when(repository.save(any(AlunoEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        var req = new ConcluirCursoRequest("Logica", 8.5);

        var resp = service.concluirCurso(1L, req);

        assertEquals(3, resp.totalCursos());
        assertTrue(resp.notificado());
        assertEquals(3, resp.cursosDesbloqueados().size());
    }

    @Test
    void deveCreditarMoedas() {
        when(repository.findById(1L)).thenReturn(Optional.of(entityExemplo));
        when(repository.save(any(AlunoEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        var req = new CreditarMoedasRequest(20, "Participacao no forum");

        var resp = service.creditarMoedas(1L, req);

        assertEquals(20, resp.saldoMoedas());
        assertEquals(1, resp.historicoMoedas().size());
    }

    @Test
    void deveConverterCriptoQuandoPremium() {
        entityExemplo.setTotalCursos(12);
        entityExemplo.setSaldoMoedas(100);
        when(repository.findById(1L)).thenReturn(Optional.of(entityExemplo));
        when(repository.save(any(AlunoEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        var req = new ConverterCriptoRequest(50, 0.02);

        var resp = service.converterCripto(1L, req);

        assertEquals(50, resp.saldoMoedas());
        assertTrue(resp.premium());
    }

    @Test
    void deveLancarExcecaoNaConversaoQuandoNaoPremium() {
        when(repository.findById(1L)).thenReturn(Optional.of(entityExemplo));
        var req = new ConverterCriptoRequest(10, 0.01);

        assertThrows(IllegalStateException.class,
                () -> service.converterCripto(1L, req));
    }

    @Test
    void deveMapearEntityComPlanoVazioSemCriarPlanoNoDominio() {
        // cobre o ramo "planoNome e nulo/blank" no toDominio
        var semPlano = new AlunoEntity("Carol", "", 0);
        when(repository.findById(1L)).thenReturn(Optional.of(semPlano));
        when(repository.save(any(AlunoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        var resp = service.creditarMoedas(1L,
                new CreditarMoedasRequest(5, "forum"));

        assertEquals(5, resp.saldoMoedas());
    }
}