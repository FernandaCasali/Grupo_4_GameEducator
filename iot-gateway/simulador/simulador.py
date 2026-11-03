"""Simulador de dispositivo de laboratorio (B1 / Desafio 5).

Publica eventos compactos no broker MQTT. Exemplos:
  python simulador/simulador.py --cenario demo
  python simulador/simulador.py --tipo P --aluno 1 --laboratorio lab1 --dispositivo sensor-03
  python simulador/simulador.py --tipo C --aluno 1 --evento-id fixo-1 --repetir 2   # mesmo id duas vezes
"""
import argparse
import json
import time
import uuid

import paho.mqtt.client as mqtt


def publicar(cliente, laboratorio, dispositivo, tipo, aluno=None, evento_id=None):
    evento_id = evento_id or str(uuid.uuid4())
    payload = {"id": evento_id, "t": tipo, "ts": int(time.time())}
    if aluno is not None:
        payload["a"] = aluno
    topico = f"labs/{laboratorio}/dispositivos/{dispositivo}/eventos"
    info = cliente.publish(topico, json.dumps(payload, separators=(",", ":")), qos=1)
    info.wait_for_publish(timeout=5)
    print(f"publicado em {topico}: {payload}")
    return evento_id


def main():
    p = argparse.ArgumentParser(description="Simulador de dispositivo IoT do laboratorio")
    p.add_argument("--host", default="localhost")
    p.add_argument("--porta", type=int, default=1883)
    p.add_argument("--laboratorio", default="lab1")
    p.add_argument("--dispositivo", default="sensor-03")
    p.add_argument("--tipo", choices=["P", "I", "C"], help="P=presenca, I=inicio, C=conclusao")
    p.add_argument("--aluno", type=int)
    p.add_argument("--evento-id")
    p.add_argument("--repetir", type=int, default=1, help="publica o mesmo evento N vezes (simula duplicidade)")
    p.add_argument("--cenario", choices=["demo"], help="demo: presenca, inicio, conclusao e uma conclusao duplicada")
    args = p.parse_args()

    cliente = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2, client_id=f"sim-{uuid.uuid4().hex[:8]}")
    cliente.connect(args.host, args.porta, keepalive=30)
    cliente.loop_start()
    try:
        if args.cenario == "demo":
            aluno = args.aluno or 1
            publicar(cliente, args.laboratorio, args.dispositivo, "P", aluno)
            publicar(cliente, args.laboratorio, args.dispositivo, "I", aluno)
            fim = publicar(cliente, args.laboratorio, args.dispositivo, "C", aluno)
            print("--- reenvio do MESMO evento de conclusao (simula duplicidade) ---")
            publicar(cliente, args.laboratorio, args.dispositivo, "C", aluno, evento_id=fim)
        elif args.tipo:
            for _ in range(args.repetir):
                args.evento_id = publicar(cliente, args.laboratorio, args.dispositivo, args.tipo, args.aluno, args.evento_id)
        else:
            p.error("informe --tipo ou --cenario demo")
    finally:
        cliente.loop_stop()
        cliente.disconnect()


if __name__ == "__main__":
    main()
