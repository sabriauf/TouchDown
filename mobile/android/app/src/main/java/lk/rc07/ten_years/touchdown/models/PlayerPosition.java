package lk.rc07.ten_years.touchdown.models;

import androidx.room.Entity;

/**
 * Created by Sabri on 12/24/2016. define players position model
 */
@Entity(primaryKeys = {"idPlayer", "idMatch"})
public class PlayerPosition {

    private int idPlayer;
    private int idPosition;
    private int idMatch;

    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public int getIdPosition() {
        return idPosition;
    }

    public void setIdPosition(int idPosition) {
        this.idPosition = idPosition;
    }

    public int getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(int idMatch) {
        this.idMatch = idMatch;
    }
}
