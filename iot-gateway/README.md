# iot-gateway (B1 · Desafio 5)

Gateway MQTT → Core. Consome os eventos dos dispositivos do laboratório (broker Mosquitto), traduz o formato
compacto do dispositivo para o evento normalizado e o entrega ao Core por HTTP/JSON (`POST /api/iot/eventos`).

```
dispositivo ──MQTT QoS1──▶ Mosquitto ──MQTT QoS1──▶ iot-gateway ──HTTP/JSON──▶ Core (módulo iot)
```

Tópico: `labs/<laboratorio>/dispositivos/<dispositivo>/eventos`
Payload do dispositivo (compacto): `{"id":"<uuid>","t":"P|I|C","a":<alunoId>,"ts":<epoch s>}`
(`P` presença · `I` início de atividade · `C` conclusão)

## Rodar (Windows/PowerShell, a partir da pasta `iot-gateway`)

```powershell
pip install -r requirements-dev.txt
python -m pytest tests -q                       # testes do gateway (29)

docker compose up -d mosquitto                  # na raiz do repositório
python -m app.gateway                           # gateway (deixe rodando)
python simulador\simulador.py --cenario demo --aluno 1
```

Variáveis do gateway: `MQTT_HOST` (localhost), `MQTT_PORT` (1883), `CORE_URL` (http://localhost:8080).

## Garantias (e limites)
- **At-least-once ponta a ponta:** sessão persistente + QoS 1 + ack manual (o broker só recebe o PUBACK depois que o Core aceitou).
- **Duplicatas são esperadas:** o Core é idempotente por `eventoId` (201 novo · 200 duplicado).
- **Core fora do ar:** o evento fica pendente (sem ack) e é reenviado a cada 5 s.
- **Limites do laboratório:** broker sem TLS/autenticação; fila de pendentes em memória; o buffer offline do dispositivo é responsabilidade do firmware.
