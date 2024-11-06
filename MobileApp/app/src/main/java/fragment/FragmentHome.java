package fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import do_an.tkll.an_iot_app.MQTTHelper;
import do_an.tkll.an_iot_app.R;
import do_an.tkll.an_iot_app.secretKey;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {

    MQTTHelper mqttHelper;
    TextView txtTemp, txtLight, txtHumi;
    LabeledSwitch btn1, btn2;
    ImageView settingImage;
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
//        settingImage = view.findViewById(R.id.Setting);
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
                if(topic.contains("cambien1")){
                    txtTemp.setText(message.toString() + "°C");
                }
                else if(topic.contains("cambien2")){
                    txtLight.setText(message.toString() + " lux");
                }
                else if(topic.contains("cambien3")){
                    txtHumi.setText(message.toString() + "%");
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
}