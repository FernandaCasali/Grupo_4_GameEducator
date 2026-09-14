package org.example.grupo_4_gameeducator.domain;

import java.util.Objects;

public class Curso {

    private String nome;
    private StatusCurso status;

    public Curso(String nome) {
        this.nome = nome;
        this.status = StatusCurso.NAO_INICIADO;
    }

    public Curso(String nome, StatusCurso status) {
        this.nome = nome;
        this.status = status;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Curso curso)) return false;
        return Objects.equals(nome, curso.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}