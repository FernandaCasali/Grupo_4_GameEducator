package org.example.grupo_4_gameeducator.cursos.application;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.CriarAlunoRequest;
import org.example.grupo_4_gameeducator.aluno.application.AlunoAppService;
import org.example.grupo_4_gameeducator.aluno.internal.persistence.InMemoryAlunoRepositorio;
import org.example.grupo_4_gameeducator.cursos.api.CursoConcluidoEvent;
import org.example.grupo_4_gameeducator.cursos.api.CursosDTOs.ConcluirCursoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CursosAppServiceTest {

    private AlunoAppService alunos;
    private List<Object> publicados;
    private CursosAppService service;
    private Long alunoId;

    @BeforeEach
    void setUp() {
        alunos = new AlunoAppService(new InMemoryAlunoRepositorio());
        publicados = new ArrayList<>();
        ApplicationEventPublisher publisher = publicados::add;
        service = new CursosAppService(alunos, publisher);
        alunoId = alunos.criar(new CriarAlunoRequest("Carolina", "Basico", 10)).id();
    }

    @Test
    void deveConcluirCursoLiberar3BonusEnotificar() {
        var resp = service.concluirCurso(alunoId, new ConcluirCursoRequest("Logica", 8.5));

        assertEquals(3, resp.totalCursos());
        assertTrue(resp.notificado());
        assertEquals(3, resp.cursosDesbloqueados().size());
    }

    @Test
    void naoDeveLiberarQuandoLimiteDoPlanoAtingido() {
        alunos.atualizar(alunoId, a -> a.setTotalCursos(10));

        var resp = service.concluirCurso(alunoId, new ConcluirCursoRequest("Redes", 8.0));

        assertEquals(0, resp.cursosDesbloqueados().size());
        assertTrue(resp.notificacoes().contains("LIMITE_ATINGIDO"));
    }

    @Test
    void naoDeveLiberarComMediaAbaixoDoMinimo() {
        var resp = service.concluirCurso(alunoId, new ConcluirCursoRequest("Redes", 6.9));

        assertEquals(0, resp.cursosDesbloqueados().size());
        assertFalse(resp.notificado());
    }

    @Test
    void devePublicarEventoComOsDadosDaConclusao() {
        service.concluirCurso(alunoId, new ConcluirCursoRequest("Logica", 8.5));

        assertEquals(1, publicados.size());
        var evento = (CursoConcluidoEvent) publicados.get(0);
        assertEquals(alunoId, evento.alunoId());
        assertEquals("Carolina", evento.alunoNome());
        assertEquals("Logica", evento.nomeCurso());
        assertEquals(3, evento.cursosLiberados());
        assertFalse(evento.limiteAtingido());
    }

    @Test
    void deveSinalizarLimiteAtingidoNoEvento() {
        alunos.atualizar(alunoId, a -> a.setTotalCursos(10));

        service.concluirCurso(alunoId, new ConcluirCursoRequest("Redes", 8.0));

        var evento = (CursoConcluidoEvent) publicados.get(0);
        assertTrue(evento.limiteAtingido());
        assertEquals(0, evento.cursosLiberados());
    }

    @Test
    void falhaNaPublicacaoDoEventoNaoImpedeAConclusaoDoCurso() {
        ApplicationEventPublisher quebrado = evento -> {
            throw new IllegalStateException("barramento indisponivel");
        };
        var servicoComFalha = new CursosAppService(alunos, quebrado);

        var resp = servicoComFalha.concluirCurso(alunoId, new ConcluirCursoRequest("Logica", 8.5));

        assertEquals(3, resp.cursosDesbloqueados().size());
        assertEquals(3, alunos.buscarPorId(alunoId).totalCursos()); // ficou persistido
    }
}
