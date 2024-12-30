import sys
import asyncio
import websockets
import threading
import webbrowser
import json  # Import thư viện json để xử lý dữ liệu JSON
from http.server import SimpleHTTPRequestHandler
from socketserver import TCPServer
from Adafruit_IO import MQTTClient




# def message(client, feed_id, payload):
#     print("Nhan du lieu: " + payload + "feed_id:" + feed_id)

audio_flag = 0
max_flag = 0
audio_key = ""


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


