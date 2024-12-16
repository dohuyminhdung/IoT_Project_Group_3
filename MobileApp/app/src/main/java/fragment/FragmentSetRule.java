package fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import do_an.tkll.an_iot_app.secretKey;

import com.github.angads25.toggle.widget.LabeledSwitch;

import java.util.ArrayList;

import adapter.TaskAdapter;
import do_an.tkll.an_iot_app.ConditionRule;
import do_an.tkll.an_iot_app.MQTTHelper;
import do_an.tkll.an_iot_app.R;
import do_an.tkll.an_iot_app.Task;
import do_an.tkll.an_iot_app.ConditionRuleViewModel;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSetRule#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSetRule extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public Spinner condition_type, comparison, turnType, deviceType;
    public EditText editTextValue;
    public Button buttonAddSetting;
    public RecyclerView recyclerViewTasks;
    public TaskAdapter taskAdapter;
    public ArrayList<Task> taskList;
    private ConditionRuleViewModel ruleViewModel;
    private ArrayList<ConditionRule> ruleList;

    private boolean condition_type_init = false;
    private boolean comparison_init = false;
    private boolean turnType_init = false;
    private boolean deviceType_init = false;

    public FragmentSetRule() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSetRule.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSetRule newInstance(String param1, String param2) {
        FragmentSetRule fragment = new FragmentSetRule();
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
        return inflater.inflate(R.layout.fragment_set_rule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        condition_type = view.findViewById(R.id.spinnerConditionType);
        comparison = view.findViewById(R.id.comparison);
        turnType = view.findViewById(R.id.turnType);
        deviceType = view.findViewById(R.id.deviceType);
        editTextValue = view.findViewById(R.id.Value);
        buttonAddSetting = view.findViewById(R.id.buttonAddSetting);
        recyclerViewTasks = view.findViewById(R.id.recyclerViewTasks);
        ruleViewModel = new ViewModelProvider(requireActivity()).get(ConditionRuleViewModel.class);
        ruleList = ruleViewModel.getRuleList();

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), taskList, ruleViewModel);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTasks.setAdapter(taskAdapter);

        buttonAddSetting.setOnClickListener(v -> addTask());

        condition_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(condition_type_init){
                    String item = adapterView.getItemAtPosition(position).toString();
                    Toast.makeText(getContext(), "Đã chọn " + item, Toast.LENGTH_SHORT).show();
                }
                else{
                    condition_type_init = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Xử lý khi không có mục nào được chọn
            }
        });

        ArrayList<String> condition = new ArrayList<String>();
        condition.add("Nhiệt Độ");
        condition.add("Độ Sáng");
        condition.add("Độ Ẩm");

        ArrayAdapter<String> condition_adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, condition);////////////////////////////////////////////////////////////////////////////////////////////////////
        condition_adapter.setDropDownViewResource(R.layout.select_dialog_item);
        condition_type.setAdapter(condition_adapter);


        comparison.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(comparison_init){
                    String item = adapterView.getItemAtPosition(position).toString();
                    switch (item){
                        case ">":
                            Toast.makeText(getContext(), "Đã chọn so sánh lớn hơn " + item, Toast.LENGTH_SHORT).show();
                            break;
                        case "<":
                            Toast.makeText(getContext(), "Đã chọn so sánh nhỏ hơn " + item, Toast.LENGTH_SHORT).show();
                            break;
                        case "=":
                            Toast.makeText(getContext(), "Đã chọn so sánh bằng nhau " + item, Toast.LENGTH_SHORT).show();
                            break;
                        default: break;
                    }
                }
                else{
                    comparison_init = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Xử lý khi không có mục nào được chọn
            }
        });

        ArrayList<String> compare = new ArrayList<String>();
        compare.add(">");
        compare.add("<");
        compare.add("=");

        ArrayAdapter<String> compare_adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, compare);
        compare_adapter.setDropDownViewResource(R.layout.select_dialog_item);
        comparison.setAdapter(compare_adapter);



        turnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(turnType_init){
                    String item = adapterView.getItemAtPosition(position).toString();
                    Toast.makeText(getContext(), "Đã chọn " + item, Toast.LENGTH_SHORT).show();
                }
                else{
                    turnType_init = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Xử lý khi không có mục nào được chọn
            }
        });

        ArrayList<String> turn = new ArrayList<String>();
        turn.add("Bật");
        turn.add("Tắt");

        ArrayAdapter<String> turn_adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, turn);
        turn_adapter.setDropDownViewResource(R.layout.select_dialog_item);
        turnType.setAdapter(turn_adapter);



        deviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(deviceType_init){
                    String item = adapterView.getItemAtPosition(position).toString();
                    Toast.makeText(getContext(), "Đã chọn " + item, Toast.LENGTH_SHORT).show();
                }
                else{
                    deviceType_init = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Xử lý khi không có mục nào được chọn
            }
        });

        ArrayList<String> device = new ArrayList<String>();
        device.add("Thiết bị 1");
        device.add("Thiết bị 2");
        device.add("Thiết bị 3");

        ArrayAdapter<String> device_adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, device);
        device_adapter.setDropDownViewResource(R.layout.select_dialog_item);
        deviceType.setAdapter(device_adapter);
    }
    private void addTask() {
        // Lấy dữ liệu từ các Spinner và EditText
        String condition = condition_type.getSelectedItem().toString(); //sensorType (Nhiet do, Do sang, Do am)
        String compare = comparison.getSelectedItem().toString();       //comparisonType (<, =, >)
        String value = editTextValue.getText().toString();              //threshold (30 do C, 109 lux, 40%) String
        String action = turnType.getSelectedItem().toString();          //operation (Bat/Tat)
        String device = deviceType.getSelectedItem().toString();        //device (Thiet bi 1/2/3)

        // Kiểm tra nếu `editTextValue` rỗng
        if(value.isEmpty()){
            Toast.makeText(getContext(), "Bạn chưa nhập giá trị ngưỡng!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra xem task mới có xung đột với task đã tồn tại không
        for (Task task : taskList) {
            if (task.getDevice().equals(device) &&
                    task.getCondition().equals(condition) &&
                    task.getValue().equals(value)){
                if(task.getComparison().equals(compare)){
                    if(!task.getAction().equals(action)){
                        Toast.makeText(getContext(), "Xung đột với tác vụ đã tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Tác vụ đã tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(task.getAction().equals(action)){
                        Toast.makeText(getContext(), "Xung đột với tác vụ đã tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }
        }


        editTextValue.setTextColor(Color.BLACK);
        editTextValue.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Ẩn bàn phím
                android.view.inputmethod.InputMethodManager imm =
                        (android.view.inputmethod.InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        float threshold; //= Float.parseFloat(value);                      //threshold (30 do C, 109 lux, 40%) Float
        try {
            threshold = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Giá trị ngưỡng không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if( 0.0 > threshold || threshold > 100){
            Toast.makeText(getContext(), "Giá trị ngưỡng không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        String btn = "NULL";
        switch (device){
            case "Thiết bị 1":
                btn = secretKey.MQTTbtn1;
                break;
            case "Thiết bị 2":
                btn = secretKey.MQTTbtn2;
                break;
            case "Thiết bị 3":
                btn = secretKey.MQTTbtn3;
                break;
            default: break;
        }
        String operation = action.equals("Bật") ? "on" : "off";
        // Tạo và thêm công việc mới vào danh sách
        Task newTask = new Task(condition, compare, value, action, device);
        taskList.add(newTask);
        taskAdapter.notifyItemInserted(taskList.size() - 1);

        ConditionRule rule = new ConditionRule(condition, btn, operation, threshold, compare);
        ruleViewModel.addRule(rule);
        Toast.makeText(getContext(), "Thêm tác vụ thành công!", Toast.LENGTH_SHORT).show();
    }
}


























