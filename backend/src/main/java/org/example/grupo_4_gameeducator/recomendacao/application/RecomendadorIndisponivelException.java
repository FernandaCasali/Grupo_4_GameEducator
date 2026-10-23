package org.example.grupo_4_gameeducator.recomendacao.application;

public class RecomendadorIndisponivelException extends RuntimeException {

    public RecomendadorIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
