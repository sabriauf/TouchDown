package lk.rc07.ten_years.touchdown.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Sabri on 12/15/2016. data model for Player
 */

public class Player implements Parcelable{

    private int idPlayer;
    private String name;
    private String img_url;
//    private int teamId;
    private double weight;
    private double height;
    private Date birthDay;
//    private int colors;


    public Player() {
    }

    private Player(Parcel in) {
        idPlayer = in.readInt();
        name = in.readString();
        img_url = in.readString();
//        teamId = in.readInt();
        weight = in.readDouble();
        height = in.readDouble();
//        colors = in.readInt();
        birthDay = new Date(in.readLong());
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

//    public int getTeamId() {
//        return teamId;
//    }
//
//    public void setTeamId(int teamId) {
//        this.teamId = teamId;
//    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

//    public int getColors() {
//        return colors;
//    }
//
//    public void setColors(int colors) {
//        this.colors = colors;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(idPlayer);
        parcel.writeString(name);
        parcel.writeString(img_url);
//        parcel.writeInt(teamId);
        parcel.writeDouble(weight);
        parcel.writeDouble(height);
//        parcel.writeInt(colors);
        parcel.writeLong(birthDay.getTime());
    }
}
