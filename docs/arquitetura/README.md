# Registro Arquitetural — Grupo 4 GameEducator (B1 · Architecture Lab)

Registro cumulativo. Cada desafio acrescenta uma seção. Regra de leitura de trás para frente:
**C4 ← ADR ← ASR + RPC ← RF/RNF**, com a evidência sustentando o raciocínio.

- Equipe: Fernanda Theodoro Casali - 235329 /
  Carolina Leamari - 235249 /
  Eduarda Gomes Abreu e Almeida - 236021
- Repositório: https://github.com/FernandaCasali/Grupo_4_GameEducator.git
- Branch do B1: `b1-architecture-part2`

---

## Matriz cumulativa de ASRs

| ASR | D1 Core | D2 Organização | D3 IA | D4 Eventos | D5 IoT | D6 CI/CD |
|---|---|---|---|---|---|----------|
| Manutenibilidade | ✅ | ✅ | | | |          |
| Escalabilidade | ✅ | | | | |          |
| Complexidade operacional | ✅ | | | | |          |
| Testabilidade | | ✅ | | | |          |
| Modificabilidade | | ✅ | | | |          |
| Deploy independente | | | ✅ | | |          |
| Tolerância a falhas | | | ✅ | ✅ | |          |
| Interoperabilidade | | | ✅ | | ✅ | ✅        |
| Desacoplamento | | | | ✅ | |          |
| Recuperabilidade | | | | ✅ | |          |
| Tolerância à conectividade intermitente | | | | | ✅ |          |
| Eficiência de comunicação | | | | | ✅ |          |

## Índice de ADRs

| ADR | Decisão | Desafio |
|---|---|---|
| ADR-001 | Monólito Modular | D1 |
| ADR-002 | Ports & Adapters no módulo `aluno` (persistência atrás de porta) | D2 |
| ADR-003 | Serviço Python independente (FastAPI) para IA, com degradação | D3 |
| ADR-004 | Eventos de domínio em processo + consumidor assíncrono (sem broker agora) | D4 |
| ADR-005 | MQTT com gateway consumidor para eventos de dispositivos | D5 |

---

# Desafio 1 — Drivers e evolução do Core

## 1. Cenário

O MVP de gamificação (AC1) passa de ~1.000 para até 100.000 usuários. O ranking tende a crescer mais que
as demais capacidades; moedas, desbloqueio de cursos e conversão em cripto continuam evoluindo.
Pergunta do Tech Lead: **há evidência para distribuição e deploy independente agora, ou uma
modularização interna resolve com menor custo operacional?**

## 2. RF afetados

| ID | Requisito | Onde está no código |
|---|---|---|
| RF01 | Ranking de alunos | **Novo** neste desafio: módulo `ranking` (`GET /api/ranking`) |
| RF02 | Conquistas / gamificação | Moedas por engajamento (US1), desbloqueio de cursos (US2), conversão em cripto (US3) |

## 3. RNF e seleção de ASRs

| RNF | ASR? | Justificativa |
|---|---|---|
| Manutenibilidade | **SIM** | As regras de moedas, desbloqueio e ranking mudam em ritmos diferentes; hoje estão misturadas no mesmo serviço (`AlunoService`/`AlunoAppService`). Fronteiras explícitas reduzem o impacto de mudança. |
| Escalabilidade | **SIM** | O Ranking é leitura que cresce mais que o resto; a estrutura precisa deixá-lo isolável para escalar à parte, se um dia for preciso. |
| Complexidade operacional | **SIM** | Equipe de 6 pessoas; microsserviços trariam rede, contratos, observabilidade e deploys múltiplos sem evidência de necessidade. |
| Desempenho | NÃO, agora | Relevante, mas não há medição de latência/throughput que prove gargalo arquitetural. |
| Testabilidade | NÃO como driver | Melhora com módulos, mas sozinha não decide a decomposição. |

## 4. RPC — restrições e premissas

| RPC | Consequência |
|---|---|
| Core em Java 21 / Spring Boot | Evolução preserva o investimento; sem troca de stack. |
| Equipe de 6 desenvolvedores | Complexidade operacional proporcional à equipe. |
| Banco relacional (H2 dev / PostgreSQL) | Sem troca de tecnologia de dados por modernização. |
| Legado da AC1 (API `/api/alunos/**`, testes, front Vue) | Evolução incremental, sem reescrita; **URLs e contratos preservados**. |

## 5. Alternativas e trade-offs

| Alternativa | Benefícios | Custos / riscos | Avaliação |
|---|---|---|---|
| A — Monólito em Camadas (estado atual) | Zero custo de mudança; operação simples. | Três capacidades no mesmo `AlunoService`/`AlunoAppService`; regra Premium duplicada; Ranking nasceria espalhado em 4 camadas. | Possível, mas despreparada para a evolução prevista. |
| **B — Monólito Modular** | Fronteiras por capacidade; um único deploy; testes por módulo; extração futura do Ranking facilitada. | Mesmo processo e mesmo banco; escala e deploy ainda conjuntos. | **Melhor equilíbrio entre ASRs e RPCs.** |
| C — Serviço(s) independente(s) (ex.: Ranking) | Escala e deploy independentes. | Rede, contratos, consistência, observabilidade, mais pipelines. | Prematuro: nada prova necessidade de escala/deploy independente. |

## 6. Decisão do Tech Lead

**Evoluir o Core para Monólito Modular**, organizado por capacidade de negócio:
`aluno` (cadastro e agregado), `cursos` (desbloqueio), `moedas` (engajamento e cripto) e `ranking` (RF01).
Continua um único processo Spring Boot, um banco e um deploy. O Ranking é **candidato explícito à extração futura**,
mas não vira serviço agora.

## 7. ADR-001 — Evoluir o Core de Monólito em Camadas para Monólito Modular

