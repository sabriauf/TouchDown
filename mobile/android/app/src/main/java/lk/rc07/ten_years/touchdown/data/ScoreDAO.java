package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.models.Scorer;

/**
 * Created by Sabri on 12/24/2016. Score data access model
 */

public class ScoreDAO extends DBManager {

    private static boolean checkIfScoreAvailable(int scoreId) {

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_ID + "=?";
        String[] WHERE_ARGS = {String.valueOf(scoreId)};

        Cursor cursor = mDatabase.query(DBContact.ScoreTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);

        boolean isAvailable = cursor.moveToFirst();
        cursor.close();
        return isAvailable;
    }

    public static boolean addScore(Score score) {

        if (score.getAction() == null)
            return false;

        ContentValues values = new ContentValues();

        boolean isScoreAlreadyExist = checkIfScoreAvailable(score.getIdscore());

        values.put(DBContact.ScoreTable.COLUMN_MATCH, score.getMatchid());
        values.put(DBContact.ScoreTable.COLUMN_DETAILS, score.getDetails());
        values.put(DBContact.ScoreTable.COLUMN_ACTION, String.valueOf(score.getAction()));
        values.put(DBContact.ScoreTable.COLUMN_PLAYER, score.getPlayer());
        values.put(DBContact.ScoreTable.COLUMN_SCORE, score.getScore());
        values.put(DBContact.ScoreTable.COLUMN_TEAM, score.getTeamId());
        values.put(DBContact.ScoreTable.COLUMN_TIME, score.getTime());

        if (score.getActionType() == Score.WHAT_ACTION_TIME)
            updateMatchTime(score);

        if (!isScoreAlreadyExist) {
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
        Match.Status status;
        if (score.getAction() == Score.Action.START)
            status = Match.Status.FIRST_HALF;
        else if (score.getAction() == Score.Action.SECOND_HALF)
            status = Match.Status.SECOND_HALF;
        else if (score.getAction() == Score.Action.HALF_TIME)
            status = Match.Status.HALF_TIME;
        else if (score.getAction() == Score.Action.GAME_PAUSE)
            status = Match.Status.GAME_PAUSE;
        else if (score.getAction() == Score.Action.GAME_RESTART) {
            ArrayList<Score> actions = ScoreDAO.getActionScore(score.getMatchid(), Score.Action.SECOND_HALF);
            if (actions.size() == 0)
                status = Match.Status.FIRST_HALF;
            else
                status = Match.Status.SECOND_HALF;
        } else {
            status = Match.Status.FULL_TIME;
            MatchDAO.updateMatchResult(score.getMatchid(), score.getDetails());
        }

        MatchDAO.updateMatchStatus(score.getMatchid(), status);

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

    public static int getTotalScore(int matchId, int teamId) {
        int totalScore = 0;

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_MATCH + "=? and " + DBContact.ScoreTable.COLUMN_TEAM + " =?";
        String[] WHERE_ARGS = {String.valueOf(matchId), String.valueOf(teamId)};
        String orderBy = DBContact.ScoreTable.COLUMN_TIME + " ASC";

        Cursor cursor = mDatabase.query(DBContact.ScoreTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, orderBy);
        while (cursor.moveToNext()) {
            totalScore += cursor.getInt(cursor.getColumnIndex(DBContact.ScoreTable.COLUMN_SCORE));
        }
        cursor.close();
        return totalScore;
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

    public static ArrayList<Score> getAllGameTimes(int matchId) {
        ArrayList<Score> scores = new ArrayList<>();

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_MATCH + "=? and (" +
                DBContact.ScoreTable.COLUMN_ACTION + "=? OR " +
                DBContact.ScoreTable.COLUMN_ACTION + "=? OR " +
                DBContact.ScoreTable.COLUMN_ACTION + "=? OR " +
                DBContact.ScoreTable.COLUMN_ACTION + "=? OR " +
                DBContact.ScoreTable.COLUMN_ACTION + "=? OR " +
                DBContact.ScoreTable.COLUMN_ACTION + "=? )";
        String[] WHERE_ARGS = {String.valueOf(matchId), String.valueOf(Score.Action.START),
                String.valueOf(Score.Action.HALF_TIME), String.valueOf(Score.Action.SECOND_HALF),
                String.valueOf(Score.Action.FULL_TIME), String.valueOf(Score.Action.GAME_PAUSE),
                String.valueOf(Score.Action.GAME_RESTART)};
        String orderBy = DBContact.ScoreTable.COLUMN_TIME + " ASC";

        Cursor cursor = mDatabase.query(DBContact.ScoreTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, orderBy);
        while (cursor.moveToNext()) {
            scores.add(cursorToScore(cursor));
        }
        cursor.close();
        return scores;
    }

    public static ArrayList<Score> getActionScore(int matchId, int teamId, Score.Action action) {
        ArrayList<Score> scores = new ArrayList<>();

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_MATCH + "=? and " +
                DBContact.ScoreTable.COLUMN_ACTION + "=? and " + DBContact.ScoreTable.COLUMN_TEAM + "=?";
        String[] WHERE_ARGS = {String.valueOf(matchId), String.valueOf(action), String.valueOf(teamId)};

        Cursor cursor = mDatabase.query(DBContact.ScoreTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        while (cursor.moveToNext()) {
            scores.add(cursorToScore(cursor));
        }
        cursor.close();
        return scores;
    }

    public static ArrayList<Scorer> getScorers(int matchId, int teamId) {
        ArrayList<Scorer> scorers = new ArrayList<>();

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_MATCH + "=? and " + DBContact.ScoreTable.COLUMN_TEAM + "=?";
        String[] WHERE_ARGS = {String.valueOf(matchId), String.valueOf(teamId)};

        Cursor cursor = mDatabase.query(DBContact.ScoreTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        while (cursor.moveToNext()) {
            Score score = cursorToScore(cursor);
            if (score.getPlayer() != 0 && score.getScore() > 0) {
                addScoreToScorers(scorers, score);
            }
        }
        cursor.close();
        return scorers;
    }

    public static ArrayList<Scorer> getPenalizedPlayers(int matchId, int teamId) {
        ArrayList<Scorer> scorers = new ArrayList<>();

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_MATCH + "=? and " + DBContact.ScoreTable.COLUMN_TEAM + "=?";
        String[] WHERE_ARGS = {String.valueOf(matchId), String.valueOf(teamId)};

        Cursor cursor = mDatabase.query(DBContact.ScoreTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        while (cursor.moveToNext()) {
            Score score = cursorToScore(cursor);
            Score.Action action = score.getAction();
            if (score.getPlayer() != 0 && (action == Score.Action.RED_CARD || action == Score.Action.YELLOW_CARD)) {
                addScoreToScorers(scorers, score);
            }
        }
        cursor.close();
        return scorers;
    }

    private static void addScoreToScorers(ArrayList<Scorer> scorers, Score score) {
        for (Scorer scorer : scorers) {
            if (scorer != null && scorer.getPlayer() != null) {
                if (score.getPlayer() == scorer.getPlayer().getIdPlayer()) {
                    scorer.getScores().add(score);
                    return;
                }
            }
        }
        Scorer scorer = new Scorer();
        scorer.setPlayer(PlayerDAO.getPlayer(score.getPlayer()));
        List<Score> scores = new ArrayList<>();
        scores.add(score);
        scorer.setScores(scores);

        scorers.add(scorer);
    }

    public static int getPlayerAction(int playerId, Score.Action action, String year) {
        int count;

        String QUERY = "SELECT * FROM %s WHERE %s";
        String TABLE = "%s G, %s S, %s M";
        String WHERE = " S.%s = M.%s AND M.%s = G.%s AND S.%s = ? AND S.%s = ? AND G.%s = ?";

        String tables = String.format(Locale.getDefault(), TABLE, DBContact.GroupTable.TABLE_NAME, DBContact.ScoreTable.TABLE_NAME,
                DBContact.MatchTable.TABLE_NAME);
        String where_clause = String.format(Locale.getDefault(), WHERE, DBContact.ScoreTable.COLUMN_MATCH,
                DBContact.MatchTable.COLUMN_ID, DBContact.MatchTable.COLUMN_GROUP, DBContact.GroupTable.COLUMN_ID,
                DBContact.ScoreTable.COLUMN_PLAYER, DBContact.ScoreTable.COLUMN_ACTION, DBContact.GroupTable.COLUMN_YEAR);
        String[] WHERE_ARGS = {String.valueOf(playerId), String.valueOf(action), year};

        Cursor cursor = mDatabase.rawQuery(String.format(Locale.getDefault(), QUERY, tables, where_clause), WHERE_ARGS);
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static boolean deleteScore(int id) {

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_ID + "=? ";
        String[] WHERE_ARGS = {String.valueOf(id)};

        return mDatabase.delete(DBContact.ScoreTable.TABLE_NAME, WHERE_CLAUSE, WHERE_ARGS) == 1;
    }

    public static boolean deleteScore(long time) {

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_TIME + "> ? ";
        String[] WHERE_ARGS = {String.valueOf(time)};

        return mDatabase.delete(DBContact.ScoreTable.TABLE_NAME, WHERE_CLAUSE, WHERE_ARGS) == 1;
    }

    public static boolean deleteAllScores(int id) {

        String WHERE_CLAUSE = DBContact.ScoreTable.COLUMN_MATCH + "=? ";
        String[] WHERE_ARGS = {String.valueOf(id)};

        return mDatabase.delete(DBContact.ScoreTable.TABLE_NAME, WHERE_CLAUSE, WHERE_ARGS) == 1;
    }

    public static List<Score> getAllScores() {
        List<Score> scores = new ArrayList<>();

        Cursor cursor = mDatabase.query(DBContact.ScoreTable.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            scores.add(cursorToScore(cursor));
        }
        cursor.close();
        return scores;
    }

    public static boolean deleteAll() {
        return mDatabase.delete(DBContact.ScoreTable.TABLE_NAME, null, null) == 1;
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
