package edu.edeguzman.productfinder;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SearchesDataSource {
    private SQLiteDatabase database;
    private SQLiteSearchDB dbHelper;
    private String[] allColumns = { SQLiteSearchDB.COLUMN_ID,
            SQLiteSearchDB.COLUMN_SEARCH_TERM };

    public SearchesDataSource(Context context) {
        dbHelper = new SQLiteSearchDB(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Searches createSearch_Term(String search_term) {
        ContentValues values = new ContentValues();
        values.put(SQLiteSearchDB.COLUMN_SEARCH_TERM, search_term);
        long insertId = database.insert(SQLiteSearchDB.TABLE_SEARCHES, null,
                values);
        Cursor cursor = database.query(SQLiteSearchDB.TABLE_SEARCHES,
                allColumns, SQLiteSearchDB.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Searches newSearchTerm = cursorToSearch(cursor);
        cursor.close();
        return newSearchTerm;
    }

    public void deleteSearchTerm(Searches search_term) {
        long id = search_term.getId();
        System.out.println("Search Term deleted with id: " + id);
        database.delete(SQLiteSearchDB.TABLE_SEARCHES, SQLiteSearchDB.COLUMN_ID
                + " = " + id, null);
    }

    public List<Searches> getAllSearches() {
        List<Searches> search_terms = new ArrayList<Searches>();

        Cursor cursor = database.query(SQLiteSearchDB.TABLE_SEARCHES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Searches comment = cursorToSearch(cursor);
            search_terms.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return search_terms;
    }

    private Searches cursorToSearch(Cursor cursor) {
        Searches search_term = new Searches();
        search_term.setId(cursor.getLong(0));
        search_term.setSearchTerm(cursor.getString(1));
        return search_term;
    }
}
