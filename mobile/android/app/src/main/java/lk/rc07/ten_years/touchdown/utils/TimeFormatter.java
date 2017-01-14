package lk.rc07.ten_years.touchdown.utils;

import android.content.Context;

import lk.rc07.ten_years.touchdown.R;

/**
 * Created by Sabri on 12/25/2016. manipulate time
 */

public class TimeFormatter {

    private final static long ONE_SECOND = 1000;
    private final static long SECONDS = 60;

    private final static long ONE_MINUTE = ONE_SECOND * SECONDS;
    private final static long MINUTES = 60;

    private final static long ONE_HOUR = ONE_MINUTE * MINUTES;
    private final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * HOURS;

    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT_LONG = "yyyy-MMMM-d";
    public static final String DATE_FORMAT_SHORT = "yyyy-MMM-d";
    public static final String DATE_FORMAT_ROUGH = "MMM yyyy";

    public static String millisToGameTime(Context context, Long time) {
        if(time != 0) {
            long difference = System.currentTimeMillis() - time;
            return String.valueOf(difference / ONE_MINUTE) + ":" + String.valueOf(difference % ONE_MINUTE);
        } else
            return context.getString(R.string.default_live_match_time);
    }
}
