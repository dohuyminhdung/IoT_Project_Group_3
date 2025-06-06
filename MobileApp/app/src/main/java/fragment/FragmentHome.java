package fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Looper;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.JsonObject;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import do_an.tkll.an_iot_app.ConditionRule;
import do_an.tkll.an_iot_app.MQTTHelper;
import do_an.tkll.an_iot_app.R;
import do_an.tkll.an_iot_app.Scheduler;
import do_an.tkll.an_iot_app.SchedulerViewModel;
import do_an.tkll.an_iot_app.ThingsBoardMQTTHelper;
import do_an.tkll.an_iot_app.secretKey;
import do_an.tkll.an_iot_app.ConditionRuleViewModel;

public class FragmentHome extends Fragment {
    MQTTHelper mqttHelper; //ADAFRUIT
    ThingsBoardMQTTHelper mqttHelperCoreIoT; //CORE IOT
    String deviceId = secretKey.coreIoT_deviceId; // ID của thiết bị vi xử lý
    String authToken = secretKey.coreIoT_authToken; // JWT token của người dùng

    TextView txtTemp, txtLight, txtHumi;
    LabeledSwitch btn1, btn2, btn3;

    private ConditionRuleViewModel ruleViewModel;
    private ArrayList<ConditionRule> ruleList;

    private SchedulerViewModel schedulerViewModel;
    private ArrayList<Scheduler> taskList;
    private Handler handler = new Handler();
    private Runnable checkSchedulerRunnable;
    private Handler handlerCoreIoT;
    private Runnable runnableCoreIoT;

    ///====================== LINECHART ====================
    private LineChart tempChart;
    private LineChart lightChart;
    private LineChart humiChart;
    List<Entry> entries_temp;
    List<Entry> entries_light;
    List<Entry> entries_humi;
    LineDataSet dataSet_temp;
    LineDataSet dataSet_light;
    LineDataSet dataSet_humi;
    LineData lineData_temp;
    LineData lineData_light;
    LineData lineData_humi;


    public FragmentHome() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtTemp = view.findViewById(R.id.txtTemperature);
        txtLight = view.findViewById(R.id.txtLightning);
        txtHumi = view.findViewById(R.id.txtHumidity);
        btn1 = view.findViewById(R.id.switch1);
        btn2 = view.findViewById(R.id.switch2);
        btn3 = view.findViewById(R.id.switch3);
        tempChart = view.findViewById(R.id.tempChart);
        lightChart = view.findViewById(R.id.lightChart);
        humiChart = view.findViewById(R.id.humiChart);

        // Tạo dữ liệu mẫu
        entries_temp = new ArrayList<>();
        entries_temp.add(new Entry(0, 27));  // (x, y)
        entries_temp.add(new Entry(1, 30));
        entries_temp.add(new Entry(2, 31));
        entries_temp.add(new Entry(3, 28));

        entries_light = new ArrayList<>();
        entries_light.add(new Entry(0, 70));  // (x, y)
        entries_light.add(new Entry(1, 44));
        entries_light.add(new Entry(2, 55));
        entries_light.add(new Entry(3, 60));

        entries_humi = new ArrayList<>();
        entries_humi.add(new Entry(0, 20));  // (x, y)
        entries_humi.add(new Entry(1, 36));
        entries_humi.add(new Entry(2, 29));
        entries_humi.add(new Entry(3, 40));

        // Tạo DataSet cho đồ thị
        dataSet_temp = new LineDataSet(entries_temp, "Nhiệt độ");
        dataSet_temp.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Đường cong mềm mại
        dataSet_temp.setDrawFilled(true);                   // Kích hoạt gradient fill
        dataSet_temp.setFillDrawable(getResources().getDrawable(R.drawable.linechart_gradient_red));
        dataSet_temp.setDrawCircles(false);                 // Ẩn các điểm trên đồ thị
        dataSet_temp.setColor(Color.RED);                  // Màu của đường chính
        dataSet_temp.setLineWidth(2f);                      // Độ dày của đường chính

