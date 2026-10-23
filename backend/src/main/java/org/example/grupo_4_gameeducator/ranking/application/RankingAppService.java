package org.example.grupo_4_gameeducator.ranking.application;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.aluno.api.AlunoGateway;
import org.example.grupo_4_gameeducator.ranking.api.RankingDTOs.RankingItemResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * RF01 - Ranking. Somente leitura. Regra didatica de pontuacao (a validar com o grupo):
 * pontuacao = PESO_CURSO * totalCursos + saldoMoedas. Desempate por nome.
 */
@Service
public class RankingAppService {

    public static final int PESO_CURSO = 100;

    private final AlunoGateway alunos;

    public RankingAppService(AlunoGateway alunos) {
        this.alunos = alunos;
    }

    public List<RankingItemResponse> listar() {
        List<AlunoResponse> ordenados = alunos.listarTodos().stream()
                .sorted(Comparator.<AlunoResponse>comparingInt(RankingAppService::pontuacao).reversed()
                        .thenComparing(AlunoResponse::nome))
                .toList();

        return IntStream.range(0, ordenados.size())
                .mapToObj(i -> {
                    AlunoResponse a = ordenados.get(i);
                    return new RankingItemResponse(i + 1, a.id(), a.nome(), a.totalCursos(), a.saldoMoedas(), pontuacao(a));
                })
                .toList();
    }

    static int pontuacao(AlunoResponse aluno) {
        return PESO_CURSO * aluno.totalCursos() + aluno.saldoMoedas();
    }
}
