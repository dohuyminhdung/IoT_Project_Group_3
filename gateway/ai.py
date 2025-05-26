from keras.models import load_model  # TensorFlow is required for Keras to work
import cv2  # Install opencv-python
import numpy as np
import asyncio
# import time
# import struct

# Disable scientific notation for clarity
np.set_printoptions(suppress=True)
# Load the model
model = load_model("keras_Model.h5", compile=False)
# Load the labels
class_names = open("labels.txt", "r", encoding="utf-8").readlines()
camera = cv2.VideoCapture(0)

def image_detector(image) -> str:
    image = cv2.resize(image, (224, 224), interpolation=cv2.INTER_AREA)  # Resize ảnh
    image = np.asarray(image, dtype=np.float32).reshape(1, 224, 224, 3)  # Định hình lại
    image = (image / 127.5) - 1  # Chuẩn hóa ảnh
    prediction = model.predict(image)
    index = np.argmax(prediction)

    # Print prediction and confidence score
    class_name = class_names[index]
    confidence_score = prediction[0][index]
    print("Class:", class_name[2:], end="")
    print("Confidence Score:", str(np.round(confidence_score * 100))[:-2], "%")

    if index in [1, 2, 4]: return "MinhDungDepZai"
    elif index == 3: return "KhongPhaiMD"
    return "KhongCoNguoi"


