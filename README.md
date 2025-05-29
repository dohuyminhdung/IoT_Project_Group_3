# IoT Smart Home System 🤖
A simple implementation of a **Smart Home Mobile Application**.
---
## ⚙️Technology
- ESP32, CoreIoT, AdafruitIO, Android Studio, PlatformIO, Teachable Machine
---
## ✨ Features
- 🌱 Real-time environmental monitoring 
- 🌐 Remote control from anywhere
- ⏰ Scheduled automatic control
- 🚀 Automate device actions with custom scenarios
- 🎙️ Voice command support (Adafruit broker)
- 👁️ Facial recognition integration
---
# ⚙️ Setup
## 1. 📥 Clone the Repository
```bash
git clone https://github.com/dohuyminhdung/IoT_Project_Group_3
cd IoT_Project_Group_3
```
## 2. 🐍 Create and Activate Virtual Environment for Python gateway
```bash
cd gateway
python -m venv venv
source venv/bin/activate     # On Windows: venv\Scripts\activate
pip install -r requirements.txt
``` 
### 🔐 Configure Broker Credentials (for Adafruit broker):
Create a file named key.py in the gateway directory with the following content:
```python
secret_key = "" #🔑 Your Adafruit IO Key
username = ""   #👤 Your Adafruit IO Username
```
### 🚀 Running the Gateway
Depending on your setup, modify the main() function in main.py as follows:
- For Adafruit
```bash
async def main():
    threading.Thread(target=start_http_server, daemon=True).start()
    open_browser()
    await asyncio.gather(
        websocket_server(),
        face_detection_loop()
    )
```
- For CoreIoT
```bash
async def main():
    await asyncio.gather(
        face_detection_loop_camera()
    )
```
## 3. 📱 Mobile Device Setup
---
### 🛠️ Configuration
#### 📁 Create configuration file:
``
MobileApp/app/src/main/java/do_an/tkll/an_iot_app/secretKey.java
``
#### 📝 Add Configuration Content:
Replace placeholder values with your actual keys and feed info.
You may need 2 device on CoreIoT to config the communication (1 for your Application and 1 for your MCU)
```bash
public class secretKey {
     // 🔐 Adafruit IO credentials
    public static final String active_key = "your_aio_key_here"; // e.g., "aio_xxxxxxxx"
    public static final String username = "your_adafruit_username"; // e.g., "john_doe"

    // 🌡️ Temperature sensor
    public static final String webcambien1 = "your_username/feeds/cambien1";
    public static final String MQTTcambien1 = "your_username/feeds/cambien1";
    public static final String APIcambien1 = "api/v2/your_username/feeds/cambien1";

    // 💡 Light sensor
    public static final String webcambien2 = "your_username/feeds/cambien2";
    public static final String MQTTcambien2 = "your_username/feeds/cambien2";
    public static final String APIcambien2 = "api/v2/your_username/feeds/cambien2";

    // 💧 Humidity sensor
    public static final String webcambien3 = "your_username/feeds/cambien3";
    public static final String MQTTcambien3 = "your_username/feeds/cambien3";
    public static final String APIcambien3 = "api/v2/your_username/feeds/cambien3";

    // 🔥 Fire and gas sensors
    public static final String MQTTcambienchay = "your_username/feeds/cambien-chay";
    public static final String MQTTcambiengas = "your_username/feeds/cambien-gas";

    // 🔌 Device 1 control
    public static final String webbtn1 = "your_username/feeds/btn1";
    public static final String APIbtn1 = "api/v2/your_username/feeds/btn1";
    public static final String MQTTbtn1 = "your_username/feeds/btn1";

    // ⚙️ Device 2 control
    public static final String webbtn2 = "your_username/feeds/btn2";
    public static final String APIbtn2 = "api/v2/your_username/feeds/btn2";
    public static final String MQTTbtn2 = "your_username/feeds/btn2";

    // 🧯 Device 3 control
    public static final String webbtn3 = "your_username/feeds/btn3";
    public static final String APIbtn3 = "api/v2/your_username/feeds/btn3";
    public static final String MQTTbtn3 = "your_username/feeds/btn3";

    // 🤖 AI Teachable Machine integration
    public static final String web_AI = "your_username/feeds/ai";
    public static final String API_AI = "api/v2/your_username/feeds/ai";
    public static final String MQTT_AI = "your_username/feeds/ai";

    // 🌐 CoreIoT platform access
    public static final String coreIoT_AccessToken = "your_coreiot_access_token_here";
    public static final String coreIoT_authToken = "your_jwt_auth_token_here"; // JWT token of the user
    public static final String coreIoT_deviceId = "your_device_id_here"; // e.g., UUID of your MCU
}
```
#### ⚠️ Note: Do not commit this file with real credentials to version control. Keep your API keys and tokens secure.

---
### 🚀 For Adafruit:
You may find some code commented out (disabled) by default like this:
```java
btn3.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
//                try{
//                    if(isOn == true){
//                        sendDataMQTT(secretKey.MQTTbtn3, "1");
//                    }
//                    else{
//                        sendDataMQTT(secretKey.MQTTbtn3, "0");
//                    }
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
                String value = isOn ? "1" : "0";
                mqttHelperCoreIoT.updateSharedAttribute(deviceId, authToken, "servo", value, new ThingsBoardMQTTHelper.Callback() {
                    @Override
                    public void onSuccess(JsonObject attributes) {
                        Log.d("TB-HTTP", "Cập nhật trạng thái LED thành công");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("TB-HTTP", "Không thể cập nhật trạng thái LED", t);
                    }
                });
            }
```
Enable them and then disable or remove the CoreIoT logic (this is optional).
``
mqttHelperCoreIoT.updateSharedAttribute(...); // ← Comment this block if using Adafruit only
``
### 🚀 For CoreIoT:
#### Keep the source code unchanged
#### To enable proper functionality on the CoreIoT platform, you need to configure the Rule Chain for your devices (1 for MCU and 1 for the mobile application):
Step 1: Navigate to the Rule Chains section in your CoreIoT dashboard.

Step 2: Replace the default "Using Devices" rule chain with the custom one provided in this repository.

Step 3: Import the file "CoreIoT_Rule_Chain.json" from this repository

---
### 📱 Testing the App
You can run the app by:
- Creating a virtual Android device via Android Studio
- Connecting your physical Android phone via USB with developer mode enabled

💡 Make sure you grant all required permissions and connect to the internet.
## ⚙️ ESP32 Usage
- 🚀 For CoreIoT: Upload the firmware located in ESP32_CoreIoT
- 🚀 For Adafruit: Upload the firmware located in ESP32_Things
### 🔄OTA Update (Available in CoreIoT only)
For OTA updates, simply follow the [OTA setup repository](https://github.com/dohuyminhdung/IoT_Lab_3) using the firmware located in the `ESP32_CoreIoT/` folder.

💡 You can use the Arduino IDE or PlatformIO to upload the code to your ESP32. Make sure the correct board and port are selected.
