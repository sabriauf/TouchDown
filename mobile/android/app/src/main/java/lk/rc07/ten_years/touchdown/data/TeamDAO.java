package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import lk.rc07.ten_years.touchdown.models.Team;

/**
 * Created by Sabri on 1/14/2017. Team data access model
 */

public class TeamDAO extends DBManager {

    private static boolean checkIfTeamAvailable(int teamId) {
        String query = "";
        query = query.concat("SELECT * FROM ");
        query = query.concat(DBContact.TeamTable.TABLE_NAME);
        query = query.concat(" WHERE ");
        query = query.concat(DBContact.TeamTable.COLUMN_ID);
        query = query.concat("='");
        query = query.concat(String.valueOf(teamId));
        query = query.concat("'");

        Cursor cursor = mDatabase.rawQuery(query, null);
        boolean isAvailable = cursor.moveToFirst();
        cursor.close();
        return isAvailable;
    }

    public static boolean addTeam(Team team) {

        ContentValues values = new ContentValues();

        boolean isTeamAlreadyExist = checkIfTeamAvailable(team.getIdTeam());

        values.put(DBContact.TeamTable.COLUMN_FLAG, team.getFlag_url());
        values.put(DBContact.TeamTable.COLUMN_LOGO, team.getLogo_url());
        values.put(DBContact.TeamTable.COLUMN_NAME, team.getName());

        if (!isTeamAlreadyExist) {
            values.put(DBContact.TeamTable.COLUMN_ID, team.getIdTeam());
            return (mDatabase.insert(DBContact.TeamTable.TABLE_NAME, null, values) != -1);

        } else {
            String WHERE_CLAUSE = DBContact.TeamTable.COLUMN_ID + "=?";
            String[] WHERE_ARGS = {String.valueOf(team.getIdTeam())};
            return mDatabase.update(DBContact.TeamTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
        }
    }

    public static Team getTeam(int id) {
        Team team = null;

        String WHERE_CLAUSE = DBContact.TeamTable.COLUMN_ID + "=?";
        String[] WHERE_ARGS = {String.valueOf(id)};

        Cursor cursor = mDatabase.query(DBContact.TeamTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        while (cursor.moveToNext()) {
            team = cursorToTeam(cursor);
        }
        cursor.close();
        return team;
    }

    private static Team cursorToTeam(Cursor cursor) {
        Team team = new Team();
        team.setIdTeam(cursor.getInt(cursor.getColumnIndex(DBContact.TeamTable.COLUMN_ID)));
        team.setFlag_url(cursor.getString(cursor.getColumnIndex(DBContact.TeamTable.COLUMN_FLAG)));
        team.setLogo_url(cursor.getString(cursor.getColumnIndex(DBContact.TeamTable.COLUMN_LOGO)));
        team.setName(cursor.getString(cursor.getColumnIndex(DBContact.TeamTable.COLUMN_NAME)));
        return team;
    }

    public static boolean deleteAll() {
        return mDatabase.delete(DBContact.TeamTable.TABLE_NAME, null, null) == 1;
    }
}