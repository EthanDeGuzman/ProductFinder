package edu.edeguzman.productfinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteSearchDB extends SQLiteOpenHelper {

    public static final String TABLE_SEARCHES = "searches";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SEARCH_TERM = "searchTerm";

    private static final String DATABASE_NAME = "searches.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_SEARCHES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_SEARCH_TERM
            + " text not null);";

    public SQLiteSearchDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteSearchDB.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCHES);
        onCreate(db);
    }

}
