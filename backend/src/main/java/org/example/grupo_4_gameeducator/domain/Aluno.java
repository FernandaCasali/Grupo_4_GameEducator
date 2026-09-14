package org.example.grupo_4_gameeducator.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Aluno {

    public static final String NOTIFICACAO_LIMITE_ATINGIDO = "LIMITE_ATINGIDO";

    private final String nome;
    private Plano plano;
    private int totalCursos;

    private final List<Curso> cursosDesbloqueados = new ArrayList<>();
    private boolean notificado;
    private final Set<String> notificacoes = new LinkedHashSet<>();

    private int moedas;
    private final List<String> historicoMoedas = new ArrayList<>();

    public Aluno(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setPlano(Plano plano) {
        this.plano = plano;
    }

    public Plano getPlano() {
        return plano;
    }

    public void setTotalCursos(int totalCursos) {
        this.totalCursos = totalCursos;
    }

    public int getTotalCursos() {
        return totalCursos;
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

    public Set<String> getNotificacoes() {
        return notificacoes;
    }

    public int getSaldoMoedas() {
        return moedas;
    }

    public void adicionarMoedas(int quantidade) {
        this.moedas += quantidade;
    }

    public void removerMoedas(int quantidade) {
        this.moedas -= quantidade;
    }

    public List<String> getHistoricoMoedas() {
        return historicoMoedas;
    }
}