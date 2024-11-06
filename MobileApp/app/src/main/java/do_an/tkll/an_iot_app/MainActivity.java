package do_an.tkll.an_iot_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import adapter.AdapterViewPager;
import fragment.FragmentCamera;
import fragment.FragmentHome;
import fragment.FragmentScheduler;
import fragment.FragmentSetRule;

public class MainActivity extends AppCompatActivity {
    ViewPager2 pagerMain;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pagerMain = findViewById(R.id.pagerMain);
        bottomNav = findViewById(R.id.bottomNav);

        fragmentArrayList.add(new FragmentHome());
        fragmentArrayList.add(new FragmentScheduler());
        fragmentArrayList.add(new FragmentCamera());
        fragmentArrayList.add(new FragmentSetRule());
        AdapterViewPager adapterViewPager = new AdapterViewPager(this, fragmentArrayList);
        //setAdapter
        pagerMain.setAdapter(adapterViewPager);
        pagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNav.setSelectedItemId(R.id.itHome);
                        break;
                    case 1:
                        bottomNav.setSelectedItemId(R.id.itScheduler);
                        break;
                    case 2:
                        bottomNav.setSelectedItemId(R.id.itCamera);
                        break;
                    case 3:
                        bottomNav.setSelectedItemId(R.id.itSet);
                        break;
                }

                super.onPageSelected(position);
            }
        });
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itHome:
                        pagerMain.setCurrentItem(0);
                        break;
                    case R.id.itScheduler:
                        pagerMain.setCurrentItem(1);
                        break;
                    case R.id.itCamera:
                        pagerMain.setCurrentItem(2);
                        break;
                    case R.id.itSet:
                        pagerMain.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });

    }
}