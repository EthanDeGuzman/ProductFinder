package edu.edeguzman.productfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class MainActivity extends AppCompatActivity {
    private Button scanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = findViewById(R.id.scanbtn);
    }

    public void doScan(View view) {
        Intent intent = new Intent(this, BarcodeScanner.class);
        startActivity(intent);
    }
}