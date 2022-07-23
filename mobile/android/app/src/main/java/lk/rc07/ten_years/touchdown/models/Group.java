package lk.rc07.ten_years.touchdown.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Sabri on 2/12/2017. Group Model
 */
@Entity
public class Group {

    @PrimaryKey
    @ColumnInfo(name = "group_id")
    private int idgroup;
    @ColumnInfo(name = "group_name")
    private String groupName;
    @ColumnInfo(name = "group_league")
    private String leagueName;
    @ColumnInfo(name = "group_round")
    private String roundName;
    @ColumnInfo(name = "group_year")
    private int year;

    public int getIdgroup() {
        return idgroup;
    }

    public void setIdgroup(int idgroup) {
        this.idgroup = idgroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
