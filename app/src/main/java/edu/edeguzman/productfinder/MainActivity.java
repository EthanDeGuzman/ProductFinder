package edu.edeguzman.productfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Button scanBtn, searchBtn;
    private TextView search;
    private Spinner scanOptions;
    private String[] Options = {"Scan Barcode", "Scan Image"};
    private ArrayAdapter<String> optionsAdapter;
    private String UserChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = findViewById(R.id.scanbtn);
        search = findViewById(R.id.searchQuery);
        searchBtn = findViewById(R.id.scanQuery);
        scanOptions = findViewById(R.id.scanOptions);

        optionsAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, Options);
        scanOptions.setAdapter(optionsAdapter);
    }

    public void doScan(View view) {
        UserChoice = scanOptions.getSelectedItem().toString();

        if(UserChoice == "Scan Barcode")
        {
            Intent barcodeScan = new Intent(this, BarcodeScanner.class);
            startActivity(barcodeScan);
        }
        else if (UserChoice == "Scan Image")
        {
            Intent imageScan = new Intent(this, ImageScanner.class);
            startActivity(imageScan);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Select an option from the dropdown", Toast.LENGTH_SHORT).show();
        }




    }


    public void callQuery(View view) {
        Toast.makeText(getApplicationContext(), "Search Query started", Toast.LENGTH_SHORT).show();
        String query = search.getText().toString().toLowerCase();

        Intent showResults = new Intent(this, Results.class);
        showResults.putExtra("query", query);
        startActivity(showResults);
    }
}