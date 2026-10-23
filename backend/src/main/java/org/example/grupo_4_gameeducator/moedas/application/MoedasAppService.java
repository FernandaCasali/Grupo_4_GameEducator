package org.example.grupo_4_gameeducator.moedas.application;

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
        return alunos.atualizar(alunoId,
                aluno -> moedas.creditarMoedasPorEngajamento(aluno, request.quantidade(), request.motivo()));
    }

    public AlunoResponse converterCripto(Long alunoId, ConverterCriptoRequest request) {
        return alunos.atualizar(alunoId,
                aluno -> moedas.converterMoedasParaCripto(aluno, request.quantidadeMoedas(), request.taxaCambio()));
    }
}
