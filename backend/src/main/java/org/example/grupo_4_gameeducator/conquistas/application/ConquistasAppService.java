package org.example.grupo_4_gameeducator.conquistas.application;

import org.example.grupo_4_gameeducator.aluno.api.AlunoDTOs.AlunoResponse;
import org.example.grupo_4_gameeducator.aluno.api.AlunoGateway;
import org.example.grupo_4_gameeducator.conquistas.api.ConquistasDTOs.BadgeResponse;
import org.example.grupo_4_gameeducator.conquistas.api.ConquistasDTOs.ConquistasResponse;
import org.example.grupo_4_gameeducator.conquistas.domain.RegrasConquistas;
import org.springframework.stereotype.Service;

/** RF02 - Conquistas. Somente leitura: consulta o aluno pelo contrato publico e aplica a regra de dominio. */
@Service
public class ConquistasAppService {

    private final AlunoGateway alunos;

    public ConquistasAppService(AlunoGateway alunos) {
        this.alunos = alunos;
    }

    public ConquistasResponse consultar(Long alunoId) {
        AlunoResponse aluno = alunos.buscarPorId(alunoId);
        var badges = RegrasConquistas.badgesPara(aluno.totalCursos(), aluno.saldoMoedas()).stream()
                .map(b -> new BadgeResponse(b.name(), b.getDescricao()))
                .toList();
        return new ConquistasResponse(aluno.id(), aluno.nome(), badges);
    }
}
