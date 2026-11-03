package org.example.grupo_4_gameeducator.aluno.internal.persistence;

import org.example.grupo_4_gameeducator.aluno.application.AlunoRepositorio;
import org.example.grupo_4_gameeducator.aluno.domain.Aluno;
import org.example.grupo_4_gameeducator.aluno.domain.Curso;
import org.example.grupo_4_gameeducator.aluno.domain.Plano;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** ADAPTER de saida: traduz entre o dominio (Aluno) e a persistencia JPA (AlunoEntity). */
@Component
public class JpaAlunoRepositorio implements AlunoRepositorio {

    private final AlunoRepository jpa;

    public JpaAlunoRepositorio(AlunoRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Registro criar(Aluno aluno) {
        Plano plano = aluno.getPlano();
        AlunoEntity entity = new AlunoEntity(
                aluno.getNome(),
                plano == null ? null : plano.getNome(),
                plano == null ? 0 : plano.getLimiteCursos());
        aplicar(entity, aluno);
        return paraRegistro(jpa.save(entity));
    }

    @Override
    public List<Registro> listar() {
        return jpa.findAll().stream().map(this::paraRegistro).toList();
    }

    @Override
    public Optional<Registro> buscar(Long id) {
        return jpa.findById(id).map(this::paraRegistro);
    }

    @Override
    public Registro salvar(Long id, Aluno aluno) {
        AlunoEntity entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno nao encontrado: " + id));
        aplicar(entity, aluno);
        return paraRegistro(jpa.save(entity));
    }

    private Registro paraRegistro(AlunoEntity entity) {
        return new Registro(entity.getId(), paraDominio(entity));
    }

    private Aluno paraDominio(AlunoEntity entity) {
        Aluno aluno = new Aluno(entity.getNome());
        if (entity.getPlanoNome() != null && !entity.getPlanoNome().isBlank()) {
            aluno.setPlano(new Plano(entity.getPlanoNome(), entity.getPlanoLimiteCursos()));
        }
        aluno.setTotalCursos(entity.getTotalCursos());
        entity.getCursosDesbloqueados().forEach(nome -> aluno.getCursosDesbloqueados().add(new Curso(nome)));
        aluno.getNotificacoes().addAll(entity.getNotificacoes());
        aluno.adicionarMoedas(entity.getSaldoMoedas());
        aluno.getHistoricoMoedas().addAll(entity.getHistoricoMoedas());
        if (entity.isNotificado()) {
            aluno.marcarComoNotificado();
        }
        return aluno;
    }

    private void aplicar(AlunoEntity entity, Aluno aluno) {
        entity.setTotalCursos(aluno.getTotalCursos());
        entity.setSaldoMoedas(aluno.getSaldoMoedas());
        entity.setNotificado(aluno.foiNotificado());
        entity.getCursosDesbloqueados().clear();
        aluno.getCursosDesbloqueados().forEach(c -> entity.getCursosDesbloqueados().add(c.getNome()));
        entity.getNotificacoes().clear();
        entity.getNotificacoes().addAll(aluno.getNotificacoes());
        entity.getHistoricoMoedas().clear();
        entity.getHistoricoMoedas().addAll(aluno.getHistoricoMoedas());
    }
}
