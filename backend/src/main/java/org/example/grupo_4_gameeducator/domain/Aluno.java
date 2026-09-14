package org.example.grupo_4_gameeducator.domain;

import java.util.ArrayList;
import java.util.List;

public class Aluno {

    private final String nome;
    private final List<Curso> cursosDesbloqueados = new ArrayList<>();
    private boolean notificado;

    public Aluno(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public List<Curso> getCursosDesbloqueados() {
        return cursosDesbloqueados;
    }

    public boolean foiNotificado() {
        return notificado;
    }

    public void marcarComoNotificado() {
        this.notificado = true;
    }
}
