package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import lk.rc07.ten_years.touchdown.models.Match;

/**
 * Created by Sabri on 12/24/2016. match data access model
 */

public class MatchDAO extends DBManager {

    private static boolean checkIfMatchAvailable(int matchId) {
        String query = "";
        query = query.concat("SELECT * FROM ");
        query = query.concat(DBContact.MatchTable.TABLE_NAME);
        query = query.concat(" WHERE ");
        query = query.concat(DBContact.MatchTable.COLUMN_ID);
        query = query.concat("='");
        query = query.concat(String.valueOf(matchId));
        query = query.concat("'");

        Cursor cursor = mDatabase.rawQuery(query, null);
        boolean isAvailable = cursor.moveToFirst();
        cursor.close();
        return isAvailable;
    }

    public static boolean addMatch(Match match) {

        ContentValues values = new ContentValues();

        boolean isMatchAlreadyExist = checkIfMatchAvailable(match.getIdmatch());

        values.put(DBContact.MatchTable.COLUMN_TEAM_ONE, match.getTeamOne());
        values.put(DBContact.MatchTable.COLUMN_TEAM_TWO, match.getTeamTwo());
        values.put(DBContact.MatchTable.COLUMN_LEAGUE, match.getLeague());
        values.put(DBContact.MatchTable.COLUMN_ROUND, match.getRound());
        values.put(DBContact.MatchTable.COLUMN_VENUE, match.getVenue());
        values.put(DBContact.MatchTable.COLUMN_STATUS, String.valueOf(match.getStatus()));
        values.put(DBContact.MatchTable.COLUMN_DATE, match.getMatchDate());
        values.put(DBContact.MatchTable.COLUMN_RESULT, match.getResult());

        if (!isMatchAlreadyExist) {
            values.put(DBContact.MatchTable.COLUMN_ID, match.getIdmatch());
            return (mDatabase.insert(DBContact.MatchTable.TABLE_NAME, null, values) != -1);

        } else {
            String WHERE_CLAUSE = DBContact.MatchTable.COLUMN_ID + "=?";
            String[] WHERE_ARGS = {String.valueOf(match.getIdmatch())};
            return mDatabase.update(DBContact.MatchTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
        }
    }

    public static List<Match> getMatchesOnStatus(Match.Status status) {
        List<Match> matches = new ArrayList<>();

        String WHERE_CLAUSE = DBContact.MatchTable.COLUMN_STATUS + "=?";
        String[] WHERE_ARGS = {String.valueOf(status)};

        Cursor cursor = mDatabase.query(DBContact.MatchTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        while (cursor.moveToNext()) {
            matches.add(cursorToMatch(cursor));
        }
        cursor.close();
        return matches;
    }

    public static List<Match> getAllMatches() {
        List<Match> matches = new ArrayList<>();

        Cursor cursor = mDatabase.query(DBContact.MatchTable.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            matches.add(cursorToMatch(cursor));
        }
        cursor.close();
        return matches;
    }

    private static Match cursorToMatch(Cursor cursor) {
        Match match = new Match();
        match.setIdmatch(cursor.getInt(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_ID)));
        match.setTeamOne(cursor.getInt(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_TEAM_ONE)));
        match.setTeamTwo(cursor.getInt(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_TEAM_TWO)));
        match.setVenue(cursor.getString(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_VENUE)));
        match.setMatchDate(cursor.getInt(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_DATE)));
        match.setLeague(cursor.getString(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_LEAGUE)));
        match.setRound(cursor.getString(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_ROUND)));
        match.setStatus(Match.Status.valueOf(cursor.getString(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_STATUS))));
        match.setResult(cursor.getString(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_RESULT)));
        return match;
    }
}