| Campo | Registro |
|---|---|
| Status | Aceito |
| Contexto | Crescimento para até 100 mil usuários; Ranking cresce mais; demais capacidades evoluem. A AC1 concentra moedas, desbloqueio e cripto em `AlunoService` e `AlunoAppService`. |
| ASRs | Manutenibilidade; Escalabilidade; Complexidade operacional. |
| RPCs | Java 21/Spring Boot; equipe de 6; banco relacional; legado da AC1 com contratos HTTP preservados. |
| Alternativas | A) Camadas; B) Modular; C) Serviços independentes. |
| Decisão | Adotar Monólito Modular. Módulos conversam apenas por contratos públicos (`api`); `internal` é privado ao módulo. |
| Consequências positivas | Responsabilidades separadas; regra Premium em um só lugar (`Aluno.isPremium()`); Ranking isolado e testável; fronteiras verificadas por teste (ArchUnit); custo operacional igual ao atual. |
| Trade-offs | Deploy ainda conjunto; processo e banco compartilhados; Ranking sem escala independente; `Aluno`, `Curso` e `Plano` ficam em `aluno.domain` como núcleo compartilhado (shared kernel). |
| Gatilho de revisão | Reavaliar extração do Ranking se métricas mostrarem gargalo de leitura, necessidade de escala própria ou frequência de deploy conflitante. |

## 8. C4 — Container

O Core permanece **um único container**; a organização interna passa a ser modular.

```mermaid
flowchart LR
    U[Aluno / Administrador] --> FE[Front-end Vue]
    FE -->|REST /api| CORE
    subgraph CORE[Core Spring Boot - Monolito Modular]
        direction TB
        RK[ranking]
        CU[cursos]
        MO[moedas]
        AL[aluno]
        RK --> AL
        CU --> AL
        MO --> AL
    end
    CORE -->|JPA| DB[(Banco relacional H2 / PostgreSQL)]
```

Detalhamento dos componentes internos (e portas/adaptadores) fica para o Desafio 2.

## 9. Estrutura dos módulos

```
aluno/        api (AlunoController, AlunoGateway, DTOs) · application · domain (Aluno, Plano, Curso) · internal/persistence
cursos/       api (+ CursoConcluidoEvent) · application · domain (DesbloqueioService)
moedas/       api · application · domain (MoedasService)
ranking/      api · application · domain (PontuacaoRanking)       (D1, D2)
conquistas/   api · application · domain (Badge, RegrasConquistas) (D2, RF02)
recomendacao/ api · application (porta) · internal (adapter HTTP)  (D3)
notificacoes/ api · application (porta) · internal (adapter)       (D4)
shared/       web (ApiExceptionHandler) · config (AsyncConfig)
iot/  api · application (porta) · domain · internal (adapter)  (D5)
```

Regras de dependência (verificadas em `arquitetura/FronteirasModulosTest`):

1. O pacote `internal` de cada módulo é privado ao módulo.
2. Sem ciclos entre módulos.
3. Cada módulo só conhece os módulos permitidos: `aluno` não conhece ninguém; `cursos`, `moedas`, `ranking` e `recomendacao` conhecem só `aluno` (via `AlunoGateway`); `notificacoes` conhece só o contrato público de `cursos` (o evento).

## 10. Mini-lab — evidência antes/depois

| Evidência | Antes (camadas) | Depois (modular) |
|---|---|---|
| Organização | `controller/ service/ domain/ entity/ repository/ dto/` | Por capacidade: `aluno`, `cursos`, `moedas`, `ranking` |
| `AlunoAppService` | Atende criar, listar, concluir curso, creditar moedas e converter cripto | Só cadastro/consulta + `atualizar(id, regra)`; regras ficam nos módulos |
| `AlunoService` (domínio) | Desbloqueio + moedas + cripto na mesma classe | `DesbloqueioService` e `MoedasService` separados |
| Regra Premium (12 cursos) | Duplicada em `AlunoService` e `AlunoAppService.toResponse` | Única, em `Aluno.isPremium()` |
| Dependências | Livres: qualquer classe usa qualquer outra | Explícitas e verificadas por teste |
| Testes | Um arquivo por camada, misturando capacidades | Testes por módulo; Ranking com testes próprios |
| Extrair o Ranking | Exigiria separar 4 camadas | Depende só de `AlunoGateway` |

Arquivos de apoio:

- `evidencias/antes-estrutura.txt` e `evidencias/depois-estrutura.txt` — árvore de classes.
- `evidencias/depois-testes.txt` — árvore de testes.
- `evidencias/mvn-test.png` — saída do `mvn test` (testes passando, incluindo ArchUnit).

![mvn-test.png](evidencias/mvn-test.png)

**Ranking (RF01) funcionando:**

![Ranking no Swagger](evidencias/d1-ranking-swagger.png)

### Como reproduzir

```bash
cd backend
mvn test                 # inclui testes de módulos e FronteirasModulosTest
mvn spring-boot:run      # GET http://localhost:8080/api/ranking
```

Preservação de comportamento (a comprovar com o `mvn test`): endpoints `/api/alunos/**` mantidos com os mesmos caminhos, e os testes da AC1
foram redistribuídos por módulo sem alterar as regras testadas.

### Evidência — Ranking até 1000 alunos (AC2)

O ranking foi validado para **todos os tamanhos de 1 a 1000 alunos** (`RankingCargaTest`), com dois cortes: **correção** (ordem decrescente, desempate por nome, posições sequenciais, fórmula `100 × totalCursos + moedas` e nenhum aluno repetido/perdido) e **tempo** (cada tamanho responde abaixo do limite declarado).

- Pior caso medido: **4 ms** com **240 alunos** (limite: 1000 ms).
- Reprodução: `cd backend` e `.\mvnw.cmd test -Dtest=RankingCargaTest`.

![Ranking de 1 a 1000 alunos](evidencias/d1-ranking-ate-1000.png)

**Limite declarado:** o teste mede o ranking em memória, sem rede nem banco. A escala alvo (até 100 mil alunos), a latência sob rede e o throughput sob concorrência ainda precisam ser medidos antes de qualquer decisão de extração.

## 11. Conclusão

A equipe decidiu evoluir o Core para Monólito Modular. A refatoração separou as capacidades sem introduzir
comunicação distribuída e sem alterar os endpoints existentes, e o RF01 (Ranking) entrou já dentro de um módulo
isolado. A extração do Ranking para serviço não foi adotada porque o cenário informa crescimento, mas não há
evidência de gargalo ou de necessidade de deploy independente. **Já medido:** o ranking responde em tempo
desprezível para até 1000 alunos (ver evidência acima). **Ainda precisa ser medido** antes de extrair: o
comportamento acima de 1000 alunos (até 100 mil), o throughput sob requisições concorrentes e a frequência de
mudança do módulo.

## 12. Rastreabilidade

