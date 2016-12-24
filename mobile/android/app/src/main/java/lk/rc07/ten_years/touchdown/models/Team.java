package lk.rc07.ten_years.touchdown.models;

/**
 * Created by Sabri on 12/15/2016. data model for Team
 */

public class Team {

    private int idTeam;
    private String name;
    private String flag_url;
    private String logo_url;
    private String year;

    public int getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(int idTeam) {
        this.idTeam = idTeam;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlag_url() {
        return flag_url;
    }

    public void setFlag_url(String flag_url) {
        this.flag_url = flag_url;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
