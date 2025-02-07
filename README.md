# My SmartHome App

## 1. Requirement environment 📑
- *Python*: 3.9
- *pip*

## 2. Setup Project
### 2.1. Setup and build gate way
1. Create enviroment for the project:
```console
python3.9 -m venv project_env
```
For more detail about virtual enviroment in python. Please referrence: [Creation of virtual environments](https://docs.python.org/3/library/venv.html)

2. Activate project enviroment.

3. Install all necessary packages for project
```console
pip install -r requirements.txt
```

4. create **key.py** file with following structure to store your key
```console
secret_key = <Adafruit_IO_Active_Key>
username = <Adafruit_IO_Username>
```

5. Run the project with the following command:
```console
python main.py
```

### 2.2. Running mobile device 

1. Install Android Studio IDE according to instructions at:
[https://developer.android.com/studio?hl=vi](https://developer.android.com/studio?hl=vi)

2. Create a virtual mobile device in Android Studio following the instructions at:
[https://developer.android.com/studio/run/managing-avds](https://developer.android.com/studio/run/managing-avds)

3. Open Project mobile app and create file `secretKey.java` to store your adafruit key for the project:

```console
public class secretKey {
    public static final String active_key = <Adafruit_IO_Active_Key>;
    public static final String username = <Adafruit_IO_Username>;
    public static final String MQTTcambien1 = <Adafruit_IO_Username_Feed_ID_1>; //temperature sensor
    public static final String MQTTcambien2 = <Adafruit_IO_Username_Feed_ID_2>; //light sensor
    public static final String MQTTcambien3 = <Adafruit_IO_Username_Feed_ID_3>; //himidity sensor
    public static final String MQTTcambienchay = <Adafruit_IO_Username_Feed_ID_4>; //fire sensor
    public static final String MQTTcambiengas = <Adafruit_IO_Username_Feed_ID_5>; //gas sensor
    //Other feeds to your divices...
}
```

6. Open the virtual device and press the "Run" button in the Android Studio IDE. The application will be installed onto your virtual device.
