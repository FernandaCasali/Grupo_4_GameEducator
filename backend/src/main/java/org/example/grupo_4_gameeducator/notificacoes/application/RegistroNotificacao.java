package org.example.grupo_4_gameeducator.notificacoes.application;

import java.time.Instant;

public record RegistroNotificacao(
        Long alunoId,
        String mensagem,
        Status status,
        String detalhe,
        Instant em
) {
    public enum Status { ENVIADA, FALHOU }
}
