package org.example.grupo_4_gameeducator.moedas.application;

import io.micrometer.core.instrument.Metrics;
import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.aluno.api.AlunoGateway;
import org.example.grupo_4_gameeducator.moedas.api.MoedasDTOs.ConverterCriptoRequest;
import org.example.grupo_4_gameeducator.moedas.api.MoedasDTOs.CreditarMoedasRequest;
import org.example.grupo_4_gameeducator.moedas.domain.MoedasService;
import org.springframework.stereotype.Service;

@Service
public class MoedasAppService {

    private final AlunoGateway alunos;
    private final MoedasService moedas = new MoedasService();

    public MoedasAppService(AlunoGateway alunos) {
        this.alunos = alunos;
    }

    public AlunoResponse creditarMoedas(Long alunoId, CreditarMoedasRequest request) {
        AlunoResponse resposta = alunos.atualizar(alunoId,
                aluno -> moedas.creditarMoedasPorEngajamento(aluno, request.quantidade(), request.motivo()));
        // D6 - metrica de negocio (Prometheus: gameeducator_moedas_creditadas_total)
        Metrics.counter("gameeducator.moedas.creditadas").increment(request.quantidade());
        return resposta;
    }

    public AlunoResponse converterCripto(Long alunoId, ConverterCriptoRequest request) {
        AlunoResponse resposta = alunos.atualizar(alunoId,
                aluno -> moedas.converterMoedasParaCripto(aluno, request.quantidadeMoedas(), request.taxaCambio()));
        // D6 - metrica de negocio (Prometheus: gameeducator_conversoes_cripto_total)
        Metrics.counter("gameeducator.conversoes.cripto").increment();
        return resposta;
    }
}
