package lk.rc07.ten_years.touchdown.models;

import android.content.Context;

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

    private int idscore;
    private int matchid;
    private int teamId;
    private int score;
    private String details;
    private int player;
    private double time;
    private Action action;

    private static Map<String, ScoreListener> scoreListeners;

    enum Action {
        TRY, CONVERSION, DROP_GOAL, YELLOW_CARD, RED_CARD, PENALTY, KNOCK_ON, SCRUM
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

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
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

    public static void notifyOnNewScore(Context context, Score score) {
        for (ScoreListener listener : scoreListeners.values()) {
            if (listener != null)
                listener.OnNewScoreUpdate(score);
        }

        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(context));
        dbManager.openDatabase();
        ScoreDAO.addScore(score);
        dbManager.closeDatabase();
    }
}
