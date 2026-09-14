package org.example.grupo_4_gameeducator.service;

import org.example.grupo_4_gameeducator.domain.Aluno;
import org.example.grupo_4_gameeducator.domain.AlunoService;
import org.example.grupo_4_gameeducator.domain.Curso;
import org.example.grupo_4_gameeducator.domain.Plano;
import org.example.grupo_4_gameeducator.domain.StatusCurso;
import org.example.grupo_4_gameeducator.dto.AlunoDTOs.*;
import org.example.grupo_4_gameeducator.entity.AlunoEntity;
import org.example.grupo_4_gameeducator.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoAppService {

    private final AlunoRepository repository;
    private final AlunoService alunoDomainService = new AlunoService();

    public AlunoAppService(AlunoRepository repository) {
        this.repository = repository;
    }

    public AlunoResponse criar(CriarAlunoRequest request) {
        AlunoEntity entity = new AlunoEntity(request.nome(), request.planoNome(), request.planoLimiteCursos());
        entity = repository.save(entity);
        return toResponse(entity);
    }

    public List<AlunoResponse> listarTodos() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public AlunoResponse buscarPorId(Long id) {
        return toResponse(buscarEntityOuFalhar(id));
    }

    public AlunoResponse concluirCurso(Long id, ConcluirCursoRequest request) {
        AlunoEntity entity = buscarEntityOuFalhar(id);
        Aluno aluno = toDominio(entity);
        Curso curso = new Curso(request.nomeCurso(), StatusCurso.CONCLUIDO);
        alunoDomainService.processarDesbloqueio(aluno, curso, request.media());
        atualizarEntityComDominio(entity, aluno);
        return toResponse(repository.save(entity));
    }

    public AlunoResponse creditarMoedas(Long id, CreditarMoedasRequest request) {
        AlunoEntity entity = buscarEntityOuFalhar(id);
        Aluno aluno = toDominio(entity);
        alunoDomainService.creditarMoedasPorEngajamento(aluno, request.quantidade(), request.motivo());
        atualizarEntityComDominio(entity, aluno);
        return toResponse(repository.save(entity));
    }

    public AlunoResponse converterCripto(Long id, ConverterCriptoRequest request) {
        AlunoEntity entity = buscarEntityOuFalhar(id);
        Aluno aluno = toDominio(entity);
        alunoDomainService.converterMoedasParaCripto(aluno, request.quantidadeMoedas(), request.taxaCambio());
        atualizarEntityComDominio(entity, aluno);
        return toResponse(repository.save(entity));
    }

    private Aluno toDominio(AlunoEntity entity) {
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

    private void atualizarEntityComDominio(AlunoEntity entity, Aluno aluno) {
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

    private AlunoResponse toResponse(AlunoEntity entity) {
        boolean premium = entity.getTotalCursos() >= 12;
        return new AlunoResponse(
                entity.getId(), entity.getNome(), entity.getPlanoNome(), entity.getTotalCursos(),
                premium, entity.getSaldoMoedas(), entity.isNotificado(),
                entity.getCursosDesbloqueados(), entity.getNotificacoes(), entity.getHistoricoMoedas()
        );
    }

    private AlunoEntity buscarEntityOuFalhar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno nao encontrado: " + id));
    }
}