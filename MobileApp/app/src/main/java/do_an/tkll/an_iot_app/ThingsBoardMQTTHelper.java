package do_an.tkll.an_iot_app;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ThingsBoardMQTTHelper {
    public MqttAndroidClient mqttAndroidClient;

    // Cập nhật với server của bạn
    final String serverUri = "tcp://app.coreiot.io:1883";

    // Access token của thiết bị trên ThingsBoard
    final String accessToken = secretKey.coreIoT_AccessToken;
    final String clientId = MqttClient.generateClientId();

    public ThingsBoardMQTTHelper(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("TB-MQTT", "Connected to: " + serverURI);
                subscribeToAttributeResponse();
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.w("TB-MQTT", "Connection lost, attempting to reconnect...");
                connect(); // Gọi lại hàm connect để khôi phục kết nối
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.d("TB-MQTT", "Received on [" + topic + "]: " + message.toString());
                // Không cần xử lý messageArrived vì không subscribe
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("TB-MQTT", "Delivery complete");
            }
        });

        connect();
    }

    public void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(accessToken); // không cần password
        options.setAutomaticReconnect(true); // Bật tự động reconnect
        try {
            mqttAndroidClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("TB-MQTT", "Connection successful");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("TB-MQTT", "Connection failed: " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribeToAttributeResponse() {
        try {
            mqttAndroidClient.subscribe("v1/devices/me/attributes/response/+", 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void requestServerAttributes(String sharedKeys) {
        String topic = "v1/devices/me/attributes/request/1";
        String payload = "{\"sharedKeys\":\"" + sharedKeys + "\"}";

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            mqttAndroidClient.publish(topic, message);
            Log.d("TB-MQTT", "Requested attributes: " + sharedKeys);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendTelemetry(String jsonPayload) {
        if (!mqttAndroidClient.isConnected()) {
            Log.w("TB-MQTT", "Client not connected, attempting to reconnect...");
            connect();
            // Optional: Chờ một chút để kết nối được thiết lập trước khi gửi
            try {
                Thread.sleep(500); // Chờ 500ms, có thể điều chỉnh
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String topic = "v1/devices/me/telemetry";
        String topic_broker = "v1/devices/me/attributes"; //shared attribute topic
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(jsonPayload.getBytes());
            mqttAndroidClient.publish(topic, message);
            mqttAndroidClient.publish(topic_broker, message);
            Log.d("TB-MQTT", "Sent telemetry: " + jsonPayload);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Phương thức mới để lấy Shared Attributes từ thiết bị qua API REST
    public void updateSharedAttribute(String deviceId, String authToken, String key, String value, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String payload = "{\"" + key + "\": " + value + "}";
        RequestBody body = RequestBody.create(payload, JSON);
        Request request = new Request.Builder()
                .url("https://app.coreiot.io/api/plugins/telemetry/DEVICE/" + deviceId + "/attributes/SHARED_SCOPE")
                .header("Authorization", "Bearer " + authToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(new Exception("Không thể cập nhật attribute: " + response.code()));
                }
            }
        });
    }

    public void getSharedAttributes(String deviceId, String authToken, Callback callback) {
        // Sử dụng Retrofit hoặc OkHttp để gửi yêu cầu HTTP
        // Ví dụ với OkHttp (cần thêm dependency OkHttp vào project)
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://app.coreiot.io/api/plugins/telemetry/DEVICE/" + deviceId + "/values/attributes/SHARED_SCOPE")
                .header("Authorization", "Bearer " + authToken) // JWT token từ ThingsBoard
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        JsonArray attributesArray = new Gson().fromJson(jsonData, JsonArray.class);
                        JsonObject attributes = new JsonObject();
                        for(JsonElement element : attributesArray){
                            JsonObject attr = element.getAsJsonObject();
                            String key = attr.get("key").getAsString();
                            JsonElement value = attr.get("value");
                            attributes.add(key, value);
                        }
                        callback.onSuccess(attributes);
                    } catch (Exception e) {
                        callback.onFailure(e);
                    }
                } else {
                    callback.onFailure(new Exception("Failed to get attributes: " + response.code()));
                }
            }
        });
    }

    // Interface cho callback
    public interface Callback {
        void onSuccess(JsonObject attributes);
        void onFailure(Throwable t);
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }
}
