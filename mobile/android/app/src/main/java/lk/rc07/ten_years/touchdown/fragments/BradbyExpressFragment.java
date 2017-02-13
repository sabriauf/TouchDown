package lk.rc07.ten_years.touchdown.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.config.Constant;
import lk.rc07.ten_years.touchdown.utils.AppHandler;

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

        ImageView img_bradex = (ImageView) view.findViewById(R.id.img_bardex);

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = AppHandler.getImageOption(imageLoader, getContext(), R.drawable.icon_book_placeholder);

        imageLoader.displayImage(img_link, img_bradex, options);

        img_bradex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect_link));
                getActivity().startActivity(intent);
            }
        });
        return view;
    }
}
