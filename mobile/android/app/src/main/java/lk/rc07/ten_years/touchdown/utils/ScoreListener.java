package lk.rc07.ten_years.touchdown.utils;

import lk.rc07.ten_years.touchdown.models.Score;

/**
 * Created by Sabri on 12/13/2016. listener class for score changes
 */

public interface ScoreListener {

    void OnNewScoreUpdate(Score score);

    void OnScoreUpdate(Score score);

    void OnScoreRemove(Score score);

    void OnMatchRemoved();
}
