package do_an.tkll.an_iot_app;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import do_an.tkll.an_iot_app.R;

public class AddSchedulerActivity extends AppCompatActivity {
    private Spinner spinnerDevice, SchTurnType;
    private TimePicker timePicker;
    private Button buttonSetTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scheduler); // Tạo layout activity_add_scheduler.xml trong res/layout
        // Tìm TimePicker và thiết lập chế độ 24 giờ
        spinnerDevice = findViewById(R.id.SchDeviceType);
        SchTurnType = findViewById(R.id.SchTurnType);
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true); // Thiết lập 24 giờ trong mã Java
        buttonSetTimer = findViewById(R.id.buttonSetTimer);

        for (int i = 0; i < timePicker.getChildCount(); i++) {
            View child = timePicker.getChildAt(i);
            if (child instanceof NumberPicker) {
                NumberPicker picker = (NumberPicker) child;

                // Duyệt qua các thành phần con của NumberPicker
                for (int j = 0; j < timePicker.getChildCount(); j++) {
                    View npChild = timePicker.getChildAt(j);
                    if (npChild instanceof EditText) {
                        EditText editText = (EditText) npChild;
                        editText.setTextColor(Color.BLACK); // Màu chữ
                        editText.setBackgroundColor(Color.TRANSPARENT); // Nền trong suốt (nếu cần)
                    }
                }
            }
        }

        buttonSetTimer.setOnClickListener(view -> {
            String deviceName = spinnerDevice.getSelectedItem().toString();
            String onOff = SchTurnType.getSelectedItem().toString();
            int hour, minute;
                if (Build.VERSION.SDK_INT >= 23) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }
            String time = String.format("%02d:%02d", hour, minute);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("deviceName", deviceName);
            resultIntent.putExtra("onOff", onOff);
            resultIntent.putExtra("time", time);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        ArrayAdapter<String> adapterDevice = new ArrayAdapter<>(
                this,
                R.layout.spinner_item, // Layout cho mục đã chọn
                getResources().getStringArray(R.array.device_types) // Array cho các mục Spinner
        );
        adapterDevice.setDropDownViewResource(R.layout.select_dialog_item); // Layout cho các mục xổ xuống
        spinnerDevice.setAdapter(adapterDevice);


        ArrayAdapter<String> adapterOnOff = new ArrayAdapter<>(
                this,
                R.layout.spinner_item, // Layout cho mục đã chọn
                getResources().getStringArray(R.array.turn_types) // Array cho các mục Spinner
        );
        adapterOnOff.setDropDownViewResource(R.layout.select_dialog_item); // Layout cho các mục xổ xuống
        SchTurnType.setAdapter(adapterOnOff);
    }
}
