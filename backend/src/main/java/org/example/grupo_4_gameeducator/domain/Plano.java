package org.example.grupo_4_gameeducator.domain;

public class Plano {

    private String nome;
    private int limiteCursos;

    public Plano(String nome, int limiteCursos) {
        this.nome = nome;
        this.limiteCursos = limiteCursos;
    }

    public String getNome() {
        return nome;
    }

    public int getLimiteCursos() {
        return limiteCursos;
    }
}