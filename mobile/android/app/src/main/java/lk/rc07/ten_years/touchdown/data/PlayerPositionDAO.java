package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.models.AdapterPlayer;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Player;
import lk.rc07.ten_years.touchdown.models.PlayerPosition;
import lk.rc07.ten_years.touchdown.models.Position;

/**
 * Created by Sabri on 12/24/2016. Player Position data access model
 */

public class PlayerPositionDAO extends DBManager {

    private static int getPlayerPosId(int posId, int matchId) {

        int playerPosId = -1;

        String WHERE_CLAUSE = DBContact.PlayerPositionTable.COLUMN_POS_ID + " =? AND "
                + DBContact.PlayerPositionTable.COLUMN_MATCH_ID + " =?";

        String[] WHERE_ARGS = new String[]{String.valueOf(posId), String.valueOf(matchId)};

        Cursor cursor = mDatabase.query(DBContact.PlayerPositionTable.TABLE_NAME, null, WHERE_CLAUSE
                , WHERE_ARGS, null, null, null);
        if (cursor.moveToFirst())
            playerPosId = cursor.getInt(cursor.getColumnIndex(DBContact.PlayerPositionTable.COLUMN_POS_ID));
        cursor.close();
        return playerPosId;
    }

    public static boolean addPlayerPosition(PlayerPosition playerPos) {

        ContentValues values = new ContentValues();

        int playerPosId = getPlayerPosId(playerPos.getIdPosition(), playerPos.getIdMatch());

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

    public static List<AdapterPlayer> getPlayersForMatch(int teamId) {
        List<AdapterPlayer> players;

        int matchId = getAvailableLastMatch();

        players = getPlayersForLastMatch(teamId, matchId);

        if (players.size() == 0) {
            players = getPlayersForLastMatch(teamId, getLastMatchIdWithPlayers());
        }

        return players;
    }

    private static List<AdapterPlayer> getPlayersForLastMatch(int teamId, int matchId) {

        String QUERY = "SELECT * from %s where %s IN (SELECT DISTINCT %s from %s where %s) order by %s";

        String TABLE_1 = "%s P, %s O";
        String WHERE1 = "O.%s = P.%s AND O.%s = ? AND P.%s";

        String SELECT = "A.%s";
        String TABLE_2 = "%s B inner join %s A";
        String WHERE2 = " A.%s = B.%s AND B.%s = ? AND A.%s = ? ";

        String ORDER_BY = " %s ASC";

        // tables1 = PlayerTable P, PlayerPositionTable O
        String tables1 = String.format(Locale.getDefault(), TABLE_1, DBContact.PlayerTable.TABLE_NAME,
                DBContact.PlayerPositionTable.TABLE_NAME);
        
        // tables2 = PlayerPositionTable B inner join PlayerTeamTable A
        String tables2 = String.format(Locale.getDefault(), TABLE_2, DBContact.PlayerPositionTable.TABLE_NAME,
                DBContact.PlayerTeamTable.TABLE_NAME);

        // select = A.PlayerId
        String select = String.format(Locale.getDefault(), SELECT, DBContact.PlayerTeamTable.COLUMN_PLAYER_ID);

        // where_clause_1 = O.PlayerId = P.Id AND O.MatchId = ? AND P.Id
        String where_clause_1 = String.format(Locale.getDefault(), WHERE1, DBContact.PlayerPositionTable.COLUMN_PLAYER_ID,
                DBContact.PlayerTable.COLUMN_ID, DBContact.PlayerPositionTable.COLUMN_MATCH_ID,
                DBContact.PlayerTable.COLUMN_ID);

        // where_clause_2 = A.PlayerId = B.PlayerId AND B.MatchId = ? AND A.TeamId = ?
        String where_clause_2 = String.format(Locale.getDefault(), WHERE2, DBContact.PlayerTeamTable.COLUMN_PLAYER_ID,
                DBContact.PlayerPositionTable.COLUMN_PLAYER_ID, DBContact.PlayerPositionTable.COLUMN_MATCH_ID,
                DBContact.PlayerTeamTable.COLUMN_TEAM_ID);

        String order = String.format(Locale.getDefault(), ORDER_BY, DBContact.PlayerPositionTable.COLUMN_POS_ID);

        String[] WHERE_ARGS = {String.valueOf(matchId), String.valueOf(matchId), String.valueOf(teamId)};

        /**
            SELECT * from PlayerTable P, PlayerPositionTable O 
            WHERE O.PlayerId = P.Id AND O.MatchId = <matchId> AND P.Id
            IN (SELECT DISTINCT A.PlayerId from PlayerPositionTable B 
                INNER JOIN PlayerTeamTable A 
                WHERE A.PlayerId = B.PlayerId AND B.MatchId = <matchId> AND A.TeamId = <teamId>) 
            ORDER BY PositionId ASC
         */
        String rawQuery = String.format(Locale.getDefault(), QUERY, tables1, where_clause_1, select, tables2, where_clause_2, order);

        Cursor cursor = mDatabase.rawQuery(rawQuery, WHERE_ARGS);
        List<AdapterPlayer> players = getAdapterPlayers(cursor);

        cursor.close();

        return players;
    }

    private static List<AdapterPlayer> getAdapterPlayers(Cursor cursor) {
        List<AdapterPlayer> players = new ArrayList<>();
        while (cursor.moveToNext()) {
            int posId = cursor.getInt(cursor.getColumnIndex(DBContact.PlayerPositionTable.COLUMN_POS_ID));
            AdapterPlayer player = new AdapterPlayer();
            player.setPlayer(PlayerDAO.cursorToPlayer(cursor));
            player.setPosition(PositionDAO.getPosition(posId));
            players.add(player);
        }
        return players;
    }

    public static int getAvailableLastMatch() {

        int matchId = 0;

        Match match = MatchDAO.getDisplayMatch();
        if (match != null)
            matchId = match.getIdmatch();

        if (matchId == 0) {
            matchId = getLastMatchIdWithPlayers();
        }

        return matchId;
    }

    private static int getLastMatchIdWithPlayers() {
        int matchId = 0;
        Cursor cursor = mDatabase.query(DBContact.PlayerPositionTable.TABLE_NAME,
                new String[]{"MAX(" + DBContact.PlayerPositionTable.COLUMN_MATCH_ID + ")"}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            matchId = cursor.getInt(0);
        }
        cursor.close();

        return matchId;
    }

    private static Position getPlayerPosition(int playerId, int matchId) {
        Position position = new Position();

        String WHERE_CLAUSE = DBContact.PlayerPositionTable.COLUMN_MATCH_ID + "=? AND " +
                DBContact.PlayerPositionTable.COLUMN_PLAYER_ID + " =?";
        String[] WHERE_ARGS = {String.valueOf(matchId), String.valueOf(playerId)};

        Cursor cursor = mDatabase.query(DBContact.PlayerPositionTable.TABLE_NAME, null, WHERE_CLAUSE
                , WHERE_ARGS, null, null, null);

        if (cursor.moveToFirst()) {
            int posId = cursor.getInt(cursor.getColumnIndex(DBContact.PlayerPositionTable.COLUMN_POS_ID));
            position = PositionDAO.getPosition(posId);
        }
        cursor.close();

        return position;
    }

    public static List<PlayerPosition> getAllPositions() {
        List<PlayerPosition> positions = new ArrayList<>();

        Cursor cursor = mDatabase.query(DBContact.PlayerPositionTable.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            positions.add(cursorToPositions(cursor));
        }
        cursor.close();
        return positions;
    }

//    public static List<Position> getPlayerPosition(int matchId) {
//        List<Position> position = new ArrayList<>();
//
//        String WHERE_CLAUSE = DBContact.PlayerPositionTable.COLUMN_MATCH_ID + "=?";
//        String[] WHERE_ARGS = {String.valueOf(matchId)};
//
//        Cursor cursor = mDatabase.query(DBContact.PlayerPositionTable.TABLE_NAME, null, WHERE_CLAUSE
//                , WHERE_ARGS, null, null, null);
//
//        while (cursor.moveToNext()) {
//            int posId = cursor.getInt(cursor.getColumnIndex(DBContact.PlayerPositionTable.COLUMN_POS_ID));
//            position.add(PositionDAO.getPosition(posId));
//        }
//        cursor.close();
//
//        return position;
//    }

    public static AdapterPlayer getAdapterPlayer(int playerId, int matchId) {
        AdapterPlayer adapterPlayer = new AdapterPlayer();

        String WHERE_CLAUSE = DBContact.PlayerTable.COLUMN_ID + " =?";
        String[] WHERE_ARGS = {String.valueOf(playerId)};

        Cursor cursor = mDatabase.query(DBContact.PlayerTable.TABLE_NAME, null, WHERE_CLAUSE
                , WHERE_ARGS, null, null, null);

        if (cursor.moveToFirst()) {
            adapterPlayer.setPlayer(PlayerDAO.cursorToPlayer(cursor));
            adapterPlayer.setPosition(PlayerPositionDAO.getPlayerPosition(playerId, matchId));
        }
        cursor.close();

        return adapterPlayer;
    }

    public static List<AdapterPlayer> getAdapterPlayers(int matchId) {
        List<AdapterPlayer> adapterPlayers = new ArrayList<>();

        String WHERE_CLAUSE = DBContact.PlayerPositionTable.COLUMN_MATCH_ID + " =?";
        String[] WHERE_ARGS = {String.valueOf(matchId)};
        String ORDER_BY = DBContact.PlayerPositionTable.COLUMN_POS_ID + " ASC";

        Cursor cursor = mDatabase.query(DBContact.PlayerPositionTable.TABLE_NAME, null, WHERE_CLAUSE
                , WHERE_ARGS, null, null, ORDER_BY);

        while (cursor.moveToNext()) {
            AdapterPlayer adapterPlayer = new AdapterPlayer();
            adapterPlayer.setPlayer(PlayerDAO.getPlayer(cursor.getInt(cursor.getColumnIndex(DBContact.PlayerPositionTable.COLUMN_PLAYER_ID))));
            adapterPlayer.setPosition(PositionDAO.getPosition(cursor.getInt(cursor.getColumnIndex(DBContact.PlayerPositionTable.COLUMN_POS_ID))));
            adapterPlayers.add(adapterPlayer);
        }
        cursor.close();

        return adapterPlayers;
    }

    public static boolean deleteAll() {
        return mDatabase.delete(DBContact.PlayerPositionTable.TABLE_NAME, null, null) == 1;
    }

    static PlayerPosition cursorToPositions(Cursor cursor) {
        PlayerPosition position = new PlayerPosition();
        int id = cursor.getInt(cursor.getColumnIndex(DBContact.PlayerPositionTable.COLUMN_MATCH_ID));
        position.setIdMatch(id);
        position.setIdPlayer(cursor.getInt(cursor.getColumnIndex(DBContact.PlayerPositionTable.COLUMN_PLAYER_ID)));
        position.setIdPosition(cursor.getInt(cursor.getColumnIndex(DBContact.PlayerPositionTable.COLUMN_POS_ID)));
        return position;
    }
}
