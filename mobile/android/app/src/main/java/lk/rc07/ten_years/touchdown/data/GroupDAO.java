package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import lk.rc07.ten_years.touchdown.models.Group;

/**
 * Created by Sabri on 2/12/2017. Group Data access model
 */

public class GroupDAO extends DBManager {

    public static final String ORDER_ASC = " ASC";
    public static final String ORDER_DESC = " DESC";

    private static boolean checkIfGroupAvailable(int groupId) {
        String query = "";
        query = query.concat("SELECT * FROM ");
        query = query.concat(DBContact.GroupTable.TABLE_NAME);
        query = query.concat(" WHERE ");
        query = query.concat(DBContact.GroupTable.COLUMN_ID);
        query = query.concat("='");
        query = query.concat(String.valueOf(groupId));
        query = query.concat("'");

        Cursor cursor = mDatabase.rawQuery(query, null);
        boolean isAvailable = cursor.moveToFirst();
        cursor.close();
        return isAvailable;
    }

    public static boolean addGroup(Group group) {

        ContentValues values = new ContentValues();

        boolean isMatchAlreadyExist = checkIfGroupAvailable(group.getGroupId());

        values.put(DBContact.GroupTable.COLUMN_NAME, group.getGroupName());
        values.put(DBContact.GroupTable.COLUMN_LEAGUE, group.getLeagueName());
        values.put(DBContact.GroupTable.COLUMN_ROUND, group.getRoundName());
        values.put(DBContact.GroupTable.COLUMN_YEAR, group.getYear());

        if (!isMatchAlreadyExist) {
            values.put(DBContact.GroupTable.COLUMN_ID, group.getGroupId());
            return (mDatabase.insert(DBContact.GroupTable.TABLE_NAME, null, values) != -1);

        } else {
            String WHERE_CLAUSE = DBContact.GroupTable.COLUMN_ID + "=?";
            String[] WHERE_ARGS = {String.valueOf(group.getGroupId())};
            return mDatabase.update(DBContact.GroupTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
        }
    }

    public static List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();

        Cursor cursor = mDatabase.query(DBContact.GroupTable.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            groups.add(cursorToGroup(cursor));
        }
        cursor.close();
        return groups;
    }

    public static List<String> getAllFromColumn(String column) {
        List<String> values = new ArrayList<>();
        String orderBy =  column + ORDER_DESC;

        Cursor cursor = mDatabase.query(true, DBContact.GroupTable.TABLE_NAME, new String[]{column}, null, null,
                null, null, orderBy, null);
        while (cursor.moveToNext()) {
            values.add(cursor.getString(cursor.getColumnIndex(column)));
        }
        cursor.close();
        return values;
    }

    public static List<String> getAllFromColumn(String column, String where, String order) {
        List<String> values = new ArrayList<>();
        String orderBy =  column + order;

        Cursor cursor = mDatabase.query(true, DBContact.GroupTable.TABLE_NAME, new String[]{column}, where, null, null, null, orderBy, null);
        while (cursor.moveToNext()) {
            values.add(cursor.getString(cursor.getColumnIndex(column)));
        }
        cursor.close();
        return values;
    }

    public static int getGroupIdForInfo(String league, String round, String group) {

        int groupId = 0;

        String WHERE_CLAUSE = DBContact.GroupTable.COLUMN_LEAGUE + "=? " + " and " +
                DBContact.GroupTable.COLUMN_ROUND + "=? " + " and " +
                DBContact.GroupTable.COLUMN_NAME + "=? ";
        String[] WHERE_ARGS = new String[]{league, round, group};

        Cursor cursor = mDatabase.query(true, DBContact.GroupTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null, null);
        while (cursor.moveToNext()) {
            groupId = cursor.getInt(cursor.getColumnIndex(DBContact.GroupTable.COLUMN_ID));
        }
        cursor.close();
        return groupId;
    }

    public static Group getGroupForId(int gId) {
        Group group = null;

        String WHERE_CLAUSE = DBContact.GroupTable.COLUMN_ID + "=?";
        String[] WHERE_ARGS = {String.valueOf(gId)};

        Cursor cursor = mDatabase.query(DBContact.GroupTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        while (cursor.moveToNext()) {
            group = cursorToGroup(cursor);
        }
        cursor.close();
        return group;
    }

    private static Group cursorToGroup(Cursor cursor) {
        Group group = new Group();
        group.setGroupId(cursor.getInt(cursor.getColumnIndex(DBContact.GroupTable.COLUMN_ID)));
        group.setGroupName(cursor.getString(cursor.getColumnIndex(DBContact.GroupTable.COLUMN_NAME)));
        group.setLeagueName(cursor.getString(cursor.getColumnIndex(DBContact.GroupTable.COLUMN_LEAGUE)));
        group.setRoundName(cursor.getString(cursor.getColumnIndex(DBContact.GroupTable.COLUMN_ROUND)));
        group.setYear(cursor.getInt(cursor.getColumnIndex(DBContact.GroupTable.COLUMN_YEAR)));
        return group;
    }

    public static boolean deleteAll() {
        return mDatabase.delete(DBContact.GroupTable.TABLE_NAME, null, null) == 1;
    }
}
