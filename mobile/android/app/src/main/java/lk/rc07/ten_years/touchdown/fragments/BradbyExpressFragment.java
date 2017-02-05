package lk.rc07.ten_years.touchdown.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.config.AppConfig;

/**
 * Created by Sabri on 2/5/2017. Bradby Express promotional page
 */

public class BradbyExpressFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bradby_express, container, false);

        ImageView img_bradex = (ImageView) view.findViewById(R.id.img_bardex);
        img_bradex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.BRADBY_EXPRESS_URL));
                getActivity().startActivity(intent);
            }
        });

        return view;
    }
}
