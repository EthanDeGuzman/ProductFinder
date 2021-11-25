package edu.edeguzman.productfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class Results extends AppCompatActivity {
    private Button Home,RecentSearches,Signin,Search;
    private TextView ProductField, PriceField, LinkField;
    private String url;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Home = findViewById(R.id.Button_Home);
        RecentSearches = findViewById(R.id.Button_Searches);
        Signin = findViewById(R.id.Button_SignIn);
        Search = findViewById(R.id.Button_NewSearch);
        ProductField = findViewById(R.id.ProductField);
        PriceField = findViewById(R.id.PriceField);
        LinkField = findViewById(R.id.LinkField);

        url = getIntent().getStringExtra("url");

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

                                ProductField.setText("Name: " + name);
                                PriceField.setText("Price: " + price);
                                LinkField.setText("Link: " + link);
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
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