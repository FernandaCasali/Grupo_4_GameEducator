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
(Resp.)** por detalhar os critérios de aceitação (Given/When/Then) em três (3) TDDs


### Carolina — BDD e TDDs da US2 (bloqueio por limite do plano)

**BDD**
- **Given** o aluno concluiu um curso elegível **And** já possui o número máximo de cursos do plano
- **When** o sistema tenta liberar 3 novos cursos **And** verifica o limite do plano
- **Then** a liberação deve ser bloqueada **And** o aluno deve ser notificado do limite atingido

**TDDs**

| TDD | SCENARIO (Green) | EXECUTION (Blue) | RESULTS / ASSERTS (Red) |
|---|---|---|---|
| `naoDeveLiberarQuandoLimiteDoPlanoAtingido` | `var aluno = new Aluno("Eduarda");` `aluno.setPlano(new Plano("Basico", 10));` `aluno.setTotalCursos(10);` `var curso = new Curso("Redes");` `curso.setStatus(StatusCurso.CONCLUIDO);` `var service = new AlunoService();` | `service.processarDesbloqueio(aluno, curso, 8.0);` | `assertEquals(0, aluno.getCursosDesbloqueados().size());` |
| `deveNotificarSobreLimiteAtingido` | `var aluno = new Aluno("Eduarda");` `aluno.setPlano(new Plano("Basico", 10));` `aluno.setTotalCursos(10);` `var curso = new Curso("Redes");` `curso.setStatus(StatusCurso.CONCLUIDO);` `var service = new AlunoService();` | `service.processarDesbloqueio(aluno, curso, 8.0);` | `assertTrue(aluno.getNotificacoes().contains("LIMITE_ATINGIDO"));` |
| `deveLiberarQuandoAbaixoDoLimiteDoPlano` | `var aluno = new Aluno("Eduarda");` `aluno.setPlano(new Plano("Basico", 10));` `aluno.setTotalCursos(5);` `var curso = new Curso("Redes");` `curso.setStatus(StatusCurso.CONCLUIDO);` `var service = new AlunoService();` | `service.processarDesbloqueio(aluno, curso, 8.0);` | `assertEquals(3, aluno.getCursosDesbloqueados().size());` |

### Eduarda — BDD e TDDs da US3 (curso em andamento)

**BDD**
- **Given** o aluno está com um curso em andamento **And** ainda não o finalizou
- **When** o sistema verifica a elegibilidade para desbloqueio **And** o curso não está concluído
- **Then** nenhum cálculo de liberação deve ser realizado **And** nenhuma notificação deve ser enviada

**TDDs**

| TDD | SCENARIO (Green) | EXECUTION (Blue) | RESULTS / ASSERTS (Red) |
|---|---|---|---|
| `naoDeveLiberarSeCursoEmAndamento` | `var aluno = new Aluno("Fernanda");` `var curso = new Curso("Inteligência Artificial");` `curso.setStatus(StatusCurso.EM_ANDAMENTO);` `var service = new AlunoService();` | `service.verificarElegibilidade(aluno, curso);` | `assertEquals(0, aluno.getCursosDesbloqueados().size());` |
| `naoDeveNotificarSeCursoNaoConcluido` | `var aluno = new Aluno("Fernanda");` `var curso = new Curso("Inteligência Artificial");` `curso.setStatus(StatusCurso.EM_ANDAMENTO);` `var service = new AlunoService();` | `service.verificarElegibilidade(aluno, curso);` | `assertFalse(aluno.foiNotificado());` |
| `deveRetornarElegibilidadeFalsaParaCursoEmAndamento` | `var aluno = new Aluno("Fernanda");` `var curso = new Curso("Inteligência Artificial");` `curso.setStatus(StatusCurso.EM_ANDAMENTO);` `var service = new AlunoService();` | `boolean elegivel = service.verificarElegibilidade(aluno, curso);` | `assertFalse(elegivel);` |


### Fernanda — BDD e TDDs da US1 (liberação por média > 7,0)

**BDD**
- **Given** o aluno está matriculado em um curso **And** concluiu todas as atividades
- **When** a média final é calculada como média > 7,0 **And** o desbloqueio é processado
- **Then** 3 novos cursos devem ser liberados no catálogo **And** o aluno deve ser notificado

**TDDs**

