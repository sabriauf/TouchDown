package lk.rc07.ten_years.touchdown.data;

import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sabri on 12/24/2016. database manager
 */

public class DBManager {
    //Instance
    private static DBManager instance;
    private static DBHelper mDatabaseHelper;
    protected static SQLiteDatabase mDatabase;
    private AtomicInteger mOpenCounter = new AtomicInteger();

    protected DBManager() {
    }

    public static synchronized DBManager initializeInstance(DBHelper helper) {
        if (instance == null) {
            instance = new DBManager();
            mDatabaseHelper = helper;
        }

        return instance;
    }

    public synchronized boolean openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1 && mDatabaseHelper != null) {
            mDatabase = mDatabaseHelper.getWritableDatabase();
            return true;
        }

        return (mDatabase != null);
    }

    public void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            mDatabase.close();
        }
    }
}
