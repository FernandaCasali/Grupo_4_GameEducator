"""Servico de IA (B1 / Desafio 3).

Fronteira arquitetural: o Core Java chama este servico por HTTP/JSON.
O modelo aqui e uma regra simples (trilhas de aprendizagem) que representa o ponto onde
entraria o ML real; o que o desafio avalia e a FRONTEIRA, o contrato e a degradacao.
"""
from fastapi import FastAPI
from pydantic import BaseModel, Field

app = FastAPI(title="GameEducator IA Service", version="0.1.0")

# Trilhas: cada curso concluido aponta para os proximos sugeridos.
TRILHAS: dict[str, list[str]] = {
    "logica": ["Java Basico", "Estruturas de Dados"],
    "java basico": ["Spring Boot", "Orientacao a Objetos"],
    "estruturas de dados": ["Algoritmos", "Banco de Dados"],
    "spring boot": ["Testes Automatizados", "Arquitetura de Software"],
    "banco de dados": ["Modelagem de Dados", "SQL Avancado"],
}
CATALOGO_INICIAL = ["Logica", "Introducao a Computacao"]


class RecomendacaoRequest(BaseModel):
    aluno_id: int = Field(alias="alunoId")
    cursos_concluidos: list[str] = Field(default_factory=list, alias="cursosConcluidos")
    limite: int = Field(default=3, ge=1, le=10)

    model_config = {"populate_by_name": True}


class Recomendacao(BaseModel):
    curso: str
    motivo: str


class RecomendacaoResponse(BaseModel):
    recomendacoes: list[Recomendacao]
    modelo: str = "regras-trilha-v1"


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/recomendacoes", response_model=RecomendacaoResponse)
def recomendar(req: RecomendacaoRequest) -> RecomendacaoResponse:
    ja_tem = {c.strip().lower() for c in req.cursos_concluidos}
    sugestoes: list[Recomendacao] = []
    vistos: set[str] = set()

    for concluido in req.cursos_concluidos:
        base = concluido.strip().lower()
        for proximo in TRILHAS.get(base, []):
            chave = proximo.lower()
            if chave not in ja_tem and chave not in vistos:
                vistos.add(chave)
                sugestoes.append(Recomendacao(curso=proximo, motivo=f"Proximo passo apos {concluido}"))

    if not sugestoes:
        for inicial in CATALOGO_INICIAL:
            if inicial.lower() not in ja_tem:
                sugestoes.append(Recomendacao(curso=inicial, motivo="Curso inicial da trilha"))

    return RecomendacaoResponse(recomendacoes=sugestoes[: req.limite])
