import serial.tools.list_ports

# def  message(client , feed_id , payload):
#     print("Nhan du lieu: " + payload)
#     ser.write((str(payload) + "#").encode())

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

if getPort()!= "None":
    ser = serial.Serial( port=getPort(), baudrate=115200)
    print(ser)

def processData(client, data):
    data = data.replace("!", "")
    data = data.replace("#", "")
    splitData = data.split(":")
    print(splitData)
    if splitData[1] == "T": #temp
        client.publish("cambien1", splitData[2])
    elif splitData[1] == "L": #lux
        client.publish("cambien2", splitData[2])
    elif splitData[1] == "H": #humid
        client.publish("cambien3", splitData[2])

mess = ""
def readSerial(client):
    bytesToRead = ser.inWaiting()
    if (bytesToRead > 0):
        global mess
        mess = mess + ser.read(bytesToRead).decode("UTF-8")
        while ("#" in mess) and ("!" in mess):
            start = mess.find("!")
            end = mess.find("#")
            processData(client, mess[start:end + 1])
            if (end == len(mess)):
                mess = ""
            else:
                mess = mess[end+1:]
def writeData(data):
    ser.write(str(data).encode())