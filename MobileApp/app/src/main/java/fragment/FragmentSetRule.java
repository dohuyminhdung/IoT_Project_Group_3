package fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.github.angads25.toggle.widget.LabeledSwitch;

import java.util.ArrayList;

import adapter.TaskAdapter;
import do_an.tkll.an_iot_app.MQTTHelper;
import do_an.tkll.an_iot_app.R;
import do_an.tkll.an_iot_app.Task;

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

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), taskList);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTasks.setAdapter(taskAdapter);

        buttonAddSetting.setOnClickListener(v -> addTask());

        condition_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), "Đã chọn " + item, Toast.LENGTH_SHORT).show();
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

        ArrayAdapter<String> condition_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, condition);
        condition_adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        condition_type.setAdapter(condition_adapter);


        comparison.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), "Đã chọn " + item, Toast.LENGTH_SHORT).show();
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

        ArrayAdapter<String> compare_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, compare);
        compare_adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        comparison.setAdapter(compare_adapter);



        turnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), "Đã chọn " + item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Xử lý khi không có mục nào được chọn
            }
        });

        ArrayList<String> turn = new ArrayList<String>();
        turn.add("Bật");
        turn.add("Tắt");

        ArrayAdapter<String> turn_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, turn);
        turn_adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        turnType.setAdapter(turn_adapter);



        deviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), "Đã chọn " + item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Xử lý khi không có mục nào được chọn
            }
        });

        ArrayList<String> device = new ArrayList<String>();
        device.add("Thiết bị 1");
        device.add("Thiết bị 2");

        ArrayAdapter<String> device_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, device);
        device_adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        deviceType.setAdapter(device_adapter);
    }
    private void addTask() {
        // Lấy dữ liệu từ các Spinner và EditText
        String condition = condition_type.getSelectedItem().toString();
        String compare = comparison.getSelectedItem().toString();
        String value = editTextValue.getText().toString();
        String action = turnType.getSelectedItem().toString();
        String device = deviceType.getSelectedItem().toString();

        // Tạo và thêm công việc mới vào danh sách
        Task newTask = new Task(condition, compare, value, action, device);
        taskList.add(newTask);
        taskAdapter.notifyItemInserted(taskList.size() - 1);
    }
}


























