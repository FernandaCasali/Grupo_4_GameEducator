package org.example.grupo_4_gameeducator.domain;

public class AlunoService {

    private static final double MEDIA_MINIMA_PARA_BONUS = 7.0;
    private static final int CURSOS_BONUS_POR_DESEMPENHO = 3;
    private static final int CURSOS_PARA_PREMIUM = 12;

    public void processarDesbloqueio(Aluno aluno, Curso curso, double mediaFinal) {
        if (!verificarElegibilidade(aluno, curso)) {
            return;
        }

        Plano plano = aluno.getPlano();
        if (plano != null && aluno.getTotalCursos() >= plano.getLimiteCursos()) {
            aluno.getNotificacoes().add(Aluno.NOTIFICACAO_LIMITE_ATINGIDO);
            return;
        }

        if (mediaFinal >= MEDIA_MINIMA_PARA_BONUS) {
            for (int i = 1; i <= CURSOS_BONUS_POR_DESEMPENHO; i++) {
                aluno.getCursosDesbloqueados().add(new Curso(curso.getNome() + " (bonus " + i + ")"));
            }
            aluno.setTotalCursos(aluno.getTotalCursos() + CURSOS_BONUS_POR_DESEMPENHO);
            aluno.marcarComoNotificado();
        }
    }

    public boolean verificarElegibilidade(Aluno aluno, Curso curso) {
        return curso.getStatus() == StatusCurso.CONCLUIDO;
    }

    public void creditarMoedasPorEngajamento(Aluno aluno, int quantidade, String motivo) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade de moedas deve ser maior que zero.");
        }
        aluno.adicionarMoedas(quantidade);
        aluno.getHistoricoMoedas().add(motivo + ": +" + quantidade + " moeda(s)");
    }

    public boolean isPremium(Aluno aluno) {
        return aluno.getTotalCursos() >= CURSOS_PARA_PREMIUM;
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