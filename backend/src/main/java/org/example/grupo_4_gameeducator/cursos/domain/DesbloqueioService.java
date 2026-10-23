package org.example.grupo_4_gameeducator.cursos.domain;

import org.example.grupo_4_gameeducator.aluno.domain.Aluno;
import org.example.grupo_4_gameeducator.aluno.domain.Curso;
import org.example.grupo_4_gameeducator.aluno.domain.Plano;
import org.example.grupo_4_gameeducator.aluno.domain.StatusCurso;

public class DesbloqueioService {

    private static final double MEDIA_MINIMA_PARA_BONUS = 7.0;
    private static final int CURSOS_BONUS_POR_DESEMPENHO = 3;

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
}
