package lk.rc07.ten_years.touchdown.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lk.rc07.ten_years.touchdown.R;

/**
 * Created by Sabri on 12/13/2016. fragment to show the live score
 */

public class LiveFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        return view;
    }
}
