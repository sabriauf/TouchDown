package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Score;

/**
 * Created by Sabri on 12/24/2016. Score data access model
 */

public class ScoreDAO extends DBManager {

    private static boolean checkIfScoreAvailable(int scoreId) {
        String query = "";
        query = query.concat("SELECT * FROM ");
        query = query.concat(DBContact.ScoreTable.TABLE_NAME);
        query = query.concat(" WHERE ");
        query = query.concat(DBContact.ScoreTable.COLUMN_ID);
        query = query.concat("='");
        query = query.concat(String.valueOf(scoreId));
        query = query.concat("'");

        Cursor cursor = mDatabase.rawQuery(query, null);
        boolean isAvailable = cursor.moveToFirst();
        cursor.close();
        return isAvailable;
    }

    public static boolean addScore(Score score) {

        ContentValues values = new ContentValues();

        boolean isRoomAlreadyExist = checkIfScoreAvailable(score.getIdscore());

        values.put(DBContact.ScoreTable.COLUMN_MATCH, score.getMatchid());
        values.put(DBContact.ScoreTable.COLUMN_DETAILS, score.getDetails());
        values.put(DBContact.ScoreTable.COLUMN_ACTION, String.valueOf(score.getAction()));
        values.put(DBContact.ScoreTable.COLUMN_PLAYER, score.getPlayer());
        values.put(DBContact.ScoreTable.COLUMN_SCORE, score.getScore());
        values.put(DBContact.ScoreTable.COLUMN_TEAM, score.getTeamId());
        values.put(DBContact.ScoreTable.COLUMN_TIME, score.getTime());

        if (score.getActionType() == Score.WHAT_ACTION_TIME)
            updateMatchTime(score);

        if (!isRoomAlreadyExist) {
            values.put(DBContact.ScoreTable.COLUMN_ID, score.getIdscore());
            mDatabase.insert(DBContact.ScoreTable.TABLE_NAME, null, values);
            return true;

        } else {
            String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_ID + "=?";
            String[] WHERE_ARGS = {String.valueOf(score.getIdscore())};
            mDatabase.update(DBContact.ScoreTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS);
            return false;
        }
    }

    private static void updateMatchTime(Score score) {
        if (score.getAction() == Score.Action.START)
            MatchDAO.updateMatchStatus(score.getMatchid(), Match.Status.FIRST_HALF);
        else if(score.getAction() == Score.Action.SECOND_HALF)
            MatchDAO.updateMatchStatus(score.getMatchid(), Match.Status.SECOND_HALF);
        else if (score.getAction() == Score.Action.HALF_TIME)
            MatchDAO.updateMatchStatus(score.getMatchid(), Match.Status.HALF_TIME);
        else
            MatchDAO.updateMatchStatus(score.getMatchid(), Match.Status.DONE);
    }

    public static ArrayList<Score> getScores(int matchId) {
        ArrayList<Score> scores = new ArrayList<>();

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_MATCH + "=?";
        String[] WHERE_ARGS = {String.valueOf(matchId)};
        String orderBy = DBContact.ScoreTable.COLUMN_TIME + " ASC";

        Cursor cursor = mDatabase.query(DBContact.ScoreTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, orderBy);
        while (cursor.moveToNext()) {
            scores.add(cursorToScore(cursor));
        }
        cursor.close();
        return scores;
    }

    public static ArrayList<Score> getActionScore(int matchId, Score.Action action) {
        ArrayList<Score> scores = new ArrayList<>();

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_MATCH + "=? and " +
                DBContact.ScoreTable.COLUMN_ACTION + "=?";
        String[] WHERE_ARGS = {String.valueOf(matchId), String.valueOf(action)};

        Cursor cursor = mDatabase.query(DBContact.ScoreTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        while (cursor.moveToNext()) {
            scores.add(cursorToScore(cursor));
        }
        cursor.close();
        return scores;
    }

    public static boolean deleteScore(int id) {

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_ID + "=? ";
        String[] WHERE_ARGS = {String.valueOf(id)};

        return mDatabase.delete(DBContact.ScoreTable.TABLE_NAME, WHERE_CLAUSE, WHERE_ARGS) == 1;
    }

    public static boolean deleteAllScores(int id) {

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_MATCH + "=? ";
        String[] WHERE_ARGS = {String.valueOf(id)};

        return mDatabase.delete(DBContact.ScoreTable.TABLE_NAME, WHERE_CLAUSE, WHERE_ARGS) == 1;
    }

    private static Score cursorToScore(Cursor cursor) {
        Score score = new Score();
        int id = cursor.getInt(cursor.getColumnIndex(DBContact.ScoreTable.COLUMN_ID));
        score.setIdscore(id);
        score.setScore(cursor.getInt(cursor.getColumnIndex(DBContact.ScoreTable.COLUMN_SCORE)));
        score.setAction(cursor.getString(cursor.getColumnIndex(DBContact.ScoreTable.COLUMN_ACTION)));
        score.setDetails(cursor.getString(cursor.getColumnIndex(DBContact.ScoreTable.COLUMN_DETAILS)));
        score.setMatchid(cursor.getInt(cursor.getColumnIndex(DBContact.ScoreTable.COLUMN_MATCH)));
        score.setPlayer(cursor.getInt(cursor.getColumnIndex(DBContact.ScoreTable.COLUMN_PLAYER)));
        score.setTime(cursor.getLong(cursor.getColumnIndex(DBContact.ScoreTable.COLUMN_TIME)));
        score.setTeamId(cursor.getInt(cursor.getColumnIndex(DBContact.ScoreTable.COLUMN_TEAM)));
        return score;
    }
}
