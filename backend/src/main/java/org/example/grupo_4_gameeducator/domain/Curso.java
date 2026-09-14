package org.example.grupo_4_gameeducator.domain;

public class Curso {

    private String nome;
    private StatusCurso status;

    public Curso(String nome) {
        this.nome = nome;
        this.status = StatusCurso.NAO_INICIADO;
    }

    public String getNome() {
        return nome;
    }

    public StatusCurso getStatus() {
        return status;
    }

    public void setStatus(StatusCurso status) {
        this.status = status;
    }
}
