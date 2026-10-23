package org.example.grupo_4_gameeducator.moedas.application;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.CriarAlunoRequest;
import org.example.grupo_4_gameeducator.aluno.application.AlunoAppService;
import org.example.grupo_4_gameeducator.aluno.internal.persistence.InMemoryAlunoRepositorio;
import org.example.grupo_4_gameeducator.moedas.api.MoedasDTOs.ConverterCriptoRequest;
import org.example.grupo_4_gameeducator.moedas.api.MoedasDTOs.CreditarMoedasRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoedasAppServiceTest {

    private AlunoAppService alunos;
    private MoedasAppService service;
    private Long alunoId;

    @BeforeEach
    void setUp() {
        alunos = new AlunoAppService(new InMemoryAlunoRepositorio());
        service = new MoedasAppService(alunos);
        alunoId = alunos.criar(new CriarAlunoRequest("Carolina", "Basico", 10)).id();
    }

    @Test
    void deveCreditarMoedas() {
        var resp = service.creditarMoedas(alunoId, new CreditarMoedasRequest(20, "Participacao no forum"));

        assertEquals(20, resp.saldoMoedas());
        assertEquals(1, resp.historicoMoedas().size());
    }

    @Test
    void deveConverterCriptoQuandoPremium() {
        alunos.atualizar(alunoId, a -> {
            a.setTotalCursos(12);
            a.adicionarMoedas(100);
        });

        var resp = service.converterCripto(alunoId, new ConverterCriptoRequest(50, 0.02));

        assertEquals(50, resp.saldoMoedas());
        assertTrue(resp.premium());
    }

    @Test
    void deveLancarExcecaoNaConversaoQuandoNaoPremium() {
        assertThrows(IllegalStateException.class,
                () -> service.converterCripto(alunoId, new ConverterCriptoRequest(10, 0.01)));
    }
}
