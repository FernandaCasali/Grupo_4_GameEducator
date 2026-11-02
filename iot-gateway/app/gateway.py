"""Gateway MQTT -> Core (B1 / Desafio 5).

Consome os eventos dos dispositivos, transforma e entrega ao Core por HTTP/JSON.
Garantias (at-least-once ponta a ponta):
  * sessao persistente + QoS 1: se o gateway ficar fora, o broker guarda as mensagens;
  * ack manual: o broker so recebe o PUBACK depois que o Core aceitou o evento;
  * o Core e idempotente por eventoId, entao reentregas/duplicatas sao inofensivas.
"""
import json
import logging
import os
import threading
import time
import urllib.error
import urllib.request

from app.transformar import EventoInvalido, transformar

log = logging.getLogger("iot-gateway")

ENVIADO = "ENVIADO"
DUPLICADO = "DUPLICADO"
PENDENTE = "PENDENTE"
INVALIDO = "INVALIDO"


class CoreIndisponivel(Exception):
    """Falha transitoria ao falar com o Core (vale tentar de novo)."""


class CoreRejeitou(Exception):
    """O Core respondeu 4xx: reenviar o mesmo evento nao vai adiantar."""


def enviar_http(core_url: str, evento: dict, timeout: float = 3.0) -> int:
    corpo = json.dumps(evento).encode("utf-8")
    req = urllib.request.Request(
        f"{core_url}/api/iot/eventos", data=corpo, method="POST",
        headers={"Content-Type": "application/json"})
    try:
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            return resp.status
    except urllib.error.HTTPError as erro:
        if 400 <= erro.code < 500:
            raise CoreRejeitou(f"Core respondeu {erro.code}") from erro
        raise CoreIndisponivel(f"Core respondeu {erro.code}") from erro
    except (urllib.error.URLError, TimeoutError, ConnectionError, OSError) as erro:
        raise CoreIndisponivel(str(erro)) from erro


class Gateway:
    def __init__(self, enviar, ack, tentativas=3, espera=0.5, dormir=time.sleep):
        self._enviar = enviar          # callable(evento) -> status http (200/201) ou levanta excecao
        self._ack = ack                # callable(mid, qos)
        self._tentativas = tentativas
        self._espera = espera
        self._dormir = dormir
        self._pendentes = []           # [(evento, mid, qos)]
        self._lock = threading.Lock()

    @property
    def total_pendentes(self) -> int:
        with self._lock:
            return len(self._pendentes)

    def _entregar(self, evento) -> str:
        """Tenta entregar; devolve ENVIADO/DUPLICADO; levanta CoreIndisponivel ou CoreRejeitou."""
        ultima = None
        for tentativa in range(1, self._tentativas + 1):
            try:
                status = self._enviar(evento)
                return ENVIADO if status == 201 else DUPLICADO
            except CoreIndisponivel as erro:
                ultima = erro
                log.warning("Core indisponivel (tentativa %d/%d): %s", tentativa, self._tentativas, erro)
                if tentativa < self._tentativas:
                    self._dormir(self._espera * (2 ** (tentativa - 1)))
        raise ultima

    def _confirmar(self, mid, qos):
        if qos and qos > 0:
            self._ack(mid, qos)

    def tratar(self, topic, payload, mid=0, qos=1) -> str:
        try:
            evento = transformar(topic, payload)
        except EventoInvalido as erro:
            log.error("Evento descartado (invalido, nao adianta reenviar): %s", erro)
            self._confirmar(mid, qos)
            return INVALIDO

        try:
            resultado = self._entregar(evento)
        except CoreRejeitou as erro:
            log.error("Core rejeitou o evento %s: %s", evento["eventoId"], erro)
            self._confirmar(mid, qos)
            return INVALIDO
        except CoreIndisponivel:
            log.error("Evento %s ficou PENDENTE (sem ack ao broker; sera reenviado)", evento["eventoId"])
            with self._lock:
                self._pendentes.append((evento, mid, qos))
            return PENDENTE

        log.info("Evento %s -> Core: %s (%s)", evento["eventoId"], resultado, evento["tipo"])
        self._confirmar(mid, qos)
        return resultado

    def reenviar_pendentes(self) -> int:
        with self._lock:
            fila, self._pendentes = self._pendentes, []
        entregues = 0
        for evento, mid, qos in fila:
            try:
                self._entregar(evento)
            except CoreRejeitou:
                self._confirmar(mid, qos)
            except CoreIndisponivel:
                with self._lock:
                    self._pendentes.append((evento, mid, qos))
            else:
                entregues += 1
                self._confirmar(mid, qos)
        if entregues:
            log.info("Reenvio: %d evento(s) pendente(s) entregue(s) ao Core", entregues)
        return entregues


def main():  # pragma: no cover - exige broker
    import paho.mqtt.client as mqtt

    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(name)s: %(message)s")
    host = os.getenv("MQTT_HOST", "localhost")
    porta = int(os.getenv("MQTT_PORT", "1883"))
    core_url = os.getenv("CORE_URL", "http://localhost:8080")
    client_id = os.getenv("MQTT_CLIENT_ID", "iot-gateway")
    topico = os.getenv("MQTT_TOPIC", "labs/+/dispositivos/+/eventos")

    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2, client_id=client_id,
                         clean_session=False, protocol=mqtt.MQTTv311, manual_ack=True)
    gateway = Gateway(enviar=lambda ev: enviar_http(core_url, ev), ack=client.ack)

    def ao_conectar(cli, _userdata, _flags, reason_code, _props):
        log.info("Conectado ao broker %s:%s (%s); assinando %s com QoS 1", host, porta, reason_code, topico)
        cli.subscribe(topico, qos=1)

    def ao_mensagem(_cli, _userdata, msg):
        gateway.tratar(msg.topic, msg.payload, msg.mid, msg.qos)

    client.on_connect = ao_conectar
    client.on_message = ao_mensagem

    def laco_reenvio():
        while True:
            time.sleep(5)
            gateway.reenviar_pendentes()

    threading.Thread(target=laco_reenvio, daemon=True).start()

    while True:
        try:
            client.connect(host, porta, keepalive=60)
            break
        except OSError as erro:
            log.warning("Broker indisponivel (%s); tentando de novo em 3 s", erro)
            time.sleep(3)
    client.loop_forever(retry_first_connection=True)


if __name__ == "__main__":  # pragma: no cover
    main()