| Elemento | Resposta |
|---|---|
| RF | RF01 Ranking; RF02 Conquistas (moedas, desbloqueio, cripto) |
| RNF | Manutenibilidade, escalabilidade, complexidade operacional, desempenho, testabilidade |
| ASR | Manutenibilidade; escalabilidade; complexidade operacional |
| RPC | Spring Boot; equipe de 6; banco relacional; legado AC1 |
| Alternativas | Camadas × Modular × Serviços |
| ADR | ADR-001 — Monólito Modular |
| C4 | Container: Core único com módulos internos |
| Evidência | Refatoração por módulo + Ranking + testes + ArchUnit + antes/depois + teste de carga do ranking de 1 a 1000 alunos |

> **Observações para a equipe:** a pontuação do Ranking (`100 × totalCursos + moedas`) é uma regra didática adotada pelo grupo.

---

# Desafio 2 — Organização interna do Core

## 1. Cenário

As regras de ranking, moedas, badges e recompensas mudam com frequência. É preciso evitar que domínio,
banco, framework e integrações fiquem fortemente acoplados.

Diagnóstico no código (antes do D2): `AlunoAppService` dependia diretamente de `AlunoRepository` (Spring Data)
e de `AlunoEntity` (JPA), e carregava a tradução entidade ↔ domínio. Para testar um caso de uso era
preciso simular o repositório JPA com Mockito.

## 2. RF afetados

RF01 Ranking; RF02 Conquistas e regras de gamificação.

## 3. RNF e seleção de ASRs

| RNF | ASR? | Como altera a arquitetura |
|---|---|---|
| Testabilidade | **SIM** | Casos de uso precisam rodar sem banco nem framework; isso decide **de que a aplicação pode depender** (porta, não JPA). |
| Modificabilidade | **SIM** | Trocar persistência ou regra não pode se espalhar; define **onde ficam as fronteiras** entre domínio, aplicação e adapters. |
| Manutenibilidade | **SIM** | Regras mudam com frequência; precisam viver em classes sem dependência técnica. |
| Facilidade de evolução | incorporada à modificabilidade | Mesmo efeito estrutural. |

## 4. RPC

Core continua Spring Boot; regras existentes (`DesbloqueioService`, `MoedasService`) são reaproveitadas;
**não** se reescreve o produto para aplicar um padrão.

## 5. Decisão do Tech Lead

- **Qual dependência precisa ser invertida?** Somente a **persistência**: a aplicação passa a depender da
  porta `AlunoRepositorio`; o JPA vira detalhe (`JpaAlunoRepositorio`).
- **O que NÃO se inverte:** controllers/DTOs (adapter de entrada HTTP já fino) e as regras de domínio, que já eram classes puras.
- **O custo é proporcional?** Sim: 1 interface, 1 adapter JPA e 1 adapter fake; o mapeamento entidade ↔ domínio já existia e apenas mudou de lugar.
- **DDD, Hexagonal, Clean e Onion são concorrentes?** Não. Usamos **Hexagonal** como estrutura (portas e adapters), com **conceitos táticos de DDD**
  (agregado `Aluno`, serviços de domínio). Clean/Onion completos (um caso de uso por classe, camadas concêntricas formais) seriam excesso para este tamanho.

## 6. Alternativas e trade-offs

| Alternativa | Benefícios | Custos / riscos | Avaliação |
|---|---|---|---|
| A — Manter como está (app service usa JPA) | Nenhum custo. | Testes dependem de Mockito sobre repositório JPA; trocar persistência atinge casos de uso. | Rejeitada: não atende testabilidade/modificabilidade. |
| **B — Ports & Adapters (Hexagonal) no módulo `aluno`** | Aplicação isolada de JPA; adapter fake para testes; regra verificável por ArchUnit. | Uma indireção a mais; `atualizar` faz duas leituras (buscar + salvar). | **Escolhida.** |
| C — Clean/Onion completo | Máximo isolamento formal. | Muitas classes e mappers; esforço desproporcional ao problema. | Rejeitada: custo estrutural maior que o benefício. |

## 7. ADR-002 — Persistência atrás de porta no módulo `aluno`

| Campo | Registro |
|---|---|
| Status | Aceito |
| Contexto | Casos de uso dependiam de JPA; regras mudam com frequência; testar exigia mocks de infraestrutura. |
| ASRs / RPCs | Testabilidade, modificabilidade, manutenibilidade / Spring Boot, reaproveitar regras, sem reescrita. |
| Alternativas | A) manter; B) Ports & Adapters; C) Clean/Onion completo. |
| Decisão | Criar a porta `aluno.application.AlunoRepositorio`; mover o mapeamento para o adapter `JpaAlunoRepositorio`; adotar `InMemoryAlunoRepositorio` como adapter de teste. |
| Consequências positivas | `AlunoAppService`, `CursosAppService` e `MoedasAppService` testados sem Spring, JPA ou Mockito; domínio sem dependência de framework (verificado). |
| Aplicação a outras portas | O mesmo padrão aparece na IA (`RecomendadorPort`, ADR-003) e nas notificações (`NotificadorPort`, ADR-004) |
| Trade-offs | Indireção extra; duas leituras por atualização (aceitável no volume atual; revisar com métricas). |
| Gatilho de revisão | Se a duplicação de leituras aparecer em métricas de latência, otimizar o adapter sem tocar na porta. |

## 8. C4 — Component (Core)

```mermaid
flowchart TB
    subgraph CORE[Core Spring Boot]
        direction TB
        subgraph ENTRADA[Adapters de entrada]
            CTL[Controllers REST<br/>aluno / cursos / moedas / ranking]
        end
        subgraph APP[Aplicação]
            APPS[AppServices<br/>AlunoAppService, CursosAppService, MoedasAppService]
            PORTA[[Porta AlunoRepositorio]]
        end
        subgraph DOM[Domínio]
            REG[Aluno, Plano, Curso<br/>DesbloqueioService, MoedasService]
        end
        subgraph SAIDA[Adapters de saída]
            JPA[JpaAlunoRepositorio]
            FAKE[InMemoryAlunoRepositorio<br/>somente testes]
        end
        CTL --> APPS
        APPS --> REG
        APPS --> PORTA
        JPA -. implementa .-> PORTA
        FAKE -. implementa .-> PORTA
    end
    JPA --> DB[(Banco relacional)]
```

## 9. Mini-lab / evidência

| Evidência | Resultado esperado |
|---|---|
| `InMemoryAlunoRepositorio` no lugar do JPA | `AlunoAppServiceTest`, `CursosAppServiceTest` e `MoedasAppServiceTest` passam **sem** Spring, JPA ou Mockito |
| `DesbloqueioServiceTest` e `MoedasServiceTest` (regras de domínio) | Continuam iguais: a regra foi isolada e reaproveitada |
| `JpaAlunoRepositorioTest` | Tradução entidade ↔ domínio (planos vazio/nulo, cursos, notificado) preservada |
| `FronteirasModulosTest` | Domínio não depende de Spring/JPA; aplicação não conhece adapters nem JPA |
| `PontuacaoRankingTest` (regra de ranking no domínio) | A fórmula do ranking é Java puro, testada sem Spring; trocar o peso afeta uma só classe |
| `RegrasConquistasTest` (badges, RF02) | A regra de conquistas é Java puro, testada sem Spring; novo badge ou limite afeta uma só classe |

## 10. Conclusão

A única inversão necessária era a de persistência. Ela foi feita com custo baixo, preservou as regras e os endpoints,
e tornou os casos de uso testáveis sem infraestrutura. **Falta medir:** o impacto das duas leituras por atualização.

---

# Desafio 3 — Serviço Python e IA

## 1. Cenário

A plataforma incorpora recomendação inteligente (RF03) e assistente educacional com RAG/LLM (RF04). Os modelos serão
em Python, com ciclo de atualização próprio e carga variável, e **podem ficar indisponíveis sem impedir operações essenciais**.

## 2. RF e RNF

RF03 Recomendação inteligente; RF04 Assistente educacional.

| RNF | ASR? | Como altera a arquitetura |
|---|---|---|
| Deploy independente | **SIM** | Modelos têm ciclo de atualização próprio; exige processo/artefato separado do Core Java. |
| Tolerância a falhas | **SIM** | A IA pode cair; define **comunicação com timeout, fallback e degradação** em vez de chamada direta sem proteção. |
| Interoperabilidade | **SIM** | Java ↔ Python exige contrato neutro de linguagem (HTTP/JSON), que fixa **o protocolo**. |
| Escalabilidade | não agora | Carga é variável, mas não há medição; a separação em serviço a habilita sem decidir nada além disso. |
| Desempenho | não agora | Sem medição de latência de inferência. |
| Manutenibilidade | atendida por consequência | — |

## 3. RPC

Core permanece Java/Spring Boot; ML/RAG em Python; indisponibilidade da IA não pode bloquear o fluxo essencial.

## 4. Decisão do Tech Lead

- **Python por si só justifica um serviço?** Não. O que justifica é a combinação **deploy independente + isolamento de falha + runtime e dependências diferentes**.
- **ML e RAG na mesma fronteira?** Por ora **um único `ia-service`**, com módulos internos separados por capacidade. Separar em dois serviços
  apenas se aparecerem ASRs distintos (ex.: RAG com LLM externo, alta latência e custo próprio, versus recomendação com re-treino periódico).
- **Comportamento degradado aceitável:** recomendação devolve lista vazia com `degradado=true` e `origem=FALLBACK_SEM_IA`; o assistente (RF04, ainda não implementado) responderia que está indisponível. Conclusão de curso, moedas, ranking e desbloqueio **não dependem da IA**.
- **Escopo implementado no lab:** somente RF03 (recomendação). O assistente RAG (RF04) **não foi implementado**; a fronteira o comporta.

## 5. Alternativas e trade-offs

| Alternativa | Benefícios | Custos / riscos | Avaliação |
|---|---|---|---|
| A — IA embutida no Core | Sem rede; um deploy. | Ciclo de modelo preso ao do Core; falha/uso de memória da IA derruba o Core; stack Python incompatível com RPC. | Rejeitada. |
| B — Módulo acoplado no monólito | Fronteira lógica barata. | Não resolve linguagem (RPC) nem deploy independente. | Rejeitada. |
| **C — Serviço Python independente** | Deploy próprio; falha isolada; contrato claro. | Rede, timeouts, observabilidade, mais um artefato. | **Escolhida.** |

## 6. ADR-003 — Serviço Python independente para IA

| Campo | Registro |
|---|---|
| Status | Aceito |
| Contexto | Recomendação (e futuramente RAG) em Python, ciclo próprio, carga variável, não pode bloquear o Core. |
| ASRs / RPCs | Deploy independente, tolerância a falhas, interoperabilidade / Core Java, IA em Python, fluxo essencial independente da IA. |
| Alternativas | A) embutida; B) módulo acoplado; C) serviço Python. |
| Decisão | `ia-service` (FastAPI) exposto por HTTP/JSON síncrono. No Core, módulo `recomendacao` com **porta** `RecomendadorPort`, adapter HTTP com timeout de 1,5 s e fallback degradado. Um único serviço para ML e RAG por ora. |
| Consequências positivas | Falha da IA não derruba o Core (comprovado por testes); contrato independente de linguagem; Core não conhece Python. |
| Trade-offs | Latência de rede; novo componente operacional; recomendações indisponíveis durante a queda. Descoberta no mini-lab: o cliente HTTP do Java (JDK) tenta negociar HTTP/2 e o uvicorn rejeita o upgrade (corpo perdido, erro 422); o adapter força HTTP/1.1.|
| Gatilho de revisão | Separar ML e RAG se escala/latência/ciclo divergirem; trocar HTTP por fila se a recomendação virar processamento em lote. |

## 7. C4 — Container (atualizado)

```mermaid
flowchart LR
    U[Aluno] --> FE[Front-end Vue]
    FE -->|REST| CORE[Core Spring Boot<br/>Monólito Modular]
    CORE -->|JPA| DB[(PostgreSQL)]
    CORE -->|HTTP/JSON<br/>timeout 1,5 s| IA[ia-service<br/>Python / FastAPI]
    IA -.->|futuro: RAG| LLM[(LLM / base vetorial)]
```

## 8. Mini-lab / evidência

| Evidência | Onde |
|---|---|
| Spring consumindo FastAPI | `GET /api/alunos/{id}/recomendacoes` → `IaServiceRecomendador` → `POST /recomendacoes` |
| Contrato do adapter (resposta 200) | `IaServiceRecomendadorTest.deveInterpretarRespostaDoIaService` (servidor HTTP falso do JDK, sem Docker) |
| Falha: serviço parado | `IaServiceRecomendadorTest.deveLancarIndisponivelQuandoServicoEstaForaDoAr` (porta fechada) |
| Falha: erro 500 | `IaServiceRecomendadorTest.deveLancarIndisponivelQuandoServicoRetornaErro500` |
| Degradação | `RecomendacaoAppServiceTest.deveDegradarSemQuebrarQuandoIaEstaIndisponivel` |
| Serviço Python | `ia-service/tests` — 6 testes (resultado real em `evidencias/d3-pytest-ia-service.txt`) |

![d3-1-ia-fora-degradado.png](evidencias/d3-1-ia-fora-degradado.png)
![d3-2-ia-no-ar.png](evidencias/d3-2-ia-no-ar.png)

**Roteiro manual (fazer e anexar prints em `evidencias/`):**

1. `docker compose up --build` (ou `uvicorn app.main:app --port 8000` + `mvn spring-boot:run`).
2. Criar aluno, concluir um curso, chamar `GET /api/alunos/1/recomendacoes` → `degradado=false`.
3. `docker compose stop ia-service` e repetir a chamada → `degradado=true`; `POST /concluir-curso` e `/moedas` continuam respondendo 200.

## 9. Conclusão

O Core ganhou recomendação sem conhecer Python e sem depender dele para as operações essenciais. **Falta medir:** latência real da inferência e
taxa de indisponibilidade, que dirão se o timeout de 1,5 s e o HTTP síncrono continuam adequados. O assistente RAG permanece como trabalho futuro.

---

# Desafio 4 — Comunicação e eventos

## 1. Cenário

Ao concluir um curso, o sistema registra a conclusão, calcula recompensas, atualiza informações e pode disparar
notificação/analytics. **Falha de notificação não pode impedir a conclusão.**

## 2. RF e RNF

RF02 Conquistas; RF05 Notificações.

| RNF | ASR? | Como altera a arquitetura |
|---|---|---|
| Tolerância a falhas | **SIM** | Define **que a notificação sai da transação principal** e que seus erros não sobem para o chamador. |
| Desacoplamento | **SIM** | `cursos` não pode conhecer `notificacoes`; a **comunicação passa a ser por evento**. |
| Recuperabilidade | **SIM** | O que falhou precisa ser registrado e reprocessável: exige **estado de entrega** e operação de reenvio. |
| Disponibilidade | coberta pela tolerância a falhas | — |
| Latência | monitorada | O caminho crítico continua síncrono e curto. |
| Consistência | **forte onde importa** | Conclusão, desbloqueio e saldo ficam consistentes na operação principal. |

## 3. RPC

Operações essenciais do Core devem concluir mesmo com consumidor externo indisponível; adotar broker tem custo operacional.

## 4. Decisão do Tech Lead

- **O que precisa ser imediato (síncrono):** a resposta de `concluir-curso` (cursos liberados, notificação de limite no próprio aluno, saldo).
- **O que pode ser eventual (assíncrono):** notificação ao aluno e analytics.
- **Que ASR justificaria mensageria?** Durabilidade entre reinícios e vários consumidores independentes/externos. Hoje há **um** consumidor, no mesmo processo.
- **RabbitMQ/Kafka resolvem um problema real do fluxo?** Ainda não. Seriam necessários se a notificação virasse serviço externo, se houvesse
  perda inaceitável de eventos em queda do processo, ou se surgissem vários consumidores (analytics, IoT).

## 5. Alternativas e trade-offs

| Alternativa | Benefícios | Custos / riscos | Avaliação |
|---|---|---|---|
| A — REST síncrono ao notificador | Simples. | A falha do notificador atinge a conclusão do curso; acoplamento temporal. | Rejeitada: viola a RPC. |
| **B — Evento em processo + consumidor `@Async`** | Zero infraestrutura nova; falha isolada; fácil de testar; desacopla os módulos. | Evento em memória pode se perder se o processo cair entre a persistência e o consumo. | **Escolhida agora.** |
| C — RabbitMQ | Entrega durável, retry/DLQ, vários consumidores. | Novo componente operacional, monitoramento, equipe de 6. | Adiar até haver gatilho. |
| D — Kafka | Log de eventos, replay, altíssimo volume. | O maior custo operacional; volume atual não justifica. | Rejeitada. |

## 6. ADR-004 — Eventos de domínio em processo com consumidor assíncrono

| Campo | Registro |
|---|---|
| Status | Aceito |
| Contexto | Conclusão de curso dispara notificação; falha de notificação não pode afetar a conclusão. |
| ASRs / RPCs | Tolerância a falhas, desacoplamento, recuperabilidade / operações essenciais independentes do consumidor; custo de broker. |
| Alternativas | A) REST síncrono; B) evento em processo; C) RabbitMQ; D) Kafka. |
| Decisão | `cursos` publica `CursoConcluidoEvent` **depois** de concluir (erros de publicação são engolidos e logados). `notificacoes` consome com `@Async @EventListener`, registra `ENVIADA`/`FALHOU` e permite `POST /api/notificacoes/reenviar`. Sem broker. |
| Consequências positivas | Falha do consumidor não afeta a conclusão (comprovado); módulos desacoplados (ArchUnit); recuperação por reenvio. |
| Trade-offs | Perda possível de evento se o processo cair antes do consumo; histórico em memória (perde-se ao reiniciar). |
| Gatilho de revisão | Adotar **outbox + RabbitMQ** quando: (1) a notificação virar serviço externo, (2) a perda de evento for inaceitável, ou (3) houver 2+ consumidores independentes. |

## 7. C4 — Container (atualizado) e sequência

```mermaid
flowchart LR
    U[Aluno] --> FE[Front-end Vue]
    FE -->|REST| CORE
    subgraph CORE[Core Spring Boot - Monólito Modular]
        CU[cursos] -->|evento CursoConcluido<br/>em processo| NO[notificacoes]
        CU --> AL[aluno]
    end
    CORE -->|JPA| DB[(PostgreSQL)]
    CORE -->|HTTP/JSON| IA[ia-service Python]
```

```mermaid
sequenceDiagram
    participant A as Aluno
    participant C as cursos (síncrono)
    participant AL as aluno
    participant N as notificacoes (assíncrono)
    A->>C: POST /concluir-curso
    C->>AL: atualizar(regras de desbloqueio)
    AL-->>C: aluno atualizado
    C-->>A: 200 OK (resposta imediata)
    C--)N: CursoConcluidoEvent
    alt consumidor disponível
        N->>N: registra ENVIADA
    else consumidor indisponível
        N->>N: registra FALHOU (conclusão intacta)
        N->>N: POST /reenviar recupera depois
    end
```

## 8. Mini-lab / evidência

| Evidência | Onde |
|---|---|
| Interação **síncrona** | `POST /api/alunos/{id}/concluir-curso` devolve a resposta completa ao aluno |
| Interação **assíncrona** | `NotificacaoListener` (`@Async @EventListener`) reage a `CursoConcluidoEvent` |
| Conclusão não é afetada por falha ao publicar | `CursosAppServiceTest.falhaNaPublicacaoDoEventoNaoImpedeAConclusaoDoCurso` |
| Consumidor indisponível vira `FALHOU` sem exceção | `NotificacaoAppServiceTest.consumidorIndisponivelRegistraFalhaSemLancarExcecao` |
| Recuperação | `NotificacaoAppServiceTest.deveRecuperarNotificacoesFalhasQuandoConsumidorVolta` |
| Acoplamento | `FronteirasModulosTest` — `cursos` não conhece `notificacoes` |

![d4-1-conclusao-ok.png](evidencias/d4-1-conclusao-ok.png)
![d4-2-notificacao-falhou.png](evidencias/d4-2-notificacao-falhou.png)
![d4-3-reenvio.png](evidencias/d4-3-reenvio.png)
![d4-4-notificacao-enviada.png](evidencias/d4-4-notificacao-enviada.png)

**Roteiro manual (fazer e anexar prints):**

1. `PUT /api/notificacoes/simulacao-falha/true` (consumidor "cai").
2. `POST /api/alunos/1/concluir-curso` → **200**, cursos liberados.
3. `GET /api/notificacoes` → registro `FALHOU`.
4. `PUT /api/notificacoes/simulacao-falha/false` → `POST /api/notificacoes/reenviar` → `{"recuperadas":1}` → `GET` mostra `ENVIADA`.

## 9. Conclusão

A conclusão do curso é síncrona e consistente; a notificação é eventual, isolada e recuperável, sem introduzir broker. A decisão de **não** usar RabbitMQ/Kafka
é deliberada e tem gatilhos explícitos. **Falta medir:** frequência real de falhas do consumidor e volume de eventos, para validar o adiamento do broker.


---

# Desafio 5 — IoT e MQTT

## 1. Cenário

Laboratórios passam a produzir eventos de presença, início e conclusão de atividades por dispositivos com recursos
limitados e conexão instável. Esses eventos devem entrar na plataforma **sem** transformar o Core em um acoplamento
direto com cada dispositivo.

## 2. RF e RNF

RF06 — receber eventos IoT e encaminhá-los ao ecossistema da plataforma.

| RNF | ASR? | Como altera a arquitetura |
|---|---|---|
| Tolerância à conectividade intermitente | **SIM** | Dispositivos caem e voltam; define **mecanismo de entrega**: sessão persistente, QoS 1, ack só depois do Core aceitar. |
| Eficiência de comunicação | **SIM** | Dispositivos de baixo poder: decide **o protocolo** (pub/sub leve com conexão persistente) em vez de um request HTTP completo por evento. |
| Interoperabilidade | **SIM** | O Core não pode conhecer cada dispositivo: exige um **contrato de evento normalizado** e um componente de tradução (gateway) entre o protocolo do dispositivo e o Core. |
| Segurança de integração | não agora | Relevante, mas não muda a estrutura: o laboratório roda sem TLS; em produção exige TLS (8883), autenticação e ACL por dispositivo (registrado como dívida técnica e gatilho de revisão). |
| Escalabilidade | não agora | Sem medição de volume; o broker escala à parte se for preciso. |
| Disponibilidade | coberta pela tolerância | O broker guarda os eventos enquanto o consumidor está fora. |

## 3. RPC — restrições e premissas

- Dispositivos podem ter baixo poder computacional (payload curto, pouca lógica, sem cliente HTTP pesado).
- A conectividade pode ser intermitente.
- Protocolo e broker **adicionam componentes operacionais** (equipe de 6 pessoas).
- Core permanece Java/Spring Boot; operações do Core não podem depender da disponibilidade dos dispositivos.

## 4. Decisão do Tech Lead

- **REST é suficiente?** Não para este cenário. Cada evento exigiria conexão e cabeçalhos HTTP completos, o dispositivo teria de
  implementar retry e fila por conta própria, e o Core ficaria exposto e acoplado à disponibilidade de cada dispositivo.
- **O que o MQTT resolve especificamente?** Cabeçalho mínimo, conexão persistente, QoS (entrega confirmada), **sessão persistente**
  (o broker guarda as mensagens de um assinante que está offline) e desacoplamento publicador/assinante.
- **Existe motivo para outro broker depois do MQTT?** Não agora. O próprio broker MQTT já faz a fila entre dispositivos e gateway.
  RabbitMQ só entraria se houvesse 2+ consumidores internos independentes com retry/DLQ próprios (os mesmos gatilhos do ADR-004).
- **Duplicidade:** MQTT QoS 1 entrega "pelo menos uma vez", então há duplicatas. Em vez de QoS 2 (handshake caro para o dispositivo),
  o Core é **idempotente por `eventoId`**.
- **Perda:** sessão persistente + QoS 1 + ack manual do gateway (o broker só descarta a mensagem depois que o Core aceitou).
  A perda residual é a de eventos que o dispositivo nunca conseguiu publicar (buffer offline do firmware, fora do laboratório).

## 5. Alternativas e trade-offs

| Alternativa | Benefícios | Custos / riscos | Avaliação |
|---|---|---|---|
| A — REST direto dos dispositivos ao Core | Sem broker novo; simples de entender. | Handshake e cabeçalhos pesados; retry e fila no dispositivo; Core acoplado a cada dispositivo e à sua conectividade. | Rejeitada: não atende eficiência nem tolerância à conectividade. |
| **B — MQTT + gateway consumidor → Core** | Protocolo leve; broker guarda eventos com o consumidor fora; Core isolado do protocolo; contrato normalizado. | Dois componentes novos (broker e gateway); fila de pendentes do gateway ainda em memória. | **Escolhida.** |
| C — MQTT + RabbitMQ/mensageria interna | Retry/DLQ e vários consumidores. | Terceiro componente operacional sem ASR que o justifique hoje. | Adiada: só com os gatilhos do ADR-004. |

## 6. ADR-005 — MQTT com gateway consumidor para eventos de dispositivos

| Campo | Registro |
|---|---|
| Status | Aceito |
| Contexto | Dispositivos de laboratório (baixo poder, conexão instável) precisam entregar eventos sem acoplar o Core a eles. |
| ASRs / RPCs | Tolerância à conectividade intermitente, eficiência de comunicação, interoperabilidade / baixo poder, conexão instável, componentes operacionais extras. |
| Alternativas | A) REST direto; B) MQTT + gateway; C) MQTT + RabbitMQ. |
| Decisão | Broker **Mosquitto**; tópico `labs/<laboratorio>/dispositivos/<dispositivo>/eventos`; **gateway Python** (`iot-gateway`) com sessão persistente, QoS 1 e ack manual, que traduz o payload compacto e chama `POST /api/iot/eventos`; no Core, módulo `iot` **idempotente por `eventoId`**, com porta de persistência. |
| Consequências positivas | O Core não conhece dispositivos nem MQTT; eventos sobrevivem a Core ou gateway fora do ar; duplicatas são inofensivas. |
| Trade-offs | Dois componentes operacionais novos; fila de pendentes em memória; laboratório sem TLS/autenticação; sem ordenação global garantida entre dispositivos. |
| Gatilho de revisão | TLS + autenticação + ACL antes de produção; fila de pendentes persistente (outbox) se a perda do gateway for inaceitável; RabbitMQ se surgirem 2+ consumidores independentes. |

## 7. C4 — Container (atualizado) e fluxo do evento

```mermaid
flowchart LR
    DISP[Dispositivos do laboratório<br/>presença / início / conclusão]
    BROKER[(Mosquitto<br/>broker MQTT)]
    GW[iot-gateway<br/>Python]
    subgraph CORE[Core Spring Boot]
        IOT[módulo iot<br/>idempotente por eventoId]
    end
    DISP -- "MQTT QoS 1<br/>payload compacto" --> BROKER
    BROKER -- "MQTT QoS 1<br/>sessão persistente" --> GW
    GW -- "HTTP/JSON<br/>POST /api/iot/eventos" --> IOT
```

```mermaid
sequenceDiagram
    participant D as Dispositivo
    participant B as Mosquitto
    participant G as iot-gateway
    participant C as Core (iot)
    D->>B: PUBLISH QoS 1 {id, t, a, ts}
    B-->>D: PUBACK
    B->>G: PUBLISH QoS 1
    G->>G: transforma em evento normalizado
    G->>C: POST /api/iot/eventos
    alt Core aceita
        C-->>G: 201 (novo) ou 200 (duplicado)
        G-->>B: PUBACK (só agora)
    else Core fora do ar
        G--xC: falha
        Note over G,B: sem PUBACK: evento fica pendente e é reenviado
    end
```

## 8. Mini-lab / evidência

**Hipótese:** o gateway isola o Core do protocolo e dos dispositivos, e a combinação QoS 1 + sessão persistente + ack manual +
idempotência mantém os eventos mesmo com o Core ou o gateway fora do ar.

Componentes: `iot-gateway/` (gateway, simulador, `mosquitto.conf`), `docker-compose.yml` (serviços `mosquitto` e `iot-gateway`) e o
módulo `iot` do Core (`POST/GET /api/iot/eventos`). Como reproduzir: ver `iot-gateway/README.md`.

| Cenário | Resultado esperado | Evidência |
|---|---|---|
| Simulador publica presença, início e conclusão; o gateway consome e transforma | 3 eventos `REGISTRADO` no Core | ![d5-1-simulador-e-gateway.png](evidencias/d5-1-simulador-e-gateway.png) |
| Mesmo evento de conclusão publicado duas vezes (duplicidade) | 2º envio devolve `DUPLICADO`; o Core mantém um só registro | ![d5-2-core-eventos.png](evidencias/d5-2-core-eventos.png) |
| **Core fora do ar** | Gateway registra `PENDENTE` (sem ack); ao voltar o Core, "Reenvio: 1 evento(s) entregue(s)" | ![d5-3-core-fora.png](evidencias/d5-3-core-fora.png) |
| **Gateway fora do ar** | O broker guarda o evento; ao voltar, o gateway o entrega ao Core | ![d5-4-gateway-fora.png](evidencias/d5-4-gateway-fora.png) |
| Testes automatizados | `iot-gateway`: 29 testes pytest; Core: testes do módulo `iot` e fronteiras (ArchUnit) | ![d5-5-pytest.png](evidencias/d5-5-pytest.png) |

## 9. Conclusão

REST direto não protege os dispositivos nem o Core; o MQTT resolve o problema real (conexão instável, baixo poder) e o gateway mantém o
Core isolado do protocolo. Um segundo broker (RabbitMQ) **não** se justifica hoje. **Falta medir:** latência fim a fim e volume por
laboratório, e **fechar a segurança** (TLS, autenticação, ACL) antes de produção. O Core hoje **registra** os eventos; encaminhá-los ao fluxo de
conquistas (por exemplo, `CONCLUSAO_ATIVIDADE` concluindo um curso) é a próxima evolução.



---

# Fechamento do B1 — Visão consolidada

## C4 — Container final (D1 a D5)

Visão integrada das decisões dos cinco desafios já aceitos. O **Desafio 6 (CI/CD e
observabilidade)** será incorporado ao diagrama quando o ADR-006 for aceito.

```mermaid
flowchart LR
    U[Aluno / Administrador] --> FE[Front-end Vue]
    FE -->|REST /api| CORE
    subgraph CORE[Core Spring Boot - Monolito Modular]
        direction TB
        AL[aluno]
        CU[cursos] -->|evento CursoConcluido<br/>em processo| NO[notificacoes]
        MO[moedas]
        RK[ranking]
        CQ[conquistas]
        RE[recomendacao<br/>porta + adapter HTTP]
        IOT[iot<br/>idempotente por eventoId]
        CU --> AL
        MO --> AL
        RK --> AL
        CQ --> AL
        RE --> AL
    end
    CORE -->|JPA| DB[(PostgreSQL)]
    RE -->|HTTP/JSON<br/>timeout 1,5 s| IA[ia-service<br/>Python / FastAPI]
    IA -.->|futuro: RAG| LLM[(LLM / base vetorial)]
    DISP[Dispositivos do laboratorio] -- "MQTT QoS 1" --> BROKER[(Mosquitto)]
    BROKER -- "MQTT QoS 1<br/>sessao persistente" --> GW[iot-gateway<br/>Python]
    GW -- "HTTP/JSON<br/>POST /api/iot/eventos" --> IOT
```

Legenda das fronteiras (confirmadas por `FronteirasModulosTest`):

- **Monólito Modular** (D1 / ADR-001): um único deploy, módulos conversam só pelos `api` públicos.
- **Ports & Adapters** no módulo `aluno` (D2 / ADR-002): persistência atrás da porta `AlunoRepositorio`.
- **Serviço Python** (D3 / ADR-003): IA fora do Core, HTTP/JSON com timeout e fallback degradado.
- **Eventos de domínio em processo** (D4 / ADR-004): `cursos` publica `CursoConcluidoEvent`; `notificacoes` consome `@Async`, sem broker.
- **MQTT + gateway** (D5 / ADR-005): dispositivos entram no Core por `iot-gateway`, idempotência por `eventoId`.

## Síntese final das decisões para a PoC da AF

A arquitetura entregue no B1 é a que o grupo leva para a PoC da AF. A linha condutora é
**decidir por evidência, não por catálogo**: cada ADR nasceu de ASRs priorizados contra RPCs
reais (equipe de 6 pessoas, Java 21/Spring Boot, banco relacional, legado da AC1 preservado) e
cada “não” (sem microsserviços, sem broker, sem Clean completo, sem outro protocolo além de
HTTP e MQTT) tem gatilho explícito de revisão.

| # | Decisão (ADR) | O que vai para a PoC | Gatilho que faria mudar |
|---|---|---|---|
| D1 / ADR-001 | Core em **Monólito Modular** com módulos `aluno`, `cursos`, `moedas`, `ranking`, `conquistas`, `recomendacao`, `notificacoes`, `iot`. | Um único deploy do Core; Ranking já isolável; teste de carga do ranking de 1 a 1000 alunos valida o módulo. | Métricas mostrando gargalo no `GET /api/ranking` ou cadência de deploy do Ranking conflitando com o restante. |
| D2 / ADR-002 | **Ports & Adapters** no `aluno` (porta `AlunoRepositorio`, adapter JPA, adapter em memória para testes). | Casos de uso sem Spring/JPA, regras de domínio puras, troca de persistência confinada ao adapter. | Latência da dupla leitura (`buscar` + `salvar`) aparecer nas métricas. |
| D3 / ADR-003 | **`ia-service` Python (FastAPI)** chamado por HTTP/JSON síncrono, com timeout de 1,5 s e degradação transparente. | Recomendação disponível com o Core isolado da falha da IA; contrato independente de linguagem; caminho pronto para o RAG (RF04) sem tocar no Core. | Separar ML de RAG se latência/custo/ciclo divergirem; trocar HTTP por fila se virar lote. |
| D4 / ADR-004 | **Eventos de domínio em processo + consumidor `@Async`** (sem broker); reenvio manual de notificações. | Conclusão de curso continua síncrona e consistente; notificação é eventual, isolada e recuperável. | Notificação virar serviço externo, perda de evento ser inaceitável ou surgirem 2+ consumidores independentes → outbox + RabbitMQ. |
| D5 / ADR-005 | **MQTT (Mosquitto) + `iot-gateway` Python** com QoS 1, sessão persistente, ack manual e idempotência por `eventoId` no Core. | Dispositivos entram sem acoplar o Core ao protocolo nem à conectividade deles; eventos sobrevivem a Core ou gateway fora do ar. | TLS/auth/ACL antes de produção; fila de pendentes persistente; RabbitMQ só se vierem 2+ consumidores. |
| D6 / ADR-006 | **Pendente** — CI/CD e observabilidade (responsável: Eduarda). | A definir no ADR-006. | A definir. |

### O que ainda precisa ser medido

Lista unificada do que cada ADR deixou como “falta medir”, para a PoC decidir onde investir
observabilidade primeiro:

- **D1**: comportamento do ranking acima de 1000 alunos (até 100 mil), throughput sob concorrência e frequência de mudança do módulo.
- **D2**: custo real da dupla leitura por atualização.
- **D3**: latência de inferência e taxa de indisponibilidade do `ia-service` (para validar o timeout de 1,5 s).
- **D4**: frequência de falha do consumidor e volume de eventos (para validar o adiamento do broker).
- **D5**: latência fim a fim e volume por laboratório; segurança (TLS, autenticação e ACL) antes de produção.

## Checagem da entrega

| Item | Onde conferir | Status |
|---|---|---|
| Nomes da equipe e matrículas | Cabeçalho deste README | Fernanda Theodoro Casali – 235329 · Carolina Leamari – 235249 · Eduarda Gomes Abreu e Almeida – 236021 |
| Repositório | https://github.com/FernandaCasali/Grupo_4_GameEducator | Público |
| Branch do B1 | `b1-architecture-lab` (consolidada) | Confirmar com o grupo |
| ADRs aceitos | ADR-001 (D1), ADR-002 (D2), ADR-003 (D3), ADR-004 (D4), ADR-005 (D5) | Todos no índice de ADRs |
| ADR pendente | ADR-006 (D6 · CI/CD e observabilidade) | A entregar pela Eduarda |
| Evidências do D1 | `evidencias/mvn-test.png`, `evidencias/d1-ranking-swagger.png`, `evidencias/d1-ranking-ate-1000.png` | Anexadas |
| Evidências do D2 | `evidencias/d2-testes-sem-infra.png`, `evidencias/antes-estrutura.txt`, `evidencias/depois-estrutura.txt`, `evidencias/depois-testes.txt` | Anexadas |
| Evidências do D3 | `evidencias/d3-1-ia-fora-degradado.png`, `evidencias/d3-2-ia-no-ar.png`, `evidencias/d3-demo-ia-service.txt`, `evidencias/d3-pytest-ia-service.txt` | Anexadas |
| Evidências do D4 | `evidencias/d4-1-conclusao-ok.png`, `evidencias/d4-2-notificacao-falhou.png`, `evidencias/d4-3-reenvio.png`, `evidencias/d4-4-notificacao-enviada.png` | Anexadas |
| Evidências do D5 | `evidencias/d5-1-simulador-e-gateway.png`, `evidencias/d5-2-core-eventos.png`, `evidencias/d5-3-core-fora.png`, `evidencias/d5-4-gateway-fora.png`, `evidencias/d5-5-pytest.png` | Anexadas |
| Evidências do D6 | A anexar pela Eduarda | Pendente |
