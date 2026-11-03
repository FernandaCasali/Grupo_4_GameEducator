package org.example.grupo_4_gameeducator.ranking.application;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.CriarAlunoRequest;
import org.example.grupo_4_gameeducator.aluno.application.AlunoAppService;
import org.example.grupo_4_gameeducator.aluno.internal.persistence.InMemoryAlunoRepositorio;
import org.example.grupo_4_gameeducator.ranking.api.RankingDTOs.RankingItemResponse;
import org.example.grupo_4_gameeducator.ranking.domain.PontuacaoRanking;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * B1 / AC2 - Ranking para ATE 1000 alunos: testa TODOS os tamanhos de 1 a 1000.
 * Usa os casos de uso reais (AlunoAppService + RankingAppService) sobre o adapter em memoria,
 * sem Spring e sem banco. Valida CORRECAO (ordem, posicoes, desempate, formula) e TEMPO.
 */
class RankingCargaTest {

    private static final int MAXIMO_ALUNOS = 1000;
    private static final long LIMITE_MS = 1000;
    private static final Set<Integer> TAMANHOS_EXIBIDOS = Set.of(1, 10, 100, 500, 999, 1000);

    private RankingAppService criarRankingCom(int total) {
        AlunoAppService alunos = new AlunoAppService(new InMemoryAlunoRepositorio());
        Random aleatorio = new Random(42); // semente fixa: o teste e reproduzivel
        for (int i = 1; i <= total; i++) {
            Long id = alunos.criar(new CriarAlunoRequest(String.format("Aluno %04d", i), "Basico", 5)).id();
            int cursos = aleatorio.nextInt(15);   // 0..14
            int moedas = aleatorio.nextInt(300);  // 0..299
            alunos.atualizar(id, a -> {
                a.setTotalCursos(cursos);
                a.adicionarMoedas(moedas);
            });
        }
        return new RankingAppService(alunos);
    }

    private void verificarRanking(int total, List<RankingItemResponse> resultado) {
        assertEquals(total, resultado.size(), "tamanho " + total);

        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < resultado.size(); i++) {
            RankingItemResponse atual = resultado.get(i);
            assertEquals(i + 1, atual.posicao(), "posicoes sequenciais com " + total + " alunos");
            assertEquals(PontuacaoRanking.calcular(atual.totalCursos(), atual.saldoMoedas()), atual.pontuacao());
            ids.add(atual.alunoId());

            if (i > 0) {
                RankingItemResponse anterior = resultado.get(i - 1);
                assertTrue(anterior.pontuacao() >= atual.pontuacao(),
                        "ordem quebrada na posicao " + atual.posicao() + " com " + total + " alunos");
                if (anterior.pontuacao() == atual.pontuacao()) {
                    assertTrue(anterior.nome().compareTo(atual.nome()) <= 0,
                            "desempate por nome quebrado na posicao " + atual.posicao() + " com " + total + " alunos");
                }
            }
        }
        assertEquals(total, ids.size(), "nenhum aluno repetido ou perdido com " + total + " alunos");
    }

    @Test
    void deveMontarRankingCorretoDe1Ate1000Alunos() {
        for (int total = 1; total <= MAXIMO_ALUNOS; total++) {
            RankingAppService ranking = criarRankingCom(total);
            verificarRanking(total, ranking.listar());
        }
    }

    @Test
    void deveResponderEmMenosDe1SegundoDe1Ate1000Alunos() {
        long piorMs = 0;
        int piorTamanho = 0;

        for (int total = 1; total <= MAXIMO_ALUNOS; total++) {
            RankingAppService ranking = criarRankingCom(total);

            long inicio = System.nanoTime();
            List<RankingItemResponse> resultado = ranking.listar();
            long ms = (System.nanoTime() - inicio) / 1_000_000;

            assertEquals(total, resultado.size());
            assertTrue(ms < LIMITE_MS, "ranking com " + total + " alunos levou " + ms + " ms (limite " + LIMITE_MS + " ms)");

            if (ms >= piorMs) {
                piorMs = ms;
                piorTamanho = total;
            }
            if (TAMANHOS_EXIBIDOS.contains(total)) {
                System.out.println("[B1-AC2] Ranking de " + total + " alunos montado em " + ms + " ms");
            }
        }
        System.out.println("[B1-AC2] Testados todos os tamanhos de 1 a " + MAXIMO_ALUNOS
                + " alunos. Pior caso: " + piorMs + " ms com " + piorTamanho + " alunos (limite " + LIMITE_MS + " ms)");
    }
}
