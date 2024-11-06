package do_an.tkll.an_iot_app;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SettingControlRule extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_control_rule); // Đảm bảo bạn có file XML tương ứng
        Spinner condition_type = findViewById(R.id.spinnerConditionType);
        Spinner comparison = findViewById(R.id.comparison);
        Spinner turnType = findViewById(R.id.turnType);
        Spinner deviceType = findViewById(R.id.deviceType);

        condition_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(SettingControlRule.this, "Đã chọn " + item, Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> condition = new ArrayList<String>();
        condition.add("Nhiệt Độ");
        condition.add("Độ Sáng");
        condition.add("Độ Ẩm");

        ArrayAdapter<String> condition_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, condition);
        condition_adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        condition_type.setAdapter(condition_adapter);

        comparison.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(SettingControlRule.this, "Đã chọn " + item, Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> compare = new ArrayList<String>();
        compare.add(">");
        compare.add("<");
        compare.add("=");

        ArrayAdapter<String> compare_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, compare);
        compare_adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        comparison.setAdapter(compare_adapter);


        turnType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(SettingControlRule.this, "Đã chọn " + item, Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> turn = new ArrayList<String>();
        turn.add("On");
        turn.add("Off");

        ArrayAdapter<String> turn_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, turn);
        turn_adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        turnType.setAdapter(condition_adapter);

        deviceType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(SettingControlRule.this, "Đã chọn " + item, Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> device = new ArrayList<String>();
        device.add("On");
        device.add("Off");

        ArrayAdapter<String> device_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, device);
        device_adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        deviceType.setAdapter(device_adapter);
    }
}
