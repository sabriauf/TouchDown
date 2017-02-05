package lk.rc07.ten_years.touchdown.utils;

import android.content.Context;

import java.util.Calendar;
import java.util.Locale;

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
        if (time != 0) {
            long difference = Calendar.getInstance().getTimeInMillis() - time;
            int min = (int) (difference / ONE_MINUTE);
            int sec = (int) ((difference / ONE_SECOND) - (min * SECONDS));
            return String.format(Locale.getDefault(), "%02d", min) + ":" + String.format(Locale.getDefault(), "%02d", sec);
        } else
            return context.getString(R.string.default_live_match_time);
    }
}
