package edu.edeguzman.productfinder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
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
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private String barcodeData, name;
    private RequestQueue queue;
    private String tempName = "";
    private TextView barcodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        barcodeText = findViewById(R.id.barcode_text);
        surfaceView = findViewById(R.id.surface_view);
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
                if (barcodes.size() != 0) {
                    barcodeDetector.release();
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
                        }
                    });
                }
            }
        });
    }

    private void convertBarcode(String query) {
        String token = "731584b4ddd0b2a2f7ffb1c2f1636d00e0a5db44fa88ea483f009d719b97743d";
        String url = "https://api.ean-search.org/api?token=" + token + "&op=barcode-lookup&format=json&ean=" + query;

        queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);

                                name = jsonObject.getString("name");

                                barcodeText.setText(name);
                                //Show the product name first to make sure it is correct
                                showProductName();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
        showResults.putExtra("query", name);
        startActivity(showResults);
    }

    public void showProductName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Product Name")
                .setMessage("Change the product name if this is the wrong product");

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

                //Show results page
                showResults();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void callSearch(View view) {
        Intent showSearch = new Intent(this, SearchResults.class);
        startActivity(showSearch);
    }

    public void CallRecentSearches(View view) {
        Intent showHistory = new Intent(this, SearchHistory.class);
        startActivity(showHistory);
    }

    public void callImageScanner(View view) {
        Intent showImageScanner = new Intent(this, ImageScanner.class);
        startActivity(showImageScanner);
    }

}