package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import lk.rc07.ten_years.touchdown.models.Staff;

public class StaffDAO extends DBManager {

    public static final String ORDER_ASC = " ASC";
//    public static final String ORDER_DESC = " DESC";

    private static boolean checkIfStaffAvailable(int staffId) {
        String query = "";
        query = query.concat("SELECT * FROM ");
        query = query.concat(DBContact.PlayerStaffTable.TABLE_NAME);
        query = query.concat(" WHERE ");
        query = query.concat(DBContact.PlayerStaffTable.COLUMN_STAFF_ID);
        query = query.concat("='");
        query = query.concat(String.valueOf(staffId));
        query = query.concat("'");

        Cursor cursor = mDatabase.rawQuery(query, null);
        boolean isAvailable = cursor.moveToFirst();
        cursor.close();
        return isAvailable;
    }

    public static boolean addStaff(Staff staff) {

        ContentValues values = new ContentValues();

        boolean isStaffAlreadyExist = checkIfStaffAvailable(staff.getId());

        values.put(DBContact.PlayerStaffTable.COLUMN_NAME, staff.getName());
        values.put(DBContact.PlayerStaffTable.COLUMN_POSITION, staff.getPosition());
        values.put(DBContact.PlayerStaffTable.COLUMN_STATUS, staff.getStatus());
        values.put(DBContact.PlayerStaffTable.COLUMN_ORDER, staff.getOrder());

        if (!isStaffAlreadyExist) {
            values.put(DBContact.PlayerStaffTable.COLUMN_STAFF_ID, staff.getId());
            return (mDatabase.insert(DBContact.PlayerStaffTable.TABLE_NAME, null, values) != -1);

        } else {
            String WHERE_CLAUSE = DBContact.PlayerStaffTable.COLUMN_STAFF_ID + "=?";
            String[] WHERE_ARGS = {String.valueOf(staff.getId())};
            return mDatabase.update(DBContact.PlayerStaffTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
        }
    }

    public static List<Staff> getAll() {
        String column = DBContact.PlayerStaffTable.COLUMN_ORDER;
        List<Staff> values = new ArrayList<>();
        String where = DBContact.PlayerStaffTable.COLUMN_STATUS + " = ?";
        String orderBy = column + " " + ORDER_ASC;

        Cursor cursor = mDatabase.query(true, DBContact.PlayerStaffTable.TABLE_NAME, null,
                where, new String[] {"ACTIVE"}, null, null, orderBy, null);
        while (cursor.moveToNext()) {
            values.add(cursorToStaff(cursor));
        }
        cursor.close();
        return values;
    }

    private static Staff cursorToStaff(Cursor cursor) {
        Staff staff = new Staff();
        staff.setId(cursor.getInt(cursor.getColumnIndex(DBContact.PlayerStaffTable.COLUMN_STAFF_ID)));
        staff.setOrder(cursor.getInt(cursor.getColumnIndex(DBContact.PlayerStaffTable.COLUMN_ORDER)));
        staff.setName(cursor.getString(cursor.getColumnIndex(DBContact.PlayerStaffTable.COLUMN_NAME)));
        staff.setStatus(cursor.getString(cursor.getColumnIndex(DBContact.PlayerStaffTable.COLUMN_STATUS)));
        staff.setPosition(cursor.getString(cursor.getColumnIndex(DBContact.PlayerStaffTable.COLUMN_POSITION)));
        return staff;
    }
}
