package lk.rc07.ten_years.touchdown.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sabri on 2/12/2017. Group Model
 */

public class Group {

    @SerializedName("idgroup")
    private int groupId;
    private String groupName;
    private String leagueName;
    private String roundName;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
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
}
