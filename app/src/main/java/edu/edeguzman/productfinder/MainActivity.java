package edu.edeguzman.productfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
    private Button scanBtn, searchBtn;
    private TextView search, nameView, priceView, linkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = findViewById(R.id.scanbtn);
        search = findViewById(R.id.searchQuery);
        nameView = findViewById(R.id.name);
        priceView = findViewById(R.id.price);
        linkView = findViewById(R.id.link);
        searchBtn = findViewById(R.id.scanQuery);

    }

    public void doScan(View view) {
        Intent intent = new Intent(this, BarcodeScanner.class);
        startActivity(intent);
    }


    public void callQuery(View view) {
        Toast.makeText(getApplicationContext(), "Search Query started", Toast.LENGTH_SHORT).show();
        String query = search.getText().toString().toLowerCase();

        Intent showResults = new Intent(this, Results.class);
        showResults.putExtra("query", query);
        startActivity(showResults);
    }
}