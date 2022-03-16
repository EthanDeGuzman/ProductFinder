package edu.edeguzman.productfinder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SearchHistory extends AppCompatActivity {

    DatabaseHelper db;
    public ListView list;
    public ArrayList<String> listItem;
    public ArrayList<SearchTerms> arrayList;
    SearchAdapter searchAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);

        db = new DatabaseHelper(this);
        listItem = new ArrayList<>();
        arrayList = new ArrayList<>();
        list = (ListView) findViewById(R.id.lv);
        loadDataInListView();

    }


    public void loadDataInListView()
    {
        arrayList = db.GetAllData();


        searchAdapter = new SearchAdapter(this,arrayList);
        list.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();

    }

    public void callHome(View view) {
        Intent showHome= new Intent(this, MainActivity.class);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(showHome, b);
    }

    public void callSearch(View view) {
        Intent showSearch = new Intent(this, SearchResults.class);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(showSearch, b);
    }

    public void CallRecentSearches(View view) {
        Intent showHistory = new Intent(this, SearchHistory.class);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(showHistory, b);
    }

    public void callImageScanner(View view) {
        Intent showImageScanner = new Intent(this, ImageScanner.class);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(showImageScanner,b);
    }

}