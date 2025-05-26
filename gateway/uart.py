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
    return "COM8"

if getPort() != "None":
    ser = serial.Serial( port=getPort(), baudrate=9600)
    print(ser)

def processData(client, data):
    print(data)
    # print("\n")
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

async def readSerial(client):
    bytesToRead = ser.inWaiting()
    if bytesToRead > 0:
        global mess
        mess = mess + ser.read(bytesToRead).decode("UTF-8", errors="ignore")
        while ("#" in mess) and ("!" in mess):
            start = mess.find("!")
            end = mess.find("#")
            await asyncio.to_thread(processData, client, mess[start:end + 1])
            if end == len(mess):
                mess = ""
            else:
                mess = mess[end+1:]

# MCU communicate directly with broker in both CoreIoT and Adafruit so this is no need anymore
# def readSerial(client):
#     bytesToRead = ser.inWaiting()
#     if (bytesToRead > 0):
#         global mess
#         mess = mess + ser.read(bytesToRead).decode("UTF-8", errors="ignore")
#         while ("#" in mess) and ("!" in mess):
#             start = mess.find("!")
#             end = mess.find("#")
#             processData(client, mess[start:end + 1])
#             if (end == len(mess)):
#                 mess = ""
#             else:
#                 mess = mess[end+1:]


def send_data(feed_id, data):
    last_char = feed_id[-1]  # Ký tự cuối cùng trong feed_id
    string= "!B:" + last_char + ":" + data + "#"
    ser.write(str(string).encode  ("utf-8"))
    print(string)

# def send_data(string):
#     ser.write(str(string).encode  ("utf-8"))