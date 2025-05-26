import sys
import asyncio
import websockets
import threading
import webbrowser
import json
from http.server import SimpleHTTPRequestHandler
from socketserver import TCPServer
from Adafruit_IO import MQTTClient
# HTTP server để phục vụ HTML
def start_http_server():
    PORT = 8080
    Handler = SimpleHTTPRequestHandler
    with TCPServer(("localhost", PORT), Handler) as httpd:
        print(f"HTTP server serving at http://localhost:{PORT}")
        httpd.serve_forever()

# Tự động mở trình duyệt với file HTML
def open_browser():
    url = "http://localhost:8080/index.html"
    webbrowser.open(url)




################### Test pushing data to CoreioT #######################
import paho.mqtt.client as mqtt
import time
import random
BROKER = "app.coreiot.io"
PORT = 1883
ACCESS_TOKEN = "ss3m6wr9ovkftcquvlpd"

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected successfully")
        client.subscribe("v1/devices/me/rpc/request/+")
    else:
        print("Connection failed")

def on_message(client, userdata, msg):
    print("Received:", msg.payload.decode())

client = mqtt.Client()
client.username_pw_set(ACCESS_TOKEN)  # chỉ cần set username là access token
client.on_connect = on_connect
client.on_message = on_message

client.connect(BROKER, PORT, 60)
client.loop_start()

def main():
    temp = 30
    humi = 50
    light = 100
    while True:
        telemetry = {"temperature": temp, "humidity": humi, "light": light}
        temp = random.uniform(28, 30)
        humi = random.uniform(40, 70)
        light = random.uniform(300, 400)
        # client.publish("v1/devices/me/telemetry", json.dumps(telemetry), qos=1)
        client.publish("v1/devices/me/attributes", json.dumps(telemetry), qos=1)
        time.sleep(8)

if __name__ == "__main__":
    main()