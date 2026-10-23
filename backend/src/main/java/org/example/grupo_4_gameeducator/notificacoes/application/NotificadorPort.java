package org.example.grupo_4_gameeducator.notificacoes.application;

/** PORTA de saida para o canal de notificacao (e-mail, push...). Lanca RuntimeException se falhar. */
public interface NotificadorPort {

    void enviar(Long alunoId, String mensagem);
}
