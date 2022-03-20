package edu.edeguzman.productfinder;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import edu.edeguzman.productfinder.ml.ModelUnquant;

public class ImageScanner extends AppCompatActivity {

    EditText result;
    ImageView imageView;
    Button picture, choosePhoto, searchProductbtn;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_scanner);

        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);
        choosePhoto = findViewById(R.id.button_2);
        searchProductbtn = findViewById(R.id.Searchbtn);

        result.setVisibility(View.INVISIBLE);
        searchProductbtn.setVisibility(View.INVISIBLE);

        searchProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(result.getText().toString() != null || result.getText().toString() != "")
                {
                    String product = result.getText().toString();

                    callResult(product);
                }
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                // view image gallery if we have permission
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    selectImage();
                else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }

            }
        });
    }

    private void selectImage() {
        //intent to select an image
        Intent photoGalleryIntent = new Intent(Intent.ACTION_PICK);
        photoGalleryIntent.setType("image/*");
        startActivityForResult(photoGalleryIntent, 2);
    }

    public void classifyImage(Bitmap image){
        try {
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"PS4 Black Controller", "PS5 White Controller", "Nintendo Switch Red & Blue Joycons",
                    "PS5 Console", "Seagate Portable PS4 Hard Drive", "Logitech G403 Wired Gaming Mouse",
                    "HyperX Cloud Wireless Headset", "PS3 Slim Console", "Nintendo Switch Console",
                    "Logitech G915 TKL Wireless Gaming Keyboard", "PS5 Black Controller", "Logitech Superlight Pro Wireless Gaming Mouse",
                    "Ezontec Sniper 100 Gaming Mouse", "PS4 Pro Console", "Razer Hammerhead Earbuds",
                    "Sennheiser HD598 SE Headphones", "Superlux HD681 MS Headphones", "ROD Lavalier Microphone",
                    "Nintendo Wii White Console", "AT2020 Audio-Technica Microphone", "HyperX Quadcast S Microphone",
                    "Lenovo SK-8827 Keyboard", "Corsair STRAFE Mechanical Gaming Keyboard", "Gioteck XH100S Headset",
                    "SN 30 Pro Controller", "Xbox One Black Controller", "Steam Controller",
                    "PS3 Black Controller", "Microsoft Wireless Mobile Mouse 3500", "Xbox Series X Console" };

            result.setText(classes[maxPos]);
            result.setVisibility(View.VISIBLE);
            searchProductbtn.setVisibility(View.VISIBLE);
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Bitmap image = loadfromUri(uri);

            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            image = image.copy(Bitmap.Config.RGBA_F16, true);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap loadfromUri(Uri uri){
        Bitmap bitmap = null;

        try {
            if (Build.VERSION.SDK_INT > 27) {
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }

    public void callResult(String query){
        Intent showResults = new Intent(this, Results.class);
        showResults.putExtra("query", query);
        startActivity(showResults);
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