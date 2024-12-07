package fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Looper;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import do_an.tkll.an_iot_app.ConditionRule;
import do_an.tkll.an_iot_app.MQTTHelper;
import do_an.tkll.an_iot_app.R;
import do_an.tkll.an_iot_app.Scheduler;
import do_an.tkll.an_iot_app.SchedulerViewModel;
import do_an.tkll.an_iot_app.secretKey;
import do_an.tkll.an_iot_app.ConditionRuleViewModel;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {
    MQTTHelper mqttHelper;
    TextView txtTemp, txtLight, txtHumi;
    LabeledSwitch btn1, btn2;

    private ConditionRuleViewModel ruleViewModel;
    private ArrayList<ConditionRule> ruleList;

    private SchedulerViewModel schedulerViewModel;
    private ArrayList<Scheduler> taskList;
    private Handler handler = new Handler();
    private Runnable checkSchedulerRunnable;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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

        ruleViewModel = new ViewModelProvider(requireActivity()).get(ConditionRuleViewModel.class);
        ruleList = ruleViewModel.getRuleList();

        schedulerViewModel = new ViewModelProvider(requireActivity()).get(SchedulerViewModel.class);
        taskList = schedulerViewModel.getSchedulerTasks();
//        schedulerViewModel.getSchedulerTasks().observe(getViewLifecycleOwner(), updatedTasks -> {
//            taskList = new ArrayList<>(updatedTasks);  // Cập nhật taskList khi dữ liệu thay đổi
//        });
        startSchedulerCheck();



        btn1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                try{
                    if(isOn == true){
                        sendDataMQTT( secretKey.MQTTbtn1, "1");
                    }
                    else{
                        sendDataMQTT( secretKey.MQTTbtn1, "0");
                    }
                } catch (MqttException e){
                    e.printStackTrace();
                }
            }
        });
        btn2.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                try{
                    if(isOn == true){
                        sendDataMQTT(secretKey.MQTTbtn2, "1");
                    }
                    else{
                        sendDataMQTT(secretKey.MQTTbtn2, "0");
                    }
                } catch (MqttException e){
                    e.printStackTrace();
                }
            }
        });


        startMQTT();
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
    public void startMQTT(){
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
                }
                else if(topic.contains("cambien2")){
                    txtLight.setText(message.toString() + " lux");
                    checkRules("Độ Sáng", sensorValue);
                }
                else if(topic.contains("cambien3")){
                    txtHumi.setText(message.toString() + "%");
                    checkRules("Độ Ẩm", sensorValue);
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
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
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
                }

                if (conditionMet) {
                    try {
                        // Nếu điều kiện thỏa mãn, bật/tắt thiết bị theo quy tắc
                        if (rule.operation.equals("on")) {
                            sendDataMQTT(rule.device, "1"); // Bật thiết bị
                        } else {
                            sendDataMQTT(rule.device, "0"); // Tắt thiết bị
                        }
                        Log.d("RuleCheck", "Rule satisfied: " + rule.sensorType + " " + rule.comparisonType + " " + rule.threshold);
                    } catch (MqttException e) {
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
                handler.postDelayed(this, 60000); // Kiểm tra mỗi phút
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
                    sendDataMQTT(task.getDeviceName(), task.getOnOff().equals("on") ? "1" : "0");
                    Log.d("SchedulerCheck", "Task executed for " + task.getDeviceName() + " with action: " + task.getOnOff());
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}