package lk.rc07.ten_years.touchdown.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Sabri on 12/13/2016. data model for Score
 */
@Entity
public class Score {

    //constant
    public static final int WHAT_ACTION_SCORE = 1;
    public static final int WHAT_ACTION_EVENT = 2;
    public static final int WHAT_ACTION_TIME = 3;

    @PrimaryKey
    private int idscore;
    private int matchid;
    private int teamId;
    private int score;
    private String details;
    private int player;
    private long time;
    private Action action;

    public enum Action {
        START, HALF_TIME, SECOND_HALF, FULL_TIME,
        TRY, CONVERSION, DROP_GOAL, PENALTY_KICK, LINE_OUT,
        YELLOW_CARD, RED_CARD, PENALTY, KNOCK_ON, SCRUM, MESSAGE, GAME_PAUSE, GAME_RESTART;
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

    public String getActionString() {
        switch (action) {
            case FULL_TIME:
                return "Full Time";
            case HALF_TIME:
                return "Half Time";
            case START:
                return "Start";
            case SECOND_HALF:
                return "Second Half";
            case KNOCK_ON:
                return "Knock on";
            case RED_CARD:
                return "Red Card";
            case YELLOW_CARD:
                return "Yellow Card";
            case PENALTY:
            case SCRUM:
            case MESSAGE:
                return action.toString();
            case TRY:
                return "Try";
            case CONVERSION:
                return "Conversion";
            case DROP_GOAL:
                return "Drop Goal";
            case PENALTY_KICK:
                return "Penalty Kick";
            case LINE_OUT:
                return "Line-out";
            case GAME_PAUSE:
                return "Stop play";
            case GAME_RESTART:
                return "Restart";
        }
        return "";
    }

    public int getActionType() {
        switch (action) {
            case FULL_TIME:
            case HALF_TIME:
            case START:
            case SECOND_HALF:
            case GAME_PAUSE:
            case GAME_RESTART:
                return WHAT_ACTION_TIME;
            case KNOCK_ON:
            case PENALTY:
            case RED_CARD:
            case SCRUM:
            case YELLOW_CARD:
            case LINE_OUT:
            case MESSAGE:
                return WHAT_ACTION_EVENT;
            case TRY:
            case CONVERSION:
            case DROP_GOAL:
            case PENALTY_KICK:
                return WHAT_ACTION_SCORE;
        }
        return WHAT_ACTION_SCORE;
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
}
