import json

from app.gateway import (DUPLICADO, ENVIADO, INVALIDO, PENDENTE, CoreIndisponivel, CoreRejeitou, Gateway)

TOPICO = "labs/lab1/dispositivos/sensor-03/eventos"
MSG = json.dumps({"id": "e-1", "t": "C", "a": 7, "ts": 1730000000}).encode()


class Core:
    """Core falso: registra o que recebeu e devolve 201 na 1a vez e 200 no mesmo eventoId."""

    def __init__(self):
        self.vistos = set()
        self.chamadas = 0
        self.fora_do_ar = False
        self.rejeitar = False

    def __call__(self, evento):
        self.chamadas += 1
        if self.fora_do_ar:
            raise CoreIndisponivel("conexao recusada")
        if self.rejeitar:
            raise CoreRejeitou("400")
        if evento["eventoId"] in self.vistos:
            return 200
        self.vistos.add(evento["eventoId"])
        return 201


def montar(core, **kw):
    acks = []
    gw = Gateway(enviar=core, ack=lambda mid, qos: acks.append(mid), dormir=lambda s: None, **kw)
    return gw, acks


def test_evento_valido_e_entregue_e_confirmado_ao_broker():
    core = Core()
    gw, acks = montar(core)
    assert gw.tratar(TOPICO, MSG, mid=10) == ENVIADO
    assert acks == [10]


def test_duplicata_e_reconhecida_sem_efeito_duplo():
    core = Core()
    gw, acks = montar(core)
    assert gw.tratar(TOPICO, MSG, mid=1) == ENVIADO
    assert gw.tratar(TOPICO, MSG, mid=2) == DUPLICADO
    assert len(core.vistos) == 1
    assert acks == [1, 2]


def test_payload_invalido_e_descartado_com_ack_para_nao_travar_o_broker():
    core = Core()
    gw, acks = montar(core)
    assert gw.tratar(TOPICO, b"lixo", mid=3) == INVALIDO
    assert core.chamadas == 0
    assert acks == [3]


def test_core_rejeitando_com_4xx_nao_e_reenviado():
    core = Core()
    core.rejeitar = True
    gw, acks = montar(core)
    assert gw.tratar(TOPICO, MSG, mid=4) == INVALIDO
    assert core.chamadas == 1
    assert acks == [4]


def test_core_fora_do_ar_deixa_pendente_sem_ack_depois_das_tentativas():
    core = Core()
    core.fora_do_ar = True
    gw, acks = montar(core, tentativas=3)
    assert gw.tratar(TOPICO, MSG, mid=5) == PENDENTE
    assert core.chamadas == 3
    assert acks == []            # sem PUBACK: o broker continua responsavel pela mensagem
    assert gw.total_pendentes == 1


def test_pendente_e_entregue_e_confirmado_quando_o_core_volta():
    core = Core()
    core.fora_do_ar = True
    gw, acks = montar(core)
    gw.tratar(TOPICO, MSG, mid=6)
    assert gw.reenviar_pendentes() == 0 and acks == []

    core.fora_do_ar = False
    assert gw.reenviar_pendentes() == 1
    assert acks == [6]
    assert gw.total_pendentes == 0
    assert "e-1" in core.vistos


def test_qos0_nao_gera_ack():
    core = Core()
    gw, acks = montar(core)
    gw.tratar(TOPICO, MSG, mid=0, qos=0)
    assert acks == []
