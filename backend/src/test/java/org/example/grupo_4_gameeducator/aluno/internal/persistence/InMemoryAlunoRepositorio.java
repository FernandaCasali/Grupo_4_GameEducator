package org.example.grupo_4_gameeducator.aluno.internal.persistence;

import org.example.grupo_4_gameeducator.aluno.application.AlunoRepositorio;
import org.example.grupo_4_gameeducator.aluno.domain.Aluno;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ADAPTER FAKE (D2): troca o JPA por memoria sem alterar nenhuma regra de negocio ou caso de uso.
 * Prova que o dominio e a aplicacao nao dependem de banco/framework.
 */
public class InMemoryAlunoRepositorio implements AlunoRepositorio {

    private final Map<Long, Aluno> dados = new LinkedHashMap<>();
    private long proximoId = 1;
    public int salvamentos = 0;

    @Override
    public Registro criar(Aluno aluno) {
        long id = proximoId++;
        dados.put(id, aluno);
        salvamentos++;
        return new Registro(id, aluno);
    }

    @Override
    public List<Registro> listar() {
        List<Registro> lista = new ArrayList<>();
        dados.forEach((id, aluno) -> lista.add(new Registro(id, aluno)));
        return lista;
    }

    @Override
    public Optional<Registro> buscar(Long id) {
        return Optional.ofNullable(dados.get(id)).map(a -> new Registro(id, a));
    }

    @Override
    public Registro salvar(Long id, Aluno aluno) {
        if (!dados.containsKey(id)) {
            throw new IllegalArgumentException("Aluno nao encontrado: " + id);
        }
        dados.put(id, aluno);
        salvamentos++;
        return new Registro(id, aluno);
    }
}
