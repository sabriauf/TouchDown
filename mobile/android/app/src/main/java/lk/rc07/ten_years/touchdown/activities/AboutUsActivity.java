package lk.rc07.ten_years.touchdown.activities;

import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import lk.rc07.ten_years.touchdown.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about_us);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView closeLayout = findViewById(R.id.txt_close);
        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        LinearLayout rateApp = (LinearLayout) findViewById(R.id.RateLayout);
//        rateApp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=lk.rc07.ten_years.touchdown"));
//                startActivity(i);
//            }
//        });
    }
}
