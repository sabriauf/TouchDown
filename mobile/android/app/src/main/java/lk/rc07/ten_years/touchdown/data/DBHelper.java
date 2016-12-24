package lk.rc07.ten_years.touchdown.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sabri on 12/24/2016. database creator
 */

public class DBHelper extends SQLiteOpenHelper {

    //configs
    public static final String DATABASE_NAME = "touchdown_database.db";
    private static final int DATABASE_VERSION = 1;

    //constants
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String COMMA_SEP = ",";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
//    private static final String AUTO_INCREMENT = " AUTOINCREMENT";

    //instants
    public static DBHelper dbHelper;

    //Team db create statement
    private static final String CREATE_TABLE_TEAM = "CREATE TABLE " + DBContact.TeamTable.TABLE_NAME + "("
            + DBContact.TeamTable.COLUMN_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
            + DBContact.TeamTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP
            + DBContact.TeamTable.COLUMN_FLAG + TEXT_TYPE + COMMA_SEP
            + DBContact.TeamTable.COLUMN_LOGO + TEXT_TYPE + COMMA_SEP
            + DBContact.TeamTable.COLUMN_YEAR + INTEGER_TYPE
            + ")";

    //Match db create statement
    private static final String CREATE_TABLE_MATCH = "CREATE TABLE " + DBContact.MatchTable.TABLE_NAME + "("
            + DBContact.MatchTable.COLUMN_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
            + DBContact.MatchTable.COLUMN_TEAM_ONE + INTEGER_TYPE + COMMA_SEP
            + DBContact.MatchTable.COLUMN_TEAM_TWO + INTEGER_TYPE + COMMA_SEP
            + DBContact.MatchTable.COLUMN_DATE + DOUBLE_TYPE + COMMA_SEP
            + DBContact.MatchTable.COLUMN_LEAGUE + TEXT_TYPE + COMMA_SEP
            + DBContact.MatchTable.COLUMN_RESULT + TEXT_TYPE + COMMA_SEP
            + DBContact.MatchTable.COLUMN_STATUS + TEXT_TYPE + COMMA_SEP
            + DBContact.MatchTable.COLUMN_VENUE + TEXT_TYPE + COMMA_SEP
            + DBContact.MatchTable.COLUMN_ROUND + TEXT_TYPE
            + ")";

    //Player db create statement
    private static final String CREATE_TABLE_PLAYER = "CREATE TABLE " + DBContact.PlayerTable.TABLE_NAME + "("
            + DBContact.PlayerTable.COLUMN_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
            + DBContact.PlayerTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP
            + DBContact.PlayerTable.COLUMN_IMG + TEXT_TYPE + COMMA_SEP
            + DBContact.PlayerTable.COLUMN_TEAM + INTEGER_TYPE + COMMA_SEP
            + DBContact.PlayerTable.COLUMN_WEIGHT + DOUBLE_TYPE + COMMA_SEP
            + DBContact.PlayerTable.COLUMN_HEIGHT + DOUBLE_TYPE
            + ")";

    //Points db create statement
    private static final String CREATE_TABLE_POINTS = "CREATE TABLE " + DBContact.PointsTable.TABLE_NAME + "("
            + DBContact.PointsTable.COLUMN_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
            + DBContact.PointsTable.COLUMN_PLAYED + INTEGER_TYPE + COMMA_SEP
            + DBContact.PointsTable.COLUMN_TEAM + INTEGER_TYPE + COMMA_SEP
            + DBContact.PointsTable.COLUMN_WON + INTEGER_TYPE + COMMA_SEP
            + DBContact.PointsTable.COLUMN_LOST + INTEGER_TYPE + COMMA_SEP
            + DBContact.PointsTable.COLUMN_POINTS + INTEGER_TYPE
            + ")";

    //Score db create statement
    private static final String CREATE_TABLE_SCORE = "CREATE TABLE " + DBContact.ScoreTable.TABLE_NAME + "("
            + DBContact.ScoreTable.COLUMN_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
            + DBContact.ScoreTable.COLUMN_PLAYER + INTEGER_TYPE + COMMA_SEP
            + DBContact.ScoreTable.COLUMN_MATCH + INTEGER_TYPE + COMMA_SEP
            + DBContact.ScoreTable.COLUMN_DETAILS + TEXT_TYPE + COMMA_SEP
            + DBContact.ScoreTable.COLUMN_TEAM + INTEGER_TYPE + COMMA_SEP
            + DBContact.ScoreTable.COLUMN_SCORE + INTEGER_TYPE + COMMA_SEP
            + DBContact.ScoreTable.COLUMN_TIME + DOUBLE_TYPE + COMMA_SEP
            + DBContact.ScoreTable.COLUMN_ACTION + TEXT_TYPE
            + ")";

    //Position db create statement
    private static final String CREATE_TABLE_POSITION = "CREATE TABLE " + DBContact.PositionTable.TABLE_NAME + "("
            + DBContact.PositionTable.COLUMN_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
            + DBContact.PositionTable.COLUMN_NAME + TEXT_TYPE + COMMA_SEP
            + DBContact.PositionTable.COLUMN_NO + INTEGER_TYPE
            + ")";

    //Player Position db create statement
    private static final String CREATE_TABLE_PLAYER_POSITION = "CREATE TABLE " + DBContact.PlayerPositionTable.TABLE_NAME + "("
            + DBContact.PlayerPositionTable.COLUMN_PLAYER_ID + INTEGER_TYPE + COMMA_SEP
            + DBContact.PlayerPositionTable.COLUMN_POS_ID + INTEGER_TYPE + COMMA_SEP
            + PRIMARY_KEY  + "(" + DBContact.PlayerPositionTable.COLUMN_PLAYER_ID + COMMA_SEP
            + DBContact.PlayerPositionTable.COLUMN_POS_ID + ")" + COMMA_SEP
            + DBContact.PlayerPositionTable.COLUMN_MATCH_ID + INTEGER_TYPE
            + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_MATCH);
        sqLiteDatabase.execSQL(CREATE_TABLE_TEAM);
        sqLiteDatabase.execSQL(CREATE_TABLE_PLAYER);
        sqLiteDatabase.execSQL(CREATE_TABLE_POINTS);
        sqLiteDatabase.execSQL(CREATE_TABLE_SCORE);
        sqLiteDatabase.execSQL(CREATE_TABLE_POSITION);
        sqLiteDatabase.execSQL(CREATE_TABLE_PLAYER_POSITION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContact.PointsTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContact.PlayerPositionTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContact.PositionTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContact.PlayerTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContact.ScoreTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContact.TeamTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContact.MatchTable.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
