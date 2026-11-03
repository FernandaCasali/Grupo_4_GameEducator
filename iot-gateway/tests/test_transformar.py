import json

import pytest

from app.transformar import EventoInvalido, extrair_do_topico, transformar

TOPICO = "labs/lab1/dispositivos/sensor-03/eventos"


def payload(**campos):
    base = {"id": "e-1", "t": "C", "a": 7, "ts": 1730000000}
    base.update(campos)
    return json.dumps(base).encode()


def test_transforma_formato_compacto_no_evento_normalizado():
    ev = transformar(TOPICO, payload())
    assert ev == {
        "eventoId": "e-1",
        "dispositivoId": "sensor-03",
        "laboratorio": "lab1",
        "tipo": "CONCLUSAO_ATIVIDADE",
        "alunoId": 7,
        "ocorridoEm": "2024-10-27T03:33:20Z",
    }


@pytest.mark.parametrize("letra,tipo", [("P", "PRESENCA"), ("I", "INICIO_ATIVIDADE"), ("C", "CONCLUSAO_ATIVIDADE")])
def test_mapeia_os_tres_tipos(letra, tipo):
    assert transformar(TOPICO, payload(t=letra))["tipo"] == tipo


def test_aluno_e_opcional():
    ev = transformar(TOPICO, json.dumps({"id": "e-2", "t": "P", "ts": 1730000000}).encode())
    assert ev["alunoId"] is None


@pytest.mark.parametrize("campos", [
    {"id": ""}, {"id": None}, {"t": "X"}, {"t": None}, {"ts": 0}, {"ts": "agora"}, {"ts": True}, {"a": -1}, {"a": "1"},
])
def test_rejeita_campos_invalidos(campos):
    with pytest.raises(EventoInvalido):
        transformar(TOPICO, payload(**campos))


@pytest.mark.parametrize("bruto", [b"nao e json", b"[1,2]", b"\xff\xfe"])
def test_rejeita_payload_que_nao_e_objeto_json(bruto):
    with pytest.raises(EventoInvalido):
        transformar(TOPICO, bruto)


@pytest.mark.parametrize("topico", ["labs/lab1/eventos", "x/lab1/dispositivos/s/eventos", "labs//dispositivos/s/eventos",
                                    "labs/lab1/dispositivos//eventos", "labs/lab1/dispositivos/s/outro"])
def test_rejeita_topico_fora_do_padrao(topico):
    with pytest.raises(EventoInvalido):
        extrair_do_topico(topico)
