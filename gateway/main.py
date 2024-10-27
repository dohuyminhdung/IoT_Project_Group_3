import sys
from Adafruit_IO import MQTTClient
import time
import random
# from simple_ai import *
from uart import *
from key import *

AIO_FEED_ID = ["btn1", "btn2"]
AIO_USERNAME = username 
AIO_KEY = secret_key #DO NOT COMMIT THIS KEY TO GITHUB!!!


def connected(client):
    print("Ket noi thanh cong ...")
    for topic in AIO_FEED_ID:
        client.subscribe(topic)


def subscribe(client, userdata, mid, granted_qos):
    print("Subscribe thanh cong ...")


def disconnected(client):
    print("Ngat ket noi ...")
    sys.exit(1)


def message(client, feed_id, payload):
    print("Nhan du lieu: " + payload + ", feed id: ", feed_id)
    if feed_id == AIO_FEED_ID[0]:
        if payload == "0":
            writeData("btn 1 off\n")
        else:
            writeData("btn 1 on\n")
    if feed_id == AIO_FEED_ID[1]:
        if payload == "0":
            writeData("btn 2 off\n")
        else:
            writeData("btn 2 on\n")

client = MQTTClient(AIO_USERNAME, AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect()
client.loop_background()

cnt = 10
while True:
    # cnt -= 1
    # if cnt <= 0:
    #     cnt = 10
    #     #TODO
    #     print("Test data...")
    #     temp = random.randint(24, 33)
    #     client.publish("cambien1", temp)
    #     ln = random.randint(150, 450)
    #     client.publish("cambien2", ln)
    #     hum = random.randint(45, 90)
    #     client.publish("cambien3", hum)
    readSerial(client)
    time.sleep(1)
    pass