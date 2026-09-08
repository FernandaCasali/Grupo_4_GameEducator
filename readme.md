# Grupo_4_GameEducator — Educação Continuada Gamificada

Estudo de caso desenvolvido em aula aplicando **ATDD** (Acceptance Test-Driven
Development), integrando o fluxo **User Stories → BDD (Scenarios / Acceptance
Criteria) → TDD**.

## Descrição do estudo de caso

Case **Educação Continuada Gamificada**, apresentado em aula.

Plataforma de cursos online em que o aluno acumula moedas conforme seu
engajamento (conclusão de cursos e participação no fórum), acompanha saldo e
histórico, tem novos cursos liberados automaticamente ao atingir bom
desempenho e, no plano Premium, pode converter as moedas acumuladas em
criptomoeda. O objetivo é incentivar o progresso contínuo na trilha de
aprendizagem por meio de recompensas e gamificação.

## Equipe e User Stories

Cada integrante redigiu **uma User Story**. Identificação de quem escreveu cada uma:

| Integrante | US redigida | Perfil | Resumo da US |
|---|---|---|---|
| **Carolina** | US1 | ALU | COMO aluno, QUERO acumular moedas conforme meu engajamento e visualizar saldo e histórico, PARA acompanhar meu progresso e as recompensas conquistadas. |
| **Eduarda** | US2 | ALU | COMO aluno, QUERO que o sistema libere automaticamente 3 novos cursos ao concluir um curso com média ≥ 7,0, PARA avançar na trilha sem depender de liberação manual. |
| **Fernanda** | US3 | ALU-PREM | COMO aluno Premium (12 cursos conquistados), QUERO converter as moedas acumuladas em criptomoeda de forma segura e com câmbio transparente, PARA ter liberdade de decidir como usar minhas recompensas. |

## US escolhida para o desenvolvimento

A US escolhida como foco do desenvolvimento foi a **US2 — Liberação automática
de 3 novos cursos ao concluir um curso com média ≥ 7,0 (Eduarda)**, que originou
os cenários de liberação automática, bloqueio por limite de plano e verificação
de elegibilidade explorados nos BDDs e TDDs abaixo.

## BDDs e TDDs por integrante

Os BDDs foram escritos de forma cruzada: cada integrante ficou **responsável
(Resp.)** por detalhar os critérios de aceitação (Given/When/Then) de uma US
diferente da que redigiu, e escreveu os TDDs correspondentes.

### Fernanda — BDD e TDDs da US1 (liberação por média > 7,0)

**BDD**
- **Given** o aluno está matriculado em um curso **And** concluiu todas as atividades
- **When** a média final é calculada como média > 7,0 **And** o desbloqueio é processado
- **Then** 3 novos cursos devem ser liberados no catálogo **And** o aluno deve ser notificado

**TDDs**
- `deveLiberar3CursosQuandoMediaAcimaDe7` — libera 3 cursos quando média = 8,5
- `naoDeveLiberarQuandoMediaIgualOuMenorQue7` — nenhum curso liberado com média = 6,9
- `deveNotificarAlunoAposLiberacao` — aluno é notificado após a liberação

### Carolina — BDD e TDDs da US2 (bloqueio por limite do plano)

**BDD**
- **Given** o aluno concluiu um curso elegível **And** já possui o número máximo de cursos do plano
- **When** o sistema tenta liberar 3 novos cursos **And** verifica o limite do plano
- **Then** a liberação deve ser bloqueada **And** o aluno deve ser notificado do limite atingido

**TDDs**
- `naoDeveLiberarQuandoLimiteDoPlanoAtingido` — bloqueia liberação no limite máximo
- `deveNotificarSobreLimiteAtingido` — notifica o aluno sobre o limite atingido
- `deveLiberarQuandoAbaixoDoLimiteDoPlano` — libera normalmente abaixo do limite

### Eduarda — BDD e TDDs da US3 (curso em andamento)

**BDD**
- **Given** o aluno está com um curso em andamento **And** ainda não o finalizou
- **When** o sistema verifica a elegibilidade para desbloqueio **And** o curso não está concluído
- **Then** nenhum cálculo de liberação deve ser realizado **And** nenhuma notificação deve ser enviada

**TDDs**
- `naoDeveLiberarSeCursoEmAndamento` — nenhum curso liberado se o curso está EM_ANDAMENTO
- `naoDeveNotificarSeCursoNaoConcluido` — nenhuma notificação enviada
- `deveRetornarElegibilidadeFalsaParaCursoEmAndamento` — elegibilidade retorna falsa

## Tecnologias

- **Java**
- **Maven** (build e gerenciamento de dependências — `mvnw` incluído)
- **JUnit** (testes unitários / TDD)
- **BDD** (Gherkin: Given / When / Then)
- Desenvolvido no **IntelliJ IDEA**

## Estrutura do repositório

```
Grupo_4_GameEducator/
├── .idea/                # configurações do IntelliJ
├── .mvn/                 # Maven Wrapper
├── src/
│   ├── main/             # código de produção (Aluno, Curso, AlunoService, Plano)
│   └── test/             # testes unitários (TDDs por US)
├── .gitattributes
├── .gitignore
├── HELP.md
├── mvnw                  # Maven Wrapper (Linux/macOS)
├── mvnw.cmd              # Maven Wrapper (Windows)
├── pom.xml
└── README.md
```

## Como executar os testes

Usando o Maven Wrapper que já acompanha o projeto (não exige Maven instalado):

```bash
# Linux / macOS
./mvnw test

# Windows
mvnw.cmd test
```

Ou, com Maven instalado globalmente:

```bash
mvn test
```

No **IntelliJ IDEA**: clique com o botão direito sobre a pasta `src/test` e
selecione **Run 'All Tests'**.