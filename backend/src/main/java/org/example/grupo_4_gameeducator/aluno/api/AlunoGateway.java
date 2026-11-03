package org.example.grupo_4_gameeducator.aluno.api;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.aluno.domain.Aluno;

import java.util.List;
import java.util.function.Consumer;

/**
 * Contrato publico do modulo aluno. Os demais modulos (cursos, moedas, ranking)
 * so podem conversar com o aluno por aqui; nunca pelo pacote internal.
 */
public interface AlunoGateway {

    List<AlunoResponse> listarTodos();

    AlunoResponse buscarPorId(Long id);

    /** Carrega o aluno, aplica a regra de negocio do chamador e persiste o resultado. */
    AlunoResponse atualizar(Long id, Consumer<Aluno> operacao);
}
