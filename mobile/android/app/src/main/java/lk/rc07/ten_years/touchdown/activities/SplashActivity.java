package lk.rc07.ten_years.touchdown.activities;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import lk.rc07.ten_years.touchdown.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

//        ImageViewAutoHeight img_tag = (ImageViewAutoHeight) findViewById(R.id.img_tag);
//        Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(),
//                R.anim.scale_out_effect);
//
//        img_tag.startAnimation(animZoomOut);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }
}
