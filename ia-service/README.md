# ia-service (B1 · Desafio 3)

Servico Python/FastAPI de recomendacao. O Core Spring Boot o consome por HTTP/JSON.

```bash
pip install -r requirements.txt
python -m pytest -q                       # testes do servico
uvicorn app.main:app --port 8000          # subir o servico
curl -X POST localhost:8000/recomendacoes -H 'Content-Type: application/json' \
     -d '{"alunoId":1,"cursosConcluidos":["Logica"]}'
```