| TDD | SCENARIO (Green) | EXECUTION (Blue) | RESULTS / ASSERTS (Red) |
|---|---|---|---|
| `deveLiberar3CursosQuandoMediaAcimaDe7` | `var aluno = new Aluno("Carolina");` `var curso = new Curso("Lógica de Programação");` `curso.setStatus(StatusCurso.CONCLUIDO);` `var service = new AlunoService();` | `service.processarDesbloqueio(aluno, curso, 8.5);` | `assertEquals(3, aluno.getCursosDesbloqueados().size());` |
| `naoDeveLiberarQuandoMediaIgualOuMenorQue7` | `var aluno = new Aluno("Carolina");` `var curso = new Curso("Lógica de Programação");` `curso.setStatus(StatusCurso.CONCLUIDO);` `var service = new AlunoService();` | `service.processarDesbloqueio(aluno, curso, 6.9);` | `assertEquals(0, aluno.getCursosDesbloqueados().size());` |
| `deveNotificarAlunoAposLiberacao` | `var aluno = new Aluno("Carolina");` `var curso = new Curso("Lógica de Programação");` `curso.setStatus(StatusCurso.CONCLUIDO);` `var service = new AlunoService();` | `service.processarDesbloqueio(aluno, curso, 9.0);` | `assertTrue(aluno.foiNotificado());` |


## Tecnologias

- **Java**
- **Maven** (build e gerenciamento de dependências — `mvnw` incluído)
- **JUnit** (testes unitários / TDD)
- **BDD** (Gherkin: Given / When / Then)
- Desenvolvido no **IntelliJ IDEA**
- **Spring Boot 4** (Web, Data JPA)
- **H2** (perfil local) e **PostgreSQL** (perfil Docker)
- **pgAdmin** (administração do Postgres via container)
- **springdoc-openapi** (Swagger UI)
- **Vue 3 + Vite** (frontend)
- **Docker + Docker Compose**

## Estrutura do repositório


```
Grupo_4_GameEducator/
├── backend/
│   ├── .mvn/              # Maven Wrapper
│   ├── src/
│   │   ├── main/          # código de produção (Aluno, Curso, AlunoService, Plano)
│   │   └── test/          # testes unitários (TDDs por US)
│   ├── mvnw
│   ├── mvnw.cmd
│   └── pom.xml
├── frontend/               # aplicação Vue.js (consome a API)
│   ├── src/
│   │   ├── components/      
├── docs/evidencias/        # prints de RED/GREEN/BLUE, Postgres, H2
├── .gitattributes
├── .gitignore
├── docker-compose.yml
└── README.md
```

## Como executar a aplicação

### Backend local (H2, sem Docker)

    cd backend
    mvnw spring-boot:run

- API: http://localhost:8080/api/alunos
- Swagger: http://localhost:8080/swagger-ui/index.html
- Console H2: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:gamificacaodb`)

### Backend + PostgreSQL + pgAdmin via Docker

    docker compose up --build

- API: http://localhost:8080/api/alunos
- Swagger: http://localhost:8080/swagger-ui/index.html
- pgAdmin: http://localhost:5050 (login `admin@admin.com` / `admin`; servidor com host `db`, porta `5432`, usuário/senha `postgres`)

### Frontend (Vue)

    cd frontend
    npm install
    npm run dev

Acesse http://localhost:5173 (o backend precisa estar rodando em paralelo).

## Como executar os testes

Dentro da pasta backend/ (onde fica o Maven Wrapper):

```bash
# Linux / macOS
./mvnw test

# Windows
mvnw.cmd test
```

Ou, com Maven instalado globalmente:

```bash
cd backend
mvn test

cd frontend
npm install
npm run dev 
```

No **IntelliJ IDEA**: clique com o botão direito sobre a pasta 'backend/src/test' e
selecione **Run 'All Tests'**.

## Evidências

Prints em `docs/evidencias/`:
- `red.png` — testes falhando antes da implementação do AlunoService
- `green.png` — testes passando após a implementação
- `blue-cobertura.png` — cobertura de 100% nos métodos do cenário trabalhado em grupo (JaCoCo)
- `postgres.png` — dados do aluno criado pela API, vistos no pgAdmin (PostgreSQL via Docker)
- `h2.png` — dados do aluno criado pela API, vistos no console do H2 (perfil local)
