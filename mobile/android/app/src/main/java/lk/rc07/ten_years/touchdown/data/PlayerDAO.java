package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.rc07.ten_years.touchdown.models.AdapterPlayer;
import lk.rc07.ten_years.touchdown.models.Player;

/**
 * Created by Sabri on 12/24/2016. Player data access model
 */

public class PlayerDAO extends DBManager {

    private static boolean checkIfPlayerAvailable(int playerId) {
        String query = "";
        query = query.concat("SELECT * FROM ");
        query = query.concat(DBContact.PlayerTable.TABLE_NAME);
        query = query.concat(" WHERE ");
        query = query.concat(DBContact.PlayerTable.COLUMN_ID);
        query = query.concat("='");
        query = query.concat(String.valueOf(playerId));
        query = query.concat("'");

        Cursor cursor = mDatabase.rawQuery(query, null);
        boolean isAvailable = cursor.moveToFirst();
        cursor.close();
        return isAvailable;
    }

    public static boolean addPlayer(Player player) {

        ContentValues values = new ContentValues();

        boolean isPlayerAlreadyExist = checkIfPlayerAvailable(player.getIdPlayer());

        values.put(DBContact.PlayerTable.COLUMN_NAME, player.getName());
        values.put(DBContact.PlayerTable.COLUMN_IMG, player.getImg_url());
        values.put(DBContact.PlayerTable.COLUMN_TEAM, player.getTeamId());
        values.put(DBContact.PlayerTable.COLUMN_HEIGHT, player.getHeight());
        values.put(DBContact.PlayerTable.COLUMN_WEIGHT, player.getWeight());
        values.put(DBContact.PlayerTable.COLUMN_BIRTH, player.getBirthDay().getTime());
        values.put(DBContact.PlayerTable.COLUMN_COLORS, player.getColors());

        if (!isPlayerAlreadyExist) {
            values.put(DBContact.PlayerTable.COLUMN_ID, player.getIdPlayer());
            return (mDatabase.insert(DBContact.PlayerTable.TABLE_NAME, null, values) != -1);

        } else {
            String WHERE_CLAUSE = DBContact.PlayerTable.COLUMN_ID + "=?";
            String[] WHERE_ARGS = {String.valueOf(player.getIdPlayer())};
            return mDatabase.update(DBContact.PlayerTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
        }
    }

    public static Player getPlayer(int playerId) {
        Player player = null;

        String WHERE_CLAUSE = DBContact.PlayerTable.COLUMN_ID + "=?";
        String[] WHERE_ARGS = {String.valueOf(playerId)};

        Cursor cursor = mDatabase.query(DBContact.PlayerTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        if (cursor.moveToFirst()) {
            player = cursorToPlayer(cursor);
        }
        cursor.close();
        return player;
    }
    public static List<AdapterPlayer> getPlayerOfTeam(int teamId, int matchId) {
        List<AdapterPlayer> players = new ArrayList<>();

        String WHERE_CLAUSE = DBContact.PlayerTable.COLUMN_TEAM + "=?";
        String[] WHERE_ARGS = {String.valueOf(teamId)};

        Cursor cursor = mDatabase.query(DBContact.PlayerTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        while (cursor.moveToNext()) {
            AdapterPlayer player = new AdapterPlayer();
            player.setPlayer(cursorToPlayer(cursor));
            player.setPosition(PlayerPositionDAO.getPosition(player.getPlayer().getIdPlayer(), matchId));
            players.add(player);
        }
        cursor.close();
        return players;
    }

    public static List<Player> getAllPlayer() {
        List<Player> players = new ArrayList<>();

        Cursor cursor = mDatabase.query(DBContact.PlayerTable.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            players.add(cursorToPlayer(cursor));
        }
        cursor.close();
        return players;
    }

    public static AdapterPlayer getAdapterPlayer(int playerId, int matchId) {
        AdapterPlayer aPlayer = new AdapterPlayer();

        aPlayer.setPlayer(PlayerDAO.getPlayer(playerId));
        aPlayer.setPosition(PlayerPositionDAO.getPosition(playerId, matchId));

        return aPlayer;
    }

    private static Player cursorToPlayer(Cursor cursor) {
        Player player = new Player();
        int id = cursor.getInt(cursor.getColumnIndex(DBContact.PlayerTable.COLUMN_ID));
        player.setIdPlayer(id);
        player.setTeamId(cursor.getInt(cursor.getColumnIndex(DBContact.PlayerTable.COLUMN_TEAM)));
        player.setImg_url(cursor.getString(cursor.getColumnIndex(DBContact.PlayerTable.COLUMN_IMG)));
        player.setName(cursor.getString(cursor.getColumnIndex(DBContact.PlayerTable.COLUMN_NAME)));
        player.setHeight(cursor.getDouble(cursor.getColumnIndex(DBContact.PlayerTable.COLUMN_HEIGHT)));
        player.setWeight(cursor.getDouble(cursor.getColumnIndex(DBContact.PlayerTable.COLUMN_WEIGHT)));
        player.setBirthDay(new Date(cursor.getLong(cursor.getColumnIndex(DBContact.PlayerTable.COLUMN_BIRTH))));
        player.setColors(cursor.getInt(cursor.getColumnIndex(DBContact.PlayerTable.COLUMN_COLORS)));
        return player;
    }
}
