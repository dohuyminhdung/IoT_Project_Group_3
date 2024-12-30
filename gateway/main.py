import struct
import sys
import time
import asyncio
import socket
from Adafruit_IO import MQTTClient
from ai import *
from key import *
from sever import *
# from uart import *

AIO_FEED_IDs = ["ai", "btn1", "btn2", "btn3", "cambien-chay", "cambien-gas", "cambien1", "cambien2", "cambien3"]
AIO_USERNAME = username
AIO_KEY = secret_key

def connected(client):
    print("Ket noi thanh cong...")
    for topic in AIO_FEED_IDs:
        client.subscribe(topic)
def subscribe(client, userdata, mid, granted_qos):
    print("Subscribe thanh cong ...")
def disconnected(client):
    print("Ngat ket noi ...")
    sys.exit(1)
def message(client, feed_id, payload):
    print("Nhan du lieu: " + payload + "feed_id:" + feed_id)
    # send_data(feed_id, payload)

client = MQTTClient(AIO_USERNAME, AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect()
client.loop_background()


async def face_detection_loop():
    HOST = '0.0.0.0'  # Lắng nghe trên tất cả các IP
    PORT = 8000  # Port cần khớp với Android
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((HOST, PORT))
    server_socket.listen(3)
    print("Server listening on port:", PORT)

    conn, addr = await asyncio.get_event_loop().run_in_executor(None, server_socket.accept)
    print(f"Connection established with {addr}")

    data = b""
    payload_size = struct.calcsize(">I")  # Cấu trúc kích thước dữ liệu

    try:
        while True:
            while len(data) < payload_size:
                packet = await asyncio.get_event_loop().run_in_executor(None, conn.recv, 4096)
                if not packet:
                    print("Connection closed by client.")
                    return
                data += packet

            if len(data) < payload_size:
                break  # Kết thúc nếu không nhận đủ dữ liệu

            packed_msg_size = data[:payload_size]
            data = data[payload_size:]
            msg_size = struct.unpack(">I", packed_msg_size)[0]

            # Nhận toàn bộ khung hình
            while len(data) < msg_size:
                data += await asyncio.get_event_loop().run_in_executor(None, conn.recv, 4096)

            frame_data = data[:msg_size]
            data = data[msg_size:]

            # Xử lý khung hình
            frame = cv2.imdecode(np.frombuffer(frame_data, np.uint8), cv2.IMREAD_COLOR)
            if frame is None:
                print("Error decoding frame")
                continue

            # Chạy nhận diện AI
            ai_result = image_detector(frame)
            print("AI Output: ", ai_result)

            if ai_result != "0":
                print(f"Published AI result: {ai_result}")
                if ai_result == "1":
                    client.publish("btn3", "1")
                    client.publish("ai", "Welcome Home")
                else:
                    client.publish("btn3", "0")
                    client.publish("ai", "Có người lạ tiếp cận")
            else:
                print("No valid AI result to publish.")
    except Exception as e:
        print(f"Error in face detection loop: {e}")
    finally:
        conn.close()
        server_socket.close()
        cv2.destroyAllWindows()




# Hàm xử lý dữ liệu từ WebSocket client
async def receive_audio_data(websocket):
    global audio_flag
    global audio_key
    print("WebSocket connection established")
    try:
        async for message in websocket:
            data = json.loads(message)  # Chuyển dữ liệu JSON thành dictionary
            max_key = max(data, key=data.get)  # Tìm key có giá trị lớn nhất
            max_value = data[max_key]  # Giá trị tương ứng
            print(f"{float(max_value) * 100:.0f}% {max_key}")  # In ra tỉ lệ % và key
            if max_key == "OK MY HOME":
                # client.publish("ai", "Hello hii")
                audio_flag = 1
                audio_key = max_key
                # print("Hello haha")
            elif audio_flag == 1 and max_key != "Background Noise" and max_key != "Others":
                audio_flag = 0
                audio_key = max_key
            if audio_key != "":
                if audio_key == "MỞ QUẠT":
                    client.publish("btn2", "1")
                elif audio_key == "MỞ ĐÈN":
                    client.publish("btn1", "1")
                elif audio_key == "TẮT QUẠT":
                    client.publish("btn2", "0")
                elif audio_key == "TẮT ĐÈN":
                    client.publish("btn1", "0")
                audio_key = ""
    except Exception as e: print("Error:", e)


# Chạy WebSocket server
async def websocket_server():
    async with websockets.serve(receive_audio_data, "localhost", 8000):
        print("WebSocket server is running on ws://localhost:8000")
        await asyncio.Future()  # Chạy vô thời hạn





async def main():
    # Chạy HTTP server trong luồng riêng
    threading.Thread(target=start_http_server, daemon=True).start()
    open_browser()
    # Chạy song song WebSocket server và vòng lặp nhận diện
    await asyncio.gather(
        websocket_server(),   # WebSocket server
        face_detection_loop(), # Nhận diện khuôn mặt
        # read_serial(client) # read serial
    )

if __name__ == "__main__":
    asyncio.run(main())




