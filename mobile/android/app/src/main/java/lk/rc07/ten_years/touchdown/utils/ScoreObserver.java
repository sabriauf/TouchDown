package lk.rc07.ten_years.touchdown.utils;

import android.os.Handler;
import android.os.Message;

import java.util.LinkedHashMap;
import java.util.Map;

import lk.rc07.ten_years.touchdown.models.Score;

/**
 * Created by Sabri on 3/1/2017. Initialization of Score listener
 */

public class ScoreObserver {

    public static final int WHAT_NEW_SCORE = 1001;
    public static final int WHAT_UPDATE_SCORE = 1002;
    public static final int WHAT_REMOVE_SCORE = 1003;
    public static final int WHAT_REMOVE_MATCH = 1004;

    private static Map<String, ScoreListener> scoreListeners;

    public static void setScoreListener(ScoreListener listener, String key) {
        if (scoreListeners == null)
            scoreListeners = new LinkedHashMap<>();

        scoreListeners.put(key, listener);
    }

    private static void notifyOnNewScoreUpdate(Score score) {
        if (scoreListeners != null)
            for (ScoreListener listener : scoreListeners.values()) {
                if (listener != null)
                    listener.OnNewScoreUpdate(score);
            }
    }

    private static void notifyOnScoreUpdate(Score score) {
        if (scoreListeners != null)
            for (ScoreListener listener : scoreListeners.values()) {
                if (listener != null)
                    listener.OnScoreUpdate(score);
            }
    }

    private static void notifyOnScoreRemove(Score score) {
        if (scoreListeners != null)
            for (ScoreListener listener : scoreListeners.values()) {
                if (listener != null)
                    listener.OnScoreRemove(score);
            }
    }

    private static void notifyOnMatchRemove() {
        if (scoreListeners != null)
            for (ScoreListener listener : scoreListeners.values()) {
                if (listener != null)
                    listener.OnMatchRemoved();
            }
    }

    public static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            Score score;
            switch (message.what) {
                case WHAT_NEW_SCORE:
                    score = (Score) message.obj;
                    notifyOnNewScoreUpdate(score);
                    break;
                case WHAT_UPDATE_SCORE:
                    score = (Score) message.obj;
                    notifyOnScoreUpdate(score);
                    break;
                case WHAT_REMOVE_SCORE:
                    score = (Score) message.obj;
                    notifyOnScoreRemove(score);
                    break;
                case WHAT_REMOVE_MATCH:
                    notifyOnMatchRemove();
            }
            return true;
        }
    });
}
