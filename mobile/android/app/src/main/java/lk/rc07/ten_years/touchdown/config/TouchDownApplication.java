package lk.rc07.ten_years.touchdown.config;

import android.app.Application;

import lk.rc07.ten_years.touchdown.utils.ScoreObserver;

/**
 * Created by Sabri on 4/11/2017. Application class for Touchdown
 */

public class TouchDownApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ScoreObserver.handler.sendEmptyMessage(0);
    }
}
