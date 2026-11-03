package org.example.grupo_4_gameeducator.aluno.application;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.CriarAlunoRequest;
import org.example.grupo_4_gameeducator.aluno.api.AlunoGateway;
import org.example.grupo_4_gameeducator.aluno.application.AlunoRepositorio.Registro;
import org.example.grupo_4_gameeducator.aluno.domain.Aluno;
import org.example.grupo_4_gameeducator.aluno.domain.Curso;
import org.example.grupo_4_gameeducator.aluno.domain.Plano;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;

/** Caso de uso do modulo aluno. Depende da PORTA AlunoRepositorio, nao de JPA. */
@Service
public class AlunoAppService implements AlunoGateway {

    private final AlunoRepositorio repositorio;

    public AlunoAppService(AlunoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public AlunoResponse criar(CriarAlunoRequest request) {
        Aluno aluno = new Aluno(request.nome());
        if (request.planoNome() != null && !request.planoNome().isBlank()) {
            aluno.setPlano(new Plano(request.planoNome(), request.planoLimiteCursos()));
        }
        return toResponse(repositorio.criar(aluno));
    }

    @Override
    public List<AlunoResponse> listarTodos() {
        return repositorio.listar().stream().map(this::toResponse).toList();
    }

    @Override
    public AlunoResponse buscarPorId(Long id) {
        return toResponse(buscarOuFalhar(id));
    }

    @Override
    public AlunoResponse atualizar(Long id, Consumer<Aluno> operacao) {
        Registro registro = buscarOuFalhar(id);
        operacao.accept(registro.aluno());
        return toResponse(repositorio.salvar(id, registro.aluno()));
    }

    private Registro buscarOuFalhar(Long id) {
        return repositorio.buscar(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno nao encontrado: " + id));
    }

    private AlunoResponse toResponse(Registro registro) {
        Aluno a = registro.aluno();
        return new AlunoResponse(
                registro.id(),
                a.getNome(),
                a.getPlano() == null ? null : a.getPlano().getNome(),
                a.getTotalCursos(),
                a.isPremium(),
                a.getSaldoMoedas(),
                a.foiNotificado(),
                a.getCursosDesbloqueados().stream().map(Curso::getNome).toList(),
                new LinkedHashSet<>(a.getNotificacoes()),
                new ArrayList<>(a.getHistoricoMoedas())
        );
    }
}
