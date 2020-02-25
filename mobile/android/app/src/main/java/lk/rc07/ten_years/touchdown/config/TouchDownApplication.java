package lk.rc07.ten_years.touchdown.config;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import lk.rc07.ten_years.touchdown.utils.ScoreObserver;

/**
 * Created by Sabri on 4/11/2017. Application class for Touchdown
 */

public class TouchDownApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ScoreObserver.handler.sendEmptyMessage(0);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
