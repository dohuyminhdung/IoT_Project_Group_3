import serial.tools.list_ports
import asyncio
def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"
    for i in range(0, N):
        port = ports[i]
        strPort = str(port)
        if "USB Serial Device" in strPort:
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
    return "COM9"

if getPort() != "None":
    ser = serial.Serial( port=getPort(), baudrate=9600)
    print(ser)

def processData(client, data):
    data = data.replace("!", "")
    data = data.replace("#", "")
    splitData = data.split(":")
    print(splitData)
    if splitData[1] == "T":
        client.publish("cambien1", splitData[2])
    if splitData[1] == "H":
        client.publish("cambien3", splitData[2])
    if splitData[1] == "L":
        client.publish("cambien2", splitData[2])
    if splitData[1] == "F":
        client.publish("cambien-chay", splitData[2])
    if splitData[1] == "G":
        client.publish("cambien-gas", splitData[2])
    if splitData[1] == "Q": #quat
        client.publish("btn2", splitData[2])
mess = ""

async def read_serial(client):
    global mess
    while True:
        try:
            # Lấy số byte sẵn sàng đọc
            bytes_to_read = await asyncio.to_thread(lambda: ser.in_waiting)
            if bytes_to_read > 0:
                # Đọc dữ liệu từ serial trong một luồng riêng
                data_chunk = await asyncio.to_thread(ser.read, bytes_to_read)
                # Giải mã dữ liệu nhận được
                mess += data_chunk.decode("UTF-8", errors="ignore")
                print(f"Message buffer: {mess}")

                # Xử lý chuỗi chứa tin nhắn hoàn chỉnh
                while "!" in mess and "#" in mess:
                    start = mess.find("!")
                    end = mess.find("#")
                    complete_message = mess[start:end + 1]
                    print(f"Processing message: {complete_message}")
                    await asyncio.to_thread(processData, client, complete_message)
                    mess = "" if end == len(mess) else mess[end + 1:]

            await asyncio.sleep(0.1)  # Tránh đọc liên tục để giảm tải CPU
        except Exception as e:
            print(f"Error in read_serial: {e}")




def send_data(feed_id, data):
    last_char = feed_id[-1]  # Ký tự cuối cùng trong feed_id
    string= "!B:" + last_char + ":" + data + "#"
    ser.write(str(string).encode("utf-8"))
    print(string)

# def send_data(string):
#     ser.write(str(string).encode  ("utf-8"))