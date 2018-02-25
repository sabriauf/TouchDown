package lk.rc07.ten_years.touchdown.models;

/**
 * Created by Sabri on 2/25/2018. model for Player and team
 */

public class PlayerTeam {

    private int teamId;
    private int playerId;
    private int year;
    private int colors;

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getColors() {
        return colors;
    }

    public void setColors(int colors) {
        this.colors = colors;
    }
}
