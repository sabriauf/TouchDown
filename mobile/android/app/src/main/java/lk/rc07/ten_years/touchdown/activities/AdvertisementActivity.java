package lk.rc07.ten_years.touchdown.activities;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.utils.ImageViewAutoHeight;

public class AdvertisementActivity extends AppCompatActivity {

    public static final String EXTRAS_IMAGE_LINK = "img_link";
    public static final String EXTRAS_REDIRECT_LINK = "redirect_link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisment);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageViewAutoHeight img_advertisement = findViewById(R.id.img_advertisement);

        if(getIntent().getExtras() != null) {
            String link = getIntent().getExtras().getString(EXTRAS_IMAGE_LINK);
            Glide.with(this).load(link).placeholder(R.drawable.icon_book_placeholder)
                    .into(img_advertisement);
        }

        final String webLink = getIntent().getExtras().getString(EXTRAS_REDIRECT_LINK);

        if (webLink != null && !webLink.equals(""))
            img_advertisement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webLink));
                    startActivity(browserIntent);
                }
            });
    }
}
