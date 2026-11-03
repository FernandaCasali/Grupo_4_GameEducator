package org.example.grupo_4_gameeducator.aluno.application;

import org.example.grupo_4_gameeducator.aluno.domain.Aluno;

import java.util.List;
import java.util.Optional;

/**
 * PORTA de saida (Ports & Adapters). A aplicacao depende desta interface, nunca de JPA.
 * Adapters: JpaAlunoRepositorio (producao) e InMemoryAlunoRepositorio (testes).
 */
public interface AlunoRepositorio {

    Registro criar(Aluno aluno);

    List<Registro> listar();

    Optional<Registro> buscar(Long id);

    Registro salvar(Long id, Aluno aluno);

    /** Aluno de dominio + identificador atribuido pela persistencia. */
    record Registro(Long id, Aluno aluno) {}
}
