package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import lk.rc07.ten_years.touchdown.models.PlayerPosition;
import lk.rc07.ten_years.touchdown.models.Position;

/**
 * Created by Sabri on 12/24/2016. Player Position data access model
 */

public class PlayerPositionDAO extends DBManager {

    private static int getPlayerPosId(int playerId, int matchId) {

        int playerPosId = -1;

        String query = "";
        query = query.concat("SELECT * FROM ");
        query = query.concat(DBContact.PlayerPositionTable.TABLE_NAME);
        query = query.concat(" WHERE ");
        query = query.concat(DBContact.PlayerPositionTable.COLUMN_PLAYER_ID);
        query = query.concat("='");
        query = query.concat(String.valueOf(playerId));
        if(matchId != 0) {
            query = query.concat("' and ");
            query = query.concat(DBContact.PlayerPositionTable.COLUMN_MATCH_ID);
            query = query.concat("='");
            query = query.concat(String.valueOf(matchId));
        }
        query = query.concat("'");

        Cursor cursor = mDatabase.rawQuery(query, null);
        if(cursor.moveToFirst())
            playerPosId = cursor.getInt(cursor.getColumnIndex(DBContact.PlayerPositionTable.COLUMN_POS_ID));
        cursor.close();
        return playerPosId;
    }

    public static boolean addPlayerPosition(PlayerPosition playerPos) {

        ContentValues values = new ContentValues();

        int playerPosId = getPlayerPosId(playerPos.getIdPlayer(), playerPos.getIdMatch());

        if (playerPosId == -1) {
            values.put(DBContact.PlayerPositionTable.COLUMN_MATCH_ID, playerPos.getIdMatch());
            values.put(DBContact.PlayerPositionTable.COLUMN_PLAYER_ID, playerPos.getIdPlayer());
            values.put(DBContact.PlayerPositionTable.COLUMN_POS_ID, playerPos.getIdPosition());
            return (mDatabase.insert(DBContact.PlayerPositionTable.TABLE_NAME, null, values) != -1);
        } else {
            values.put(DBContact.PlayerPositionTable.COLUMN_POS_ID, playerPos.getIdPosition());
            String WHERE_CLAUSE = DBContact.PlayerPositionTable.COLUMN_PLAYER_ID + "=? and "
                    + DBContact.PlayerPositionTable.COLUMN_MATCH_ID + " =?";
            String[] WHERE_ARGS = {String.valueOf(playerPos.getIdPlayer()), String.valueOf(playerPos.getIdMatch())};
            return mDatabase.update(DBContact.PlayerPositionTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
        }
    }

    static Position getPosition(int playerId, int matchId) {
        int posId = getPlayerPosId(playerId, matchId);
        if(posId == -1)
            posId = getPlayerPosId(playerId, 0);

        return PositionDAO.getPosition(posId);
    }
}
