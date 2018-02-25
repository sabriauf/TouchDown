package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import lk.rc07.ten_years.touchdown.models.Points;

/**
 * Created by Sabri on 1/15/2017. data access model for Points
 */

public class PointsDAO extends DBManager {

    private static final String ORDER_DESC = " DESC";

    private static boolean checkIfPointsAvailable(int pointId) {
        String query = "";
        query = query.concat("SELECT * FROM ");
        query = query.concat(DBContact.PointsTable.TABLE_NAME);
        query = query.concat(" WHERE ");
        query = query.concat(DBContact.PointsTable.COLUMN_ID);
        query = query.concat("='");
        query = query.concat(String.valueOf(pointId));
        query = query.concat("'");

        Cursor cursor = mDatabase.rawQuery(query, null);
        boolean isAvailable = cursor.moveToFirst();
        cursor.close();
        return isAvailable;
    }

    public static boolean addPoints(Points points) {

        ContentValues values = new ContentValues();

        boolean isPointsAlreadyExist = checkIfPointsAvailable(points.getIdPoint());

        values.put(DBContact.PointsTable.COLUMN_LOST, points.getLost());
        values.put(DBContact.PointsTable.COLUMN_PLAYED, points.getPlayed());
        values.put(DBContact.PointsTable.COLUMN_POINTS, points.getPoints());
        values.put(DBContact.PointsTable.COLUMN_TEAM, points.getTeamId());
        values.put(DBContact.PointsTable.COLUMN_WON, points.getWon());
        values.put(DBContact.PointsTable.COLUMN_GROUP, points.getIdGroup());

        if (!isPointsAlreadyExist) {
            values.put(DBContact.PointsTable.COLUMN_ID, points.getIdPoint());
            return (mDatabase.insert(DBContact.PointsTable.TABLE_NAME, null, values) != -1);

        } else {
            String WHERE_CLAUSE = DBContact.PointsTable.COLUMN_ID + "=?";
            String[] WHERE_ARGS = {String.valueOf(points.getIdPoint())};
            return mDatabase.update(DBContact.PointsTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
        }
    }

//    public static List<Points> getAllPointTable() {
//        List<Points> points = new ArrayList<>();
//
//        Cursor cursor = mDatabase.query(DBContact.PointsTable.TABLE_NAME, null, null, null, null, null, null);
//        while (cursor.moveToNext()) {
//            points.add(cursorToPoints(cursor));
//        }
//        cursor.close();
//        return points;
//    }

    public static List<Points> getPointTable(int groupId) {
        List<Points> points = new ArrayList<>();

        String[] SELECT = {"*", "(" + DBContact.PointsTable.COLUMN_POINTS + "/" + DBContact.PointsTable.COLUMN_PLAYED + ") as total"};
        String WHERE_CLAUSE = DBContact.PointsTable.COLUMN_GROUP + "=? ";
        String[] WHERE_ARGS = {String.valueOf(groupId)};
        String orderBy = "total" + ORDER_DESC;
        Cursor cursor = mDatabase.query(DBContact.PointsTable.TABLE_NAME, SELECT, WHERE_CLAUSE, WHERE_ARGS, null, null, orderBy);
        while (cursor.moveToNext()) {
            points.add(cursorToPoints(cursor));
        }
        cursor.close();
        return points;
    }

    private static Points cursorToPoints(Cursor cursor) {
        Points points = new Points();
        points.setIdPoint(cursor.getInt(cursor.getColumnIndex(DBContact.PointsTable.COLUMN_ID)));
        points.setLost(cursor.getInt(cursor.getColumnIndex(DBContact.PointsTable.COLUMN_LOST)));
        points.setPlayed(cursor.getInt(cursor.getColumnIndex(DBContact.PointsTable.COLUMN_PLAYED)));
        points.setPoints(cursor.getFloat(cursor.getColumnIndex(DBContact.PointsTable.COLUMN_POINTS)));
        points.setTeamId(cursor.getInt(cursor.getColumnIndex(DBContact.PointsTable.COLUMN_TEAM)));
        points.setWon(cursor.getInt(cursor.getColumnIndex(DBContact.PointsTable.COLUMN_WON)));
        points.setIdGroup(cursor.getInt(cursor.getColumnIndex(DBContact.PointsTable.COLUMN_GROUP)));
        return points;
    }

    public static boolean deleteAll() {
        return mDatabase.delete(DBContact.PointsTable.TABLE_NAME, null, null) == 1;
    }
}
