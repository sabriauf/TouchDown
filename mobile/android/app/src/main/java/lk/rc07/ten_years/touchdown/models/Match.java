package lk.rc07.ten_years.touchdown.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sabri on 12/15/2016. data model of Match
 */
@Entity
public class Match implements Parcelable {

    @PrimaryKey
    private int idmatch;
    @ColumnInfo(name = "group_year")
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
    @SerializedName("idgroup")
    private int group;
    private String album;

    public Match() {
    }

    protected Match(Parcel in) {
        idmatch = in.readInt();
        teamOne = in.readInt();
        teamTwo = in.readInt();
        venue = in.readString();
        matchDate = in.readLong();
        result = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        lastMatch = in.readString();
        group = in.readInt();
        album = in.readString();
    }

    public static final Creator<Match> CREATOR = new Creator<Match>() {
        @Override
        public Match createFromParcel(Parcel in) {
            return new Match(in);
        }

        @Override
        public Match[] newArray(int size) {
            return new Match[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(idmatch);
        parcel.writeInt(teamOne);
        parcel.writeInt(teamTwo);
        parcel.writeString(venue);
        parcel.writeLong(matchDate);
        parcel.writeString(result);
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
        parcel.writeString(lastMatch);
        parcel.writeInt(group);
        parcel.writeString(album);
    }

    public enum Status {
        PENDING, DONE, FULL_TIME, CALLED_OFF, CANCELED, CANCELLED, HALF_TIME, FIRST_HALF, SECOND_HALF, GAME_PAUSE, TBA;

        public String toStringValue() {
            switch (this) {
                case PENDING:
                case TBA:
                case CANCELED:
                case CANCELLED:
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
                case GAME_PAUSE:
                    return "Stop play";
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
