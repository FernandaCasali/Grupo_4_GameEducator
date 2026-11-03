package org.example.grupo_4_gameeducator.moedas.domain;

import org.example.grupo_4_gameeducator.aluno.domain.Aluno;

public class MoedasService {

    public void creditarMoedasPorEngajamento(Aluno aluno, int quantidade, String motivo) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade de moedas deve ser maior que zero.");
        }
        aluno.adicionarMoedas(quantidade);
        aluno.getHistoricoMoedas().add(motivo + ": +" + quantidade + " moeda(s)");
    }

    public boolean isPremium(Aluno aluno) {
        return aluno.isPremium();
    }

    public double converterMoedasParaCripto(Aluno aluno, int quantidadeMoedas, double taxaCambio) {
        if (!isPremium(aluno)) {
            throw new IllegalStateException("Somente alunos Premium (12 cursos concluidos) podem converter moedas em criptomoeda.");
        }
        if (quantidadeMoedas <= 0 || quantidadeMoedas > aluno.getSaldoMoedas()) {
            throw new IllegalArgumentException("Quantidade de moedas invalida para conversao.");
        }
        aluno.removerMoedas(quantidadeMoedas);
        double valorConvertido = quantidadeMoedas * taxaCambio;
        aluno.getHistoricoMoedas().add(
                "Conversao para cripto: -" + quantidadeMoedas + " moeda(s) -> " + valorConvertido + " cripto");
        return valorConvertido;
    }
}
