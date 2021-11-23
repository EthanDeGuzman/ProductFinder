package edu.edeguzman.productfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private Button scanBtn, searchBtn;
    private TextView search, nameView, priceView, linkView;
    private RequestQueue queue;

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
        String url ="https://ebay-product-search-scraper.p.rapidapi.com/index.php?query=" + query;
        queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("products");
                            for(int i = 0; i < 1; i++){
                            JSONObject products = array.getJSONObject(i);

                            String name = products.getString("name");
                            String price = products.getString("price");
                            String link = products.getString("link");

                            nameView.setText("Name: " + name);
                            priceView.setText("Price: " + price);
                            linkView.setText("Link: " + link);
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        nameView.setText("That did not work");
                        error.printStackTrace();
                    }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("x-rapidapi-host", "ebay-product-search-scraper.p.rapidapi.com");
                headers.put("x-rapidapi-key", "721f9e5ae4msh6c1a025129c3019p187cfbjsnc2b39eb1b331");
                return headers;
            }
        };

        queue.add(request);
    }
}