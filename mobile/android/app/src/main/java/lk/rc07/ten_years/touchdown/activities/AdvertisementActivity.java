package lk.rc07.ten_years.touchdown.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.utils.AppHandler;
import lk.rc07.ten_years.touchdown.utils.ImageViewAutoHeight;

public class AdvertisementActivity extends AppCompatActivity {

    public static final String EXTRAS_IMAGE_LINK = "img_link";
    public static final String EXTRAS_REDIRECT_LINK = "redirect_link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisment);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String link = getIntent().getExtras().getString(EXTRAS_IMAGE_LINK);
        final String webLink = getIntent().getExtras().getString(EXTRAS_REDIRECT_LINK);

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = AppHandler.getImageOptionBestQuality();

        ImageViewAutoHeight img_advertisement = findViewById(R.id.img_advertisement);

        imageLoader.displayImage(link, img_advertisement, options);

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
