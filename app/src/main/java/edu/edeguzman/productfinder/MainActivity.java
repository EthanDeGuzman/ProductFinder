package edu.edeguzman.productfinder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.InputType;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private String barcodeData, name;
    private RequestQueue queue;
    private String tempName = "";
    private TextView barcodeText;
    private boolean isScannable = true;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        barcodeText = findViewById(R.id.barcode_text);
        surfaceView = findViewById(R.id.surface_view);

        isScannable = true;
        initialiseDetectorsAndSources();
    }


    private void initialiseDetectorsAndSources() {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1440, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            //Checks permissions for the camera and request permissions if needed
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Required methods for SurfaceHolder
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }

        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            //Required method
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0 && isScannable == true) {
                    barcodeText.post(new Runnable() {
                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                barcodeText.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            } else {
                                barcodeData = barcodes.valueAt(0).displayValue;
                                barcodeText.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            }

                            //When barcode is scanned convert to product name
                            convertBarcode(barcodeData);

                            isScannable = false;
                        }
                    });
                }
            }
        });
    }

    private void convertBarcode(String query) {
        String token = "afb9feace2da918945763b1630efd7adb5bbc86425d833f8da4175d3c6d94d1d";
        String url = "https://api.ean-search.org/api?token=" + token + "&op=barcode-lookup&format=json&ean=" + query;

        queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Convert String to JSON Array
                        JSONArray array = null;
                        try {
                            array = new JSONArray(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Loop through array and get data
                        for(int i = 0; i < array.length(); i++){
                            try {
                                JSONObject jsonObject = array.getJSONObject(i);
                                if (jsonObject.getString("error") == null){
                                    name = jsonObject.getString("name");
                                    barcodeText.setText(name);
                                    showProductName();
                                }
                                else{
                                    name = "";
                                    Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                                    showProductName();
                                }
                            }
                            catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Something went wrong in EAN Database API", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    private void showResults(){
        Intent showResults = new Intent(this, Results.class);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        showResults.putExtra("query", name);
        startActivity(showResults, b);
    }

    public void showProductName() {
        AlertDialog.Builder builder;
        if (name == "" || name.isEmpty() || name == null){
            builder = new AlertDialog.Builder(this)
                    .setTitle("No Results Found!")
                    .setMessage("Click here to Enter the product name")
                    .setIcon(android.R.drawable.ic_dialog_alert);
        }
        else{
            builder = new AlertDialog.Builder(this)
                    .setTitle("Is This Your Product?")
                    .setMessage("Click here to change the product name");
        }

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(name);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tempName = input.getText().toString();

                if (!tempName.equals("")&& db.insertData(tempName)) {
                }
                else{
                }

                //Show results page
                showResults();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                isScannable = true;
            }
        });

        builder.show();
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
        startActivity(showImageScanner, b);
    }

}