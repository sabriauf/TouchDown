package lk.rc07.ten_years.touchdown.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import lk.rc07.ten_years.touchdown.models.FBImage;

/**
 * Created by Sabri on 4/13/2017. data access model for facebook images
 */

public class ImageDAO extends DBManager {

    private static boolean checkIfImageAvailable(String imageId) {
        String query = "";
        query = query.concat("SELECT * FROM ");
        query = query.concat(DBContact.ImageTable.TABLE_NAME);
        query = query.concat(" WHERE ");
        query = query.concat(DBContact.ImageTable.COLUMN_ID);
        query = query.concat("='");
        query = query.concat(imageId);
        query = query.concat("'");

        Cursor cursor = mDatabase.rawQuery(query, null);
        boolean isAvailable = cursor.moveToFirst();
        cursor.close();
        return isAvailable;
    }

    public static boolean addImage(FBImage image) {

        ContentValues values = new ContentValues();

        boolean isImageAlreadyExist = checkIfImageAvailable(image.getId());

        values.put(DBContact.ImageTable.COLUMN_CREATED, image.getCreated_time());
        values.put(DBContact.ImageTable.COLUMN_MATCH, image.getMatch());
        values.put(DBContact.ImageTable.COLUMN_WIDTH, image.getWidth());
        values.put(DBContact.ImageTable.COLUMN_HEIGHT, image.getHeight());

        if (!isImageAlreadyExist) {
            values.put(DBContact.ImageTable.COLUMN_ID, image.getId());
            return (mDatabase.insert(DBContact.ImageTable.TABLE_NAME, null, values) != -1);

        } else {
            String WHERE_CLAUSE = DBContact.ImageTable.COLUMN_ID + "=?";
            String[] WHERE_ARGS = {String.valueOf(image.getId())};
            return mDatabase.update(DBContact.ImageTable.TABLE_NAME, values, WHERE_CLAUSE, WHERE_ARGS) == 1;
        }
    }

    public static List<FBImage> getImages(int matchId) {
        List<FBImage> images = new ArrayList<>();

        String WHERE_CLAUSE = DBContact.ImageTable.COLUMN_MATCH + "=?";
        String[] WHERE_ARGS = {String.valueOf(matchId)};

        Cursor cursor = mDatabase.query(DBContact.ImageTable.TABLE_NAME, null, WHERE_CLAUSE, WHERE_ARGS, null, null, null);
        while (cursor.moveToNext()) {
            images.add(cursorToImage(cursor));
        }
        cursor.close();
        return images;
    }

    private static FBImage cursorToImage(Cursor cursor) {
        FBImage image = new FBImage();
        image.setId(cursor.getString(cursor.getColumnIndex(DBContact.ImageTable.COLUMN_ID)));
        image.setCreated_time(cursor.getString(cursor.getColumnIndex(DBContact.ImageTable.COLUMN_CREATED)));
        image.setMatch(cursor.getInt(cursor.getColumnIndex(DBContact.ImageTable.COLUMN_MATCH)));
        image.setWidth(cursor.getInt(cursor.getColumnIndex(DBContact.ImageTable.COLUMN_WIDTH)));
        image.setHeight(cursor.getInt(cursor.getColumnIndex(DBContact.ImageTable.COLUMN_HEIGHT)));
        return image;
    }
}
