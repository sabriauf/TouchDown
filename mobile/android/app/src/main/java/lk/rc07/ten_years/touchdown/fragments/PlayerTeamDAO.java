package lk.rc07.ten_years.touchdown.fragments;

import android.content.ContentValues;
import android.database.Cursor;

import lk.rc07.ten_years.touchdown.data.DBContact;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.models.PlayerTeam;

/**
 * Created by Sabri on 2/25/2018. data access model for Player Team
 */

public class PlayerTeamDAO extends DBManager {

    private static int getPlayerColors(int playerId, int teamId, int year) {

        int playerTeamId = -1;

        String WHERE_CLAUSE = DBContact.PlayerTeamTable.COLUMN_PLAYER_ID + " =? AND "
                + DBContact.PlayerTeamTable.COLUMN_TEAM_ID + " =? AND " + DBContact.PlayerTeamTable.COLUMN_YEAR + " =?";

        String[] WHERE_ARGS = new String[]{String.valueOf(playerId), String.valueOf(teamId), String.valueOf(year)};

        Cursor cursor = mDatabase.query(DBContact.PlayerTeamTable.TABLE_NAME, null, WHERE_CLAUSE
                , WHERE_ARGS, null, null, null);
        if (cursor.moveToFirst())
            playerTeamId = cursor.getInt(cursor.getColumnIndex(DBContact.PlayerTeamTable.COLUMN_COLORS));
        cursor.close();
        return playerTeamId;
    }

    public static boolean addPlayerTeam(PlayerTeam playerTeam) {

        ContentValues values = new ContentValues();

        int playerPos = getPlayerColors(playerTeam.getPlayerId(), playerTeam.getTeamId(), playerTeam.getYear());

        if (playerPos == -1) {
            values.put(DBContact.PlayerTeamTable.COLUMN_TEAM_ID, playerTeam.getTeamId());
            values.put(DBContact.PlayerTeamTable.COLUMN_PLAYER_ID, playerTeam.getPlayerId());
            values.put(DBContact.PlayerTeamTable.COLUMN_COLORS, playerTeam.getColors());
            values.put(DBContact.PlayerTeamTable.COLUMN_YEAR, playerTeam.getYear());
            return (mDatabase.insert(DBContact.PlayerTeamTable.TABLE_NAME, null, values) != -1);
        } else {
            values.put(DBContact.PlayerTeamTable.COLUMN_COLORS, playerTeam.getColors());
            String WHERE_CLAUSE = DBContact.PlayerTeamTable.COLUMN_PLAYER_ID + " =? AND "
                    + DBContact.PlayerTeamTable.COLUMN_TEAM_ID + " =? AND " + DBContact.PlayerTeamTable.COLUMN_YEAR + " =?";
            String[] WHERE_ARGS = {String.valueOf(playerTeam.getPlayerId()), String.valueOf(playerTeam.getTeamId()),
                    String.valueOf(playerTeam.getYear())};
            return mDatabase.update(DBContact.PlayerTeamTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
        }
    }

    public static PlayerTeam getPlayerTeam(int playerId, int teamId, int year) {
        PlayerTeam playerTeam = new PlayerTeam();

        String WHERE_CLAUSE = DBContact.PlayerTeamTable.COLUMN_PLAYER_ID + " =? AND "
                + DBContact.PlayerTeamTable.COLUMN_TEAM_ID + " =? AND " + DBContact.PlayerTeamTable.COLUMN_YEAR + " =?";

        String[] WHERE_ARGS = new String[]{String.valueOf(playerId), String.valueOf(teamId), String.valueOf(year)};

        Cursor cursor = mDatabase.query(DBContact.PlayerTeamTable.TABLE_NAME, null, WHERE_CLAUSE
                , WHERE_ARGS, null, null, null);

        if (cursor.moveToFirst()) {
            playerTeam = getPlayerTeamForCursor(cursor);
        }
        cursor.close();

        return playerTeam;
    }

    private static PlayerTeam getPlayerTeamForCursor(Cursor cursor) {
        PlayerTeam playerTeam = new PlayerTeam();
        playerTeam.setPlayerId(cursor.getInt(cursor.getColumnIndex(DBContact.PlayerTeamTable.COLUMN_PLAYER_ID)));
        playerTeam.setTeamId(cursor.getInt(cursor.getColumnIndex(DBContact.PlayerTeamTable.COLUMN_TEAM_ID)));
        playerTeam.setColors(cursor.getInt(cursor.getColumnIndex(DBContact.PlayerTeamTable.COLUMN_COLORS)));
        playerTeam.setYear(cursor.getInt(cursor.getColumnIndex(DBContact.PlayerTeamTable.COLUMN_YEAR)));
        return playerTeam;
    }

    public static boolean deleteAll() {
        return mDatabase.delete(DBContact.PlayerTeamTable.TABLE_NAME, null, null) == 1;
    }
}
