package org.example.grupo_4_gameeducator.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "aluno")
public class AlunoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String planoNome;
    private int planoLimiteCursos;

    private int totalCursos;
    private int saldoMoedas;
    private boolean notificado;

    @ElementCollection
    @CollectionTable(name = "aluno_cursos_desbloqueados", joinColumns = @JoinColumn(name = "aluno_id"))
    @Column(name = "nome_curso")
    private List<String> cursosDesbloqueados = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "aluno_notificacoes", joinColumns = @JoinColumn(name = "aluno_id"))
    @Column(name = "notificacao")
    private Set<String> notificacoes = new LinkedHashSet<>();

    @ElementCollection
    @CollectionTable(name = "aluno_historico_moedas", joinColumns = @JoinColumn(name = "aluno_id"))
    @Column(name = "descricao")
    private List<String> historicoMoedas = new ArrayList<>();

    protected AlunoEntity() {
    }

    public AlunoEntity(String nome, String planoNome, int planoLimiteCursos) {
        this.nome = nome;
        this.planoNome = planoNome;
        this.planoLimiteCursos = planoLimiteCursos;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getPlanoNome() { return planoNome; }
    public int getPlanoLimiteCursos() { return planoLimiteCursos; }
    public int getTotalCursos() { return totalCursos; }
    public void setTotalCursos(int totalCursos) { this.totalCursos = totalCursos; }
    public int getSaldoMoedas() { return saldoMoedas; }
    public void setSaldoMoedas(int saldoMoedas) { this.saldoMoedas = saldoMoedas; }
    public boolean isNotificado() { return notificado; }
    public void setNotificado(boolean notificado) { this.notificado = notificado; }
    public List<String> getCursosDesbloqueados() { return cursosDesbloqueados; }
    public Set<String> getNotificacoes() { return notificacoes; }
    public List<String> getHistoricoMoedas() { return historicoMoedas; }
}