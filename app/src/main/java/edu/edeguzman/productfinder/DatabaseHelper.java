package edu.edeguzman.productfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME ="SearchTerms.db";
    private static final String DB_TABLE = "Search_Table";

    //columns
    private static final String ID = "ID";
    private static final String SEARCHTERM ="SEARCH_TERM";

    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE+" ("+
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            SEARCHTERM+ " TEXT "+ ")";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE);

        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String searchTerm) {
        long result=0;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        searchTerm = WordUtils.capitalizeFully(searchTerm);
        contentValues.put(SEARCHTERM, searchTerm);

        String query = "Select * from "+DB_TABLE +" Where "+ SEARCHTERM + "= '"+ searchTerm +"'";

        if (query == null || query.isEmpty())
        {
            result = db.insert(DB_TABLE, null, contentValues);
        }
        else
        {
            Cursor cursor = db.rawQuery(query, null);
            while(cursor.moveToNext())
            {
                String id = cursor.getString(0);
                deleteSearchTerm(id);

            }
            result = db.insert(DB_TABLE, null, contentValues);
        }

        return result != -1;
    }

    public void deleteSearchTerm(String givenid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String id = givenid;
        db.delete(DB_TABLE,ID+" = "+id, null);
    }



        public ArrayList<SearchTerms> GetAllData() {
        ArrayList<SearchTerms> arrayList = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "Select * from "+DB_TABLE;
            Cursor cursor = db.rawQuery(query, null);

            if(cursor.moveToLast())
            {
                do {
                    String id = cursor.getString(0);
                    String  searchTerm = cursor.getString(1);

                    SearchTerms searchTerms = new SearchTerms(id,searchTerm);

                    arrayList.add(searchTerms);
                }while (cursor.moveToPrevious());
            }

        return arrayList;
        }

}
