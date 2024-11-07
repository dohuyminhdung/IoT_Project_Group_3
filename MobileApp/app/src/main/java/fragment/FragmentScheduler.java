package fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import adapter.SchedulerAdapter;
import do_an.tkll.an_iot_app.AddSchedulerActivity;
import do_an.tkll.an_iot_app.R;
import do_an.tkll.an_iot_app.Scheduler;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentScheduler#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentScheduler extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final int REQUEST_CODE_ADD_SCHEDULER = 1;
    public RecyclerView recyclerViewSchedulers;
    public SchedulerAdapter schedulerAdapter;
    public ArrayList<Scheduler> schedulerList;
    public FloatingActionButton fabAddScheduler;
    public FragmentScheduler() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentScheduler.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentScheduler newInstance(String param1, String param2) {
        FragmentScheduler fragment = new FragmentScheduler();
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
        return inflater.inflate(R.layout.fragment_scheduler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewSchedulers = view.findViewById(R.id.recyclerViewSchedulers);
        fabAddScheduler = view.findViewById(R.id.fabAddScheduler);

        schedulerList = new ArrayList<>(); // Lưu trữ danh sách hẹn giờ
        schedulerAdapter = new SchedulerAdapter(getContext(), schedulerList);
        recyclerViewSchedulers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSchedulers.setAdapter(schedulerAdapter);

        fabAddScheduler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến AddSchedulerActivity để thêm hẹn giờ
                Intent intent = new Intent(getContext(), AddSchedulerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_SCHEDULER);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_SCHEDULER && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            // Nhận dữ liệu từ AddSchedulerActivity
            String deviceName = data.getStringExtra("deviceName");
            String onOff = data.getStringExtra("onOff");
            String time = data.getStringExtra("time");


            // Tạo một đối tượng Scheduler mới và thêm vào danh sách
            String description = onOff + " " + deviceName + " vào lúc " + time;
            Scheduler newScheduler = new Scheduler(deviceName, time, description, onOff);
            schedulerList.add(newScheduler);
            schedulerAdapter.notifyItemInserted(schedulerList.size() - 1);
        }
    }
}