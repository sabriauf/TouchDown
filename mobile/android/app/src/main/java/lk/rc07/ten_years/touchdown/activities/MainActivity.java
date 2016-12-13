package lk.rc07.ten_years.touchdown.activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.adapters.PageAdapter;
import lk.rc07.ten_years.touchdown.utils.PageBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PageAdapter adapter = new PageAdapter(
                getSupportFragmentManager(), new PageBuilder()
                .addPage("LIVE")
                .addPage("FIXTURE")
                .addPage("RESULTS")
                .addPage("POINT TABLE")
                .addPage("FIXTURE"));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpager_tab);
        viewPagerTab.setViewPager(viewPager);
    }
}