        dataSet_light = new LineDataSet(entries_light, "Độ sáng");
        dataSet_light.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Đường cong mềm mại
        dataSet_light.setDrawFilled(true);                   // Kích hoạt gradient fill
        dataSet_light.setFillDrawable(getResources().getDrawable(R.drawable.linechart_gradient_yellow));
        dataSet_light.setDrawCircles(false);                 // Ẩn các điểm trên đồ thị
        dataSet_light.setColor(Color.YELLOW);                  // Màu của đường chính
        dataSet_light.setLineWidth(2f);                      // Độ dày của đường chính

        dataSet_humi = new LineDataSet(entries_humi, "Độ Ẩm");
        dataSet_humi.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Đường cong mềm mại
        dataSet_humi.setDrawFilled(true);                   // Kích hoạt gradient fill
        dataSet_humi.setFillDrawable(getResources().getDrawable(R.drawable.linechart_gradient));
        dataSet_humi.setDrawCircles(false);                 // Ẩn các điểm trên đồ thị
        dataSet_humi.setColor(Color.BLUE);                  // Màu của đường chính
        dataSet_humi.setLineWidth(2f);                      // Độ dày của đường chính

        // Tạo LineData
        lineData_temp = new LineData(dataSet_temp);
        lineData_light = new LineData(dataSet_light);
        lineData_humi = new LineData(dataSet_humi);
        // Gắn dữ liệu cho LineChart
        tempChart.setData(lineData_temp);
        lightChart.setData(lineData_light);
        humiChart.setData(lineData_humi);
        // Tùy chỉnh giao diện LineChart
        tempChart.getDescription().setEnabled(false);   // Tắt mô tả
        tempChart.getAxisRight().setEnabled(false);     // Tắt trục Y bên phải
        tempChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Trục X ở dưới cùng
        tempChart.getLegend().setEnabled(false);        // Tắt chú thích
        tempChart.animateY(1000, Easing.EaseInOutCubic); // Hiệu ứng hoạt hình

        lightChart.getDescription().setEnabled(false);   // Tắt mô tả
        lightChart.getAxisRight().setEnabled(false);     // Tắt trục Y bên phải
        lightChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Trục X ở dưới cùng
        lightChart.getLegend().setEnabled(false);        // Tắt chú thích
        lightChart.animateY(1000, Easing.EaseInOutCubic); // Hiệu ứng hoạt hình

        humiChart.getDescription().setEnabled(false);   // Tắt mô tả
        humiChart.getAxisRight().setEnabled(false);     // Tắt trục Y bên phải
        humiChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Trục X ở dưới cùng
        humiChart.getLegend().setEnabled(false);        // Tắt chú thích
        humiChart.animateY(1000, Easing.EaseInOutCubic); // Hiệu ứng hoạt hình


        ruleViewModel = new ViewModelProvider(requireActivity()).get(ConditionRuleViewModel.class);
        ruleList = ruleViewModel.getRuleList();

        schedulerViewModel = new ViewModelProvider(requireActivity()).get(SchedulerViewModel.class);
        taskList = schedulerViewModel.getSchedulerTasks();
        startSchedulerCheck();

//        #################### ADAFRUIT #########################
        btn1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
//                try{
//                    if(isOn == true){
//                        sendDataMQTT( secretKey.MQTTbtn1, "1");
//                    }
//                    else{
//                        sendDataMQTT( secretKey.MQTTbtn1, "0");
//                    }
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
                String value = isOn ? "1" : "0";
                mqttHelperCoreIoT.updateSharedAttribute(deviceId, authToken, "led", value, new ThingsBoardMQTTHelper.Callback() {
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
        });
        btn2.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
//                try{
//                    if(isOn == true){
//                        sendDataMQTT(secretKey.MQTTbtn2, "1");
//                    }
//                    else{
//                        sendDataMQTT(secretKey.MQTTbtn2, "0");
//                    }
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
                String value = isOn ? "1" : "0";
                mqttHelperCoreIoT.updateSharedAttribute(deviceId, authToken, "fan", value, new ThingsBoardMQTTHelper.Callback() {
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
        });
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
        });
        startMQTT();   //ADAFRUIT
        startMQTT_CoreIoT();
    }
    public void sendDataMQTT(String topic, String value) throws MqttException {
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);
        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);
        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
            //publish(topic, b, msg.getQos(), msg.isRetained());
        }
        catch(MqttException e){}
    }

    public void sendDataMQTT_CoreIoT(String key, String value) {
        String jsonPayload = "{\"" + key + "\": " + value + "}";
        mqttHelperCoreIoT.sendTelemetry(jsonPayload);
        mqttHelperCoreIoT.updateSharedAttribute(deviceId, authToken, key, value, new ThingsBoardMQTTHelper.Callback() {
            @Override
            public void onSuccess(JsonObject attributes) {
                Log.d("TB-HTTP", "Cập nhật trạng thái thiết bị thành công");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("TB-HTTP", "Không thể cập nhật trạng thái thiết bị", t);
            }
        });
    }

    public void startMQTT(){
        //ADAFRUIT
        mqttHelper = new MQTTHelper(getView().getContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
            }
            @Override
            public void connectionLost(Throwable cause) {
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + "***" + message.toString());
                float sensorValue = Float.parseFloat(message.toString());
                if(topic.contains("cambien1")){
                    txtTemp.setText(message.toString() + "°C");
                    checkRules("Nhiệt Độ", sensorValue);
                    LineData data = tempChart.getData();
                    if (data != null) {
                        ILineDataSet set = data.getDataSetByIndex(0);
                        if (set != null) {
                            set.addEntry(new Entry(set.getEntryCount(), sensorValue)); // Thêm giá trị mới
                            data.notifyDataChanged();
                            tempChart.notifyDataSetChanged();
                            tempChart.invalidate(); // Làm mới đồ thị
                        }
                    }
                }
                else if(topic.contains("cambien2")){
                    txtLight.setText(message.toString() + "%");
                    checkRules("Độ Sáng", sensorValue);
                    LineData data = lightChart.getData();
                    if (data != null) {
                        ILineDataSet set = data.getDataSetByIndex(0);
                        if (set != null) {
                            set.addEntry(new Entry(set.getEntryCount(), sensorValue)); // Thêm giá trị mới
                            data.notifyDataChanged();
                            lightChart.notifyDataSetChanged();
                            lightChart.invalidate(); // Làm mới đồ thị
                        }
                    }
                }
                else if(topic.contains("cambien3")){
                    txtHumi.setText(message.toString() + "%");
                    checkRules("Độ Ẩm", sensorValue);
                    LineData data = humiChart.getData();
                    if (data != null) {
                        ILineDataSet set = data.getDataSetByIndex(0);
                        if (set != null) {
                            set.addEntry(new Entry(set.getEntryCount(), sensorValue)); // Thêm giá trị mới
                            data.notifyDataChanged();
                            humiChart.notifyDataSetChanged();
                            humiChart.invalidate(); // Làm mới đồ thị
                        }
                    }
                }
                else if(topic.contains("btn1")){
                    if(message.toString().equals("1")){
                        btn1.setOn(true);
                    }
                    else{
                        btn1.setOn(false);
                    }
                }
                else if(topic.contains("btn2")){
                    if(message.toString().equals("1")){
                        btn2.setOn(true);
                    }
                    else{
                        btn2.setOn(false);
                    }
                }
                else if(topic.contains("btn3")){
                    if(message.toString().equals("1")){
                        btn3.setOn(true);
                    }
                    else{
                        btn3.setOn(false);
                    }
                }
                else if(topic.contains("cambien-chay")) {
                    if(message.toString().equals("0")){
                        //make toast
                        Toast.makeText(getContext(), "Khu vực của bạn đang gặp nguy hiểm! Có cháy!", Toast.LENGTH_LONG).show();
                        //send notify
                        sendNotification("Khu vực của bạn đang gặp nguy hiểm! Phát hiện cháy!");
//                        playAlarm(); //make noise
                        vibrateDevice();//rung
                    }
                }
                else if(topic.contains("cambien-gas")){
                    if(message.toString().equals("0")){
                        //make toast
                        Toast.makeText(getContext(), "Phát hiện có khí độc tại khu vực của bạn!", Toast.LENGTH_LONG).show();
                        //send notify
                        sendNotification("Phát hiện có khí độc tại khu vực của bạn!");
//                        playAlarm(); //make noise
                        vibrateDevice();//rung
                    }
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) { }
        });
    }

    public void startMQTT_CoreIoT() {
        mqttHelperCoreIoT = new ThingsBoardMQTTHelper(getContext());
        // Thiết lập Handler để lấy dữ liệu định kỳ
        handlerCoreIoT = new Handler();
        runnableCoreIoT = new Runnable() {
            @Override
            public void run() {
                mqttHelperCoreIoT.getSharedAttributes(deviceId, authToken, new ThingsBoardMQTTHelper.Callback() {
                    @Override
                    public void onSuccess(JsonObject attributes) {
                        Log.d("TB-HTTP", "Received attributes: " + attributes.toString());
                        if (attributes.isJsonObject()) {
                            JsonObject attrObject = attributes.getAsJsonObject();
                            if (attrObject.has("temperature")) {
                                Log.d("TB-HTTP", "Temp: " + attrObject.get("temperature").getAsFloat());
                            }
                            else{
                                Log.d("TB-HTTP", "No Temp receive" );
                            }
                        }

                        // Xử lý dữ liệu nhiệt độ
                        if (attributes.has("temperature")) {
                            float temp = attributes.get("temperature").getAsFloat();
                            txtTemp.setText(temp + "°C");
                            checkRules("Nhiệt Độ", temp);
                            updateChart(tempChart, temp);
                        }
                        // Xử lý dữ liệu độ sáng
                        if (attributes.has("light")) {
                            float light = attributes.get("light").getAsFloat();
                            txtLight.setText(light + "%");
                            checkRules("Độ Sáng", light);
                            updateChart(lightChart, light);
                        }
                        // Xử lý dữ liệu độ ẩm
                        if (attributes.has("humidity")) {
                            float humi = attributes.get("humidity").getAsFloat();
                            txtHumi.setText(humi + "%");
                            checkRules("Độ Ẩm", humi);
                            updateChart(humiChart, humi);
                        }
                        // Xử lý trạng thái LED
                        if (attributes.has("led")) {
                            int ledState = attributes.get("led").getAsInt();
                            btn1.setOn(ledState == 1);
                        }
                        // Xử lý trạng thái quạt
                        if (attributes.has("fan")) {
                            int fanState = attributes.get("fan").getAsInt();
                            btn2.setOn(fanState == 1);
                        }
                        // Xử lý trạng thái servo
                        if (attributes.has("servo")) {
                            int servoState = attributes.get("servo").getAsInt();
                            btn3.setOn(servoState == 1);
                        }
                        // Xử lý cảnh báo cháy
                        if (attributes.has("fire") && attributes.get("fire").getAsInt() == 1) {
                            Toast.makeText(getContext(), "Khu vực của bạn đang gặp nguy hiểm! Có cháy!", Toast.LENGTH_LONG).show();
                            sendNotification("Khu vực của bạn đang gặp nguy hiểm! Phát hiện cháy!");
                            vibrateDevice();
                        }
                        // Xử lý cảnh báo khí độc
                        if (attributes.has("gas") && attributes.get("gas").getAsInt() == 1) {
                            Toast.makeText(getContext(), "Phát hiện có khí độc tại khu vực của bạn!", Toast.LENGTH_LONG).show();
                            sendNotification("Phát hiện có khí độc tại khu vực của bạn!");
                            vibrateDevice();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("TB-HTTP", "Không thể lấy dữ liệu shared attributes", t);
                    }
                });
                handler.postDelayed(this, 5000); // Lặp lại mỗi 5 giây
            }
        };
        handler.post(runnableCoreIoT); // Bắt đầu lấy dữ liệu
    }

    private void updateChart(LineChart chart, float value) {
        LineData data = chart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set != null) {
                set.addEntry(new Entry(set.getEntryCount(), value));
                data.notifyDataChanged();
                chart.notifyDataSetChanged();
                chart.invalidate();
            }
        }
    }




    private void checkRules(String sensorType, float sensorValue) {
        for (ConditionRule rule : ruleList) { // `ruleList` là danh sách lưu các quy tắc
            // Kiểm tra loại cảm biến và điều kiện ngưỡng
            if (rule.sensorType.equals(sensorType)) {
                boolean conditionMet = false;
                // Kiểm tra loại điều kiện so sánh
                switch (rule.comparisonType) {
                    case ">":
                        conditionMet = sensorValue > rule.threshold;
                        break;
                    case "=":
                        conditionMet = sensorValue == rule.threshold;
                        break;
                    case "<":
                        conditionMet = sensorValue < rule.threshold;
                        break;
                    default: break;
                }

                if (conditionMet) {
                    try {
                        // Nếu điều kiện thỏa mãn, bật/tắt thiết bị theo quy tắc
                        if (rule.operation.equals("on")) {
                            sendDataMQTT_CoreIoT(rule.device, "1"); // Bật thiết bị
                        } else {
                            sendDataMQTT_CoreIoT(rule.device, "0"); // Tắt thiết bị
                        }
                        Log.d("RuleCheck", "Rule satisfied: " + rule.sensorType + " " + rule.comparisonType + " " + rule.threshold);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void startSchedulerCheck() {
        checkSchedulerRunnable = new Runnable() {
            @Override
            public void run() {
                checkSchedulerTasks();
                handler.postDelayed(this, 10000); // Kiểm tra mỗi 10s
            }
        };
        handler.post(checkSchedulerRunnable);
    }

    private void checkSchedulerTasks() {
        Calendar currentCalendar = Calendar.getInstance();
        String currentTime = new SimpleDateFormat("HH:mm").format(currentCalendar.getTime());

        for (Scheduler task : taskList) {
            if (task.getTime().equals(currentTime)) {
                try {
                    String name = task.getDeviceName();
                    boolean isOn = task.getOnOff().equals("on");
                    switch(name){
                        case secretKey.MQTTbtn1:
                            sendDataMQTT_CoreIoT("led", isOn ? "1" : "0");
                            btn1.setOn(isOn);
                            break;
                        case secretKey.MQTTbtn2:
                            sendDataMQTT_CoreIoT("fan", isOn ? "1" : "0");
                            btn2.setOn(isOn);
                            break;
                        case secretKey.MQTTbtn3:
                            sendDataMQTT_CoreIoT("servo", isOn ? "1" : "0");
                            btn3.setOn(isOn);
                            break;
                        default: break;
                    }
                    Log.d("SchedulerCheck", "Task executed for " + task.getDeviceName() + " with action: " + task.getOnOff());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendNotification(String messageBody) {
        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "fire_alert_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Fire Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for fire alerts");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(requireContext(), channelId)
                .setSmallIcon(R.drawable.ic_alert)
                .setContentTitle("Cảnh báo cháy")
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(1, notificationBuilder.build());
    }

    //    private void playAlarm() {
//        MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alarm_sound);
//        mediaPlayer.start();
//    }
    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(3000);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mqttHelperCoreIoT != null) {
            try {
                mqttHelperCoreIoT.mqttAndroidClient.disconnect();
                Log.d("TB-MQTT", "Disconnected MQTT client");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        handler.removeCallbacks(checkSchedulerRunnable); // Dừng scheduler khi fragment hủy
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mqttHelperCoreIoT != null && !mqttHelperCoreIoT.mqttAndroidClient.isConnected()) {
            mqttHelperCoreIoT.connect();
            Log.d("TB-MQTT", "Reconnecting MQTT client");
        }
        startSchedulerCheck(); // Khởi động lại scheduler khi fragment hiển thị
    }

}