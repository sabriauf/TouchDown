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
    private double longitude;
    private double latitude;
    private String lastMatch;
    private int group;

    public enum Status {
        PENDING, DONE, FULL_TIME, CALLED_OFF, CANCELED, HALF_TIME, FIRST_HALF, SECOND_HALF;

        public String toStringValue() {
            switch (this) {
                case PENDING:
                case CANCELED:
                    return this.name();
                case DONE:
                case FULL_TIME:
                    return "Full Time";
                case CALLED_OFF:
                    return "Called Off";
                case HALF_TIME:
                    return "Half Time";
                case FIRST_HALF:
                    return "First Half";
                case SECOND_HALF:
                    return "Second Half";
            }
            return "";
        }
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

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
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
