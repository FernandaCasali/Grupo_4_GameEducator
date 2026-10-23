from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_health():
    r = client.get("/health")
    assert r.status_code == 200
    assert r.json() == {"status": "ok"}


def test_recomenda_proximos_cursos_da_trilha():
    r = client.post("/recomendacoes", json={"alunoId": 1, "cursosConcluidos": ["Logica"]})
    assert r.status_code == 200
    cursos = [x["curso"] for x in r.json()["recomendacoes"]]
    assert cursos == ["Java Basico", "Estruturas de Dados"]


def test_nao_recomenda_curso_ja_concluido():
    r = client.post(
        "/recomendacoes",
        json={"alunoId": 1, "cursosConcluidos": ["Logica", "Java Basico"]},
    )
    cursos = [x["curso"] for x in r.json()["recomendacoes"]]
    assert "Java Basico" not in cursos
    assert "Spring Boot" in cursos


def test_aluno_novo_recebe_cursos_iniciais():
    r = client.post("/recomendacoes", json={"alunoId": 2, "cursosConcluidos": []})
    cursos = [x["curso"] for x in r.json()["recomendacoes"]]
    assert cursos == ["Logica", "Introducao a Computacao"]


def test_respeita_limite():
    r = client.post(
        "/recomendacoes",
        json={"alunoId": 1, "cursosConcluidos": ["Logica", "Java Basico", "Estruturas de Dados"], "limite": 2},
    )
    assert len(r.json()["recomendacoes"]) == 2


def test_rejeita_limite_invalido():
    r = client.post("/recomendacoes", json={"alunoId": 1, "cursosConcluidos": [], "limite": 0})
    assert r.status_code == 422
