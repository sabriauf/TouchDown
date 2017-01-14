package lk.rc07.ten_years.touchdown.models;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.LinkedHashMap;
import java.util.Map;

import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.utils.ScoreListener;

/**
 * Created by Sabri on 12/13/2016. data model for Score
 */

public class Score {

    //constant
    public static final int WHAT_NEW_SCORE = 1001;
    public static final int WHAT_UPDATE_SCORE = 1002;
    public static final int WHAT_REMOVE_SCORE = 1003;

    private int idscore;
    private int matchid;
    private int teamId;
    private int score;
    private String details;
    private int player;
    private long time;
    private Action action;

    private static Map<String, ScoreListener> scoreListeners;

    public enum Action {
        START, HALF_TIME, FULL_TIME, TRY, CONVERSION, DROP_GOAL, YELLOW_CARD, RED_CARD, PENALTY, KNOCK_ON, SCRUM
    }

    public int getIdscore() {
        return idscore;
    }

    public void setIdscore(int idscore) {
        this.idscore = idscore;
    }

    public int getMatchid() {
        return matchid;
    }

    public void setMatchid(int matchid) {
        this.matchid = matchid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setAction(String action) {
        this.action = Action.valueOf(action);
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public static void setScoreListener(ScoreListener listener, String key) {
        if (scoreListeners == null)
            scoreListeners = new LinkedHashMap<>();

        scoreListeners.put(key, listener);
    }

    private static void notifyOnNewScoreUpdate(Score score) {
        for (ScoreListener listener : scoreListeners.values()) {
            if (listener != null)
                listener.OnNewScoreUpdate(score);
        }
    }

    private static void notifyOnScoreUpdate(Score score) {
        for (ScoreListener listener : scoreListeners.values()) {
            if (listener != null)
                listener.OnNewScoreUpdate(score);
        }
    }

    private static void notifyOnScoreRemove(Score score) {
        for (ScoreListener listener : scoreListeners.values()) {
            if (listener != null)
                listener.OnNewScoreUpdate(score);
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
            }
            return true;
        }
    });
}
