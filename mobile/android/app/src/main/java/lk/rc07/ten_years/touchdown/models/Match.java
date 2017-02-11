package lk.rc07.ten_years.touchdown.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sabri on 12/15/2016. data model of Match
 */

public class Match {

    private int idmatch;
    private int teamOne;
    private int teamTwo;
    private String venue;
    @SerializedName("date")
    private long matchDate;
    private Status status;
    private String result;
    private String league;
    private String round;
    private double longitude;
    private double latitude;
    private String lastMatch;

    public enum Status {
        PENDING, PROGRESS, DONE, CALLED_OFF, CANCELED, HALF_TIME, FIRST_HALF, SECOND_HALF
    }

    public int getIdmatch() {
        return idmatch;
    }

    public void setIdmatch(int idmatch) {
        this.idmatch = idmatch;
    }

    public int getTeamOne() {
        return teamOne;
    }

    public void setTeamOne(int teamOne) {
        this.teamOne = teamOne;
    }

    public int getTeamTwo() {
        return teamTwo;
    }

    public void setTeamTwo(int teamTwo) {
        this.teamTwo = teamTwo;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public long getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(long matchDate) {
        this.matchDate = matchDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLastMatch() {
        return lastMatch;
    }

    public void setLastMatch(String lastMatch) {
        this.lastMatch = lastMatch;
    }
}
