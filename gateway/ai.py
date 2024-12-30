from keras.models import load_model  # TensorFlow is required for Keras to work
import cv2  # Install opencv-python
import numpy as np
# import time
# import struct

# Disable scientific notation for clarity
np.set_printoptions(suppress=True)
# Load the model
model = load_model("keras_Model.h5", compile=False)
# Load the labels
class_names = open("labels.txt", "r", encoding="utf-8").readlines()
def image_detector(image) -> str:
    image = cv2.resize(image, (224, 224), interpolation=cv2.INTER_AREA)  # Resize ảnh
    image = np.asarray(image, dtype=np.float32).reshape(1, 224, 224, 3)  # Định hình lại
    image = (image / 127.5) - 1  # Chuẩn hóa ảnh
    prediction = model.predict(image)
    index = np.argmax(prediction)
    class_name = class_names[index]
    if index == 1: return "1"
    elif index == 2: return "2"
    return "0"

