package lk.rc07.ten_years.touchdown.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Sabri on 12/15/2016. data model for Position
 */
@Entity
public class Position {

    @PrimaryKey
    private int idPosition;
    private int posNo;
    private String posName;

    public int getIdPosition() {
        return idPosition;
    }

    public void setIdPosition(int idPosition) {
        this.idPosition = idPosition;
    }

    public int getPosNo() {
        return posNo;
    }

    public void setPosNo(int posNo) {
        this.posNo = posNo;
    }

    public String getPosName() {
        return posName;
    }

    public void setPosName(String posName) {
        this.posName = posName;
    }
}
