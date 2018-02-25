package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import lk.rc07.ten_years.touchdown.models.Position;

/**
 * Created by Sabri on 12/24/2016. Position data access model
 */

public class PositionDAO extends DBManager {

    private static boolean checkIfPositionAvailable(int posId) {
        String query = "";
        query = query.concat("SELECT * FROM ");
        query = query.concat(DBContact.PositionTable.TABLE_NAME);
        query = query.concat(" WHERE ");
        query = query.concat(DBContact.PositionTable.COLUMN_ID);
        query = query.concat("='");
        query = query.concat(String.valueOf(posId));
        query = query.concat("'");

        Cursor cursor = mDatabase.rawQuery(query, null);
        boolean isAvailable = cursor.moveToFirst();
        cursor.close();
        return isAvailable;
    }

    public static boolean addPosition(Position position) {

        ContentValues values = new ContentValues();

        boolean isRoomAlreadyExist = checkIfPositionAvailable(position.getIdPosition());

        values.put(DBContact.PositionTable.COLUMN_NAME, position.getPosName());
        values.put(DBContact.PositionTable.COLUMN_NO, position.getPosNo());

        if (!isRoomAlreadyExist) {
            values.put(DBContact.PositionTable.COLUMN_ID, position.getIdPosition());
            return (mDatabase.insert(DBContact.PositionTable.TABLE_NAME, null, values) != -1);

        } else {
            String WHERE_CLAUSE = DBContact.PositionTable.COLUMN_ID + "=?";
            String[] WHERE_ARGS = {String.valueOf(position.getIdPosition())};
            return mDatabase.update(DBContact.PositionTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
        }
    }

    public static Position getPosition(int posId) {
        Position position = null;

        String WHERE_CLAUSE = DBContact.PositionTable.COLUMN_ID + "=?";
        String[] WHERE_ARGS = {String.valueOf(posId)};

        Cursor cursor = mDatabase.query(DBContact.PositionTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        if (cursor.moveToFirst()) {
            position = cursorToPosition(cursor);
        }
        cursor.close();
        return position;
    }

    public static Position getPositionForNo(int posNo) {
        Position position = null;

        String WHERE_CLAUSE = DBContact.PositionTable.COLUMN_NO + "=?";
        String[] WHERE_ARGS = {String.valueOf(posNo)};

        Cursor cursor = mDatabase.query(DBContact.PositionTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        if (cursor.moveToFirst()) {
            position = cursorToPosition(cursor);
        }
        cursor.close();
        return position;
    }

    private static Position cursorToPosition(Cursor cursor) {
        Position position = new Position();
        position.setIdPosition(cursor.getInt(cursor.getColumnIndex(DBContact.PositionTable.COLUMN_ID)));
        position.setPosName(cursor.getString(cursor.getColumnIndex(DBContact.PositionTable.COLUMN_NAME)));
        position.setPosNo(cursor.getInt(cursor.getColumnIndex(DBContact.PositionTable.COLUMN_NO)));
        return position;
    }

    public static boolean deleteAll() {
        return mDatabase.delete(DBContact.PositionTable.TABLE_NAME, null, null) == 1;
    }
}
