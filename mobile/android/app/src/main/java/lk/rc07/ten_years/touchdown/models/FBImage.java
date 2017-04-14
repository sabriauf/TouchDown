package lk.rc07.ten_years.touchdown.models;

/**
 * Created by Sabri on 4/13/2017. facebook image object
 */

public class FBImage {

    private String created_time;
    private String id;
    private int width;
    private int height;
    private int match;
    private String name;

    public String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
