"""Traducao do formato compacto do dispositivo para o evento normalizado do Core.

O dispositivo (recursos limitados) envia JSON curto; o Core nunca ve esse formato.
Topico: labs/<laboratorio>/dispositivos/<dispositivo>/eventos
Payload: {"id": "<uuid>", "t": "P|I|C", "a": <alunoId opcional>, "ts": <epoch segundos>}
"""
import json
from datetime import datetime, timezone

TIPOS = {"P": "PRESENCA", "I": "INICIO_ATIVIDADE", "C": "CONCLUSAO_ATIVIDADE"}


class EventoInvalido(ValueError):
    """Mensagem que nunca sera valida (nao adianta reenviar)."""


def extrair_do_topico(topic: str) -> tuple[str, str]:
    partes = topic.split("/")
    if len(partes) != 5 or partes[0] != "labs" or partes[2] != "dispositivos" or partes[4] != "eventos":
        raise EventoInvalido(f"topico inesperado: {topic}")
    laboratorio, dispositivo = partes[1], partes[3]
    if not laboratorio or not dispositivo:
        raise EventoInvalido(f"topico sem laboratorio/dispositivo: {topic}")
    return laboratorio, dispositivo


def transformar(topic: str, payload: bytes) -> dict:
    laboratorio, dispositivo = extrair_do_topico(topic)
    try:
        bruto = json.loads(payload)
    except (ValueError, UnicodeDecodeError) as erro:
        raise EventoInvalido(f"payload nao e JSON: {erro}") from erro
    if not isinstance(bruto, dict):
        raise EventoInvalido("payload deve ser um objeto JSON")

    evento_id = bruto.get("id")
    if not isinstance(evento_id, str) or not evento_id.strip():
        raise EventoInvalido("campo 'id' obrigatorio")

    tipo = TIPOS.get(bruto.get("t"))
    if tipo is None:
        raise EventoInvalido(f"tipo invalido: {bruto.get('t')!r}")

    ts = bruto.get("ts")
    if isinstance(ts, bool) or not isinstance(ts, int) or ts <= 0:
        raise EventoInvalido("campo 'ts' (epoch em segundos) obrigatorio")

    aluno = bruto.get("a")
    if aluno is not None and (isinstance(aluno, bool) or not isinstance(aluno, int) or aluno <= 0):
        raise EventoInvalido("campo 'a' (alunoId) deve ser inteiro positivo")

    ocorrido_em = datetime.fromtimestamp(ts, tz=timezone.utc).isoformat().replace("+00:00", "Z")
    return {
        "eventoId": evento_id.strip(),
        "dispositivoId": dispositivo,
        "laboratorio": laboratorio,
        "tipo": tipo,
        "alunoId": aluno,
        "ocorridoEm": ocorrido_em,
    }
