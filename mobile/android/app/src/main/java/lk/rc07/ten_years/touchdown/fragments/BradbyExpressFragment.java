package lk.rc07.ten_years.touchdown.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.config.Constant;

/**
 * Created by Sabri on 2/5/2017. Bradby Express promotional page
 */

public class BradbyExpressFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bradby_express, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE);
        String img_link = preferences.getString(Constant.PREFERENCES_EXPRESS_IMAGE, AppConfig.EXPRESS_DEFAULT_LINK);
//        final String redirect_link = preferences.getString(Constant.PREFERENCES_EXPRESS_LINK, AppConfig.BRADBY_EXPRESS_URL);

        WebView webview = view.findViewById(R.id.web_view);

        // Configure WebView settings
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript for interactive content
        webSettings.setDomStorageEnabled(true); // Enable DOM storage (local storage)
        webSettings.setLoadWithOverviewMode(true); // Zoom out if the content is too big
        webSettings.setUseWideViewPort(true); // Enable viewport meta tag

        // Optional: If you want to enable zooming
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Load a URL
        webview.loadUrl(img_link);

        return view;
    }
}
