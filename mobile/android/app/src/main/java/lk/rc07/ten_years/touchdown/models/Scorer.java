package lk.rc07.ten_years.touchdown.models;

import java.util.List;

/**
 * Created by Sabri on 3/25/2017. Players who has scored with score objects
 */

public class Scorer {

    private Player player;
    private List<Score> scores;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }
}
