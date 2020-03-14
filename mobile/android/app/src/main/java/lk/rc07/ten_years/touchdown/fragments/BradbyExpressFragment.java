package lk.rc07.ten_years.touchdown.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.config.Constant;
import lk.rc07.ten_years.touchdown.utils.ImageViewAutoHeight;

/**
 * Created by Sabri on 2/5/2017. Bradby Express promotional page
 */

public class BradbyExpressFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bradby_express, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE);
        String img_link = preferences.getString(Constant.PREFERENCES_EXPRESS_IMAGE, AppConfig.EXPRESS_DEFAULT_LINK);
        final String redirect_link = preferences.getString(Constant.PREFERENCES_EXPRESS_LINK, AppConfig.BRADBY_EXPRESS_URL);

        ImageViewAutoHeight img_bradex = view.findViewById(R.id.img_bardex);

        Glide.with(view.getContext()).load(img_link).placeholder(R.drawable
                .icon_book_placeholder).into(img_bradex);

        img_bradex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_link));
                    Activity activity = getActivity();
                    if (activity != null)
                        activity.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
        return view;
    }
}
