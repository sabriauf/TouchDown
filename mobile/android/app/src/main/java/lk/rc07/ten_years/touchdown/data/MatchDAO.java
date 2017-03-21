package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
        values.put(DBContact.MatchTable.COLUMN_GROUP, match.getGroup());
        values.put(DBContact.MatchTable.COLUMN_VENUE, match.getVenue());
        values.put(DBContact.MatchTable.COLUMN_STATUS, match.getStatus().toString());
        values.put(DBContact.MatchTable.COLUMN_DATE, match.getMatchDate());
        values.put(DBContact.MatchTable.COLUMN_RESULT, match.getResult());
        values.put(DBContact.MatchTable.COLUMN_LAST, match.getLastMatch());
        values.put(DBContact.MatchTable.COLUMN_LATITUDE, match.getLatitude());
        values.put(DBContact.MatchTable.COLUMN_LONGITUDE, match.getLongitude());

        if (!isMatchAlreadyExist) {
            values.put(DBContact.MatchTable.COLUMN_ID, match.getIdmatch());
            return (mDatabase.insert(DBContact.MatchTable.TABLE_NAME, null, values) != -1);

        } else {
            String WHERE_CLAUSE = DBContact.MatchTable.COLUMN_ID + "=?";
            String[] WHERE_ARGS = {String.valueOf(match.getIdmatch())};
            return mDatabase.update(DBContact.MatchTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
        }
    }

    public static Match getMatchForId(int id) {
        Match match = null;

        String WHERE_CLAUSE = DBContact.MatchTable.COLUMN_ID + "=?";
        String[] WHERE_ARGS = {String.valueOf(id)};

        Cursor cursor = mDatabase.query(DBContact.MatchTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        while (cursor.moveToNext()) {
            match = cursorToMatch(cursor);
        }
        cursor.close();
        return match;
    }

    private static List<Match> getMatchesOnStatus(Match.Status[] status) {
        List<Match> matches = new ArrayList<>();

        String WHERE_CLAUSE = "";
        for (int i = 0; i < status.length; i++) {
            WHERE_CLAUSE += DBContact.MatchTable.COLUMN_STATUS + "=?";
            if (i != status.length - 1)
                WHERE_CLAUSE += " or ";
        }
        String[] WHERE_ARGS = Arrays.toString(status).split("[\\[\\]]")[1].split(", ");

        Cursor cursor = mDatabase.query(DBContact.MatchTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        while (cursor.moveToNext()) {
            matches.add(cursorToMatch(cursor));
        }
        cursor.close();
        return matches;
    }

    private static Match getCalendarMatch() {

        Calendar from = Calendar.getInstance();
        from.add(Calendar.DAY_OF_MONTH, -5);

        Calendar to = Calendar.getInstance();
        to.add(Calendar.DAY_OF_MONTH, 3);

        String query = "";
        query = query.concat("SELECT * FROM ");
        query = query.concat(DBContact.MatchTable.TABLE_NAME);
        query = query.concat(" WHERE ");
        query = query.concat(DBContact.MatchTable.COLUMN_DATE);
        query = query.concat(">'");
        query = query.concat(String.valueOf(from.getTimeInMillis()));
        query = query.concat("' and ");
        query = query.concat(DBContact.MatchTable.COLUMN_DATE);
        query = query.concat("<'");
        query = query.concat(String.valueOf(to.getTimeInMillis()));
        query = query.concat("'");

        Match match = null;
        Cursor cursor = mDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            match = cursorToMatch(cursor);
        }
        cursor.close();
        return match;
    }

    public static Match getDisplayMatch() {
        Match.Status[] currentMatchStatus = new Match.Status[]{Match.Status.FIRST_HALF, Match.Status.SECOND_HALF, Match.Status.HALF_TIME};
        List<Match> matches = getMatchesOnStatus(currentMatchStatus);
        if (matches.size() > 0)
            return matches.get(0);
        else
            return getCalendarMatch();
    }

    public static List<Match> getAllMatches() {
        List<Match> matches = new ArrayList<>();

        String orderBy = DBContact.MatchTable.COLUMN_DATE + " ASC";
        Cursor cursor = mDatabase.query(DBContact.MatchTable.TABLE_NAME, null, null, null, null, null, orderBy);
        while (cursor.moveToNext()) {
            matches.add(cursorToMatch(cursor));
        }
        cursor.close();
        return matches;
    }

    public static boolean updateMatchStatus(int matchId, Match.Status status) {

        ContentValues values = new ContentValues();

        values.put(DBContact.MatchTable.COLUMN_STATUS, String.valueOf(status));

        String WHERE_CLAUSE = DBContact.MatchTable.COLUMN_ID + "=?";
        String[] WHERE_ARGS = {String.valueOf(matchId)};
        return mDatabase.update(DBContact.MatchTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
    }

    static boolean updateMatchResult(int matchId, String result) {

        ContentValues values = new ContentValues();

        values.put(DBContact.MatchTable.COLUMN_RESULT, result);

        String WHERE_CLAUSE = DBContact.MatchTable.COLUMN_ID + "=?";
        String[] WHERE_ARGS = {String.valueOf(matchId)};
        return mDatabase.update(DBContact.MatchTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
    }

    private static Match cursorToMatch(Cursor cursor) {
        Match match = new Match();
        match.setIdmatch(cursor.getInt(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_ID)));
        match.setTeamOne(cursor.getInt(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_TEAM_ONE)));
        match.setTeamTwo(cursor.getInt(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_TEAM_TWO)));
        match.setVenue(cursor.getString(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_VENUE)));
        match.setMatchDate(cursor.getLong(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_DATE)));
        match.setGroup(cursor.getInt(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_GROUP)));
        match.setStatus(Match.Status.valueOf(cursor.getString(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_STATUS))));
        match.setResult(cursor.getString(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_RESULT)));
        match.setLastMatch(cursor.getString(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_LAST)));
        match.setLongitude(cursor.getDouble(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_LONGITUDE)));
        match.setLatitude(cursor.getDouble(cursor.getColumnIndex(DBContact.MatchTable.COLUMN_LATITUDE)));
        return match;
    }
}
