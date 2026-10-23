package org.example.grupo_4_gameeducator.notificacoes.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/** Ferramenta do laboratorio (D4): liga/desliga a falha do consumidor em tempo de execucao. */
@Component
public class SimuladorDeFalha {

    private final AtomicBoolean ativa;

    public SimuladorDeFalha(@Value("${notificacoes.simular-falha:false}") boolean inicial) {
        this.ativa = new AtomicBoolean(inicial);
    }

    public boolean ativa() {
        return ativa.get();
    }

    public void definir(boolean valor) {
        ativa.set(valor);
    }
}
