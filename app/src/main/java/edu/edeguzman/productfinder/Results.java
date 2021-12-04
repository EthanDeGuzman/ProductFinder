package edu.edeguzman.productfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Results extends AppCompatActivity {
    private Button Home,RecentSearches,Signin,Search;
    private TextView ProductField, PriceField, LinkField;
    private String ebayUrl, amazonUrl, aliExpressUrl, query;
    private RequestQueue queue;
    private ListView displayData;
    private List<String> Products;
    private ArrayAdapter<String> productsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Home = findViewById(R.id.Button_Home);
        RecentSearches = findViewById(R.id.Button_Searches);
        Signin = findViewById(R.id.Button_SignIn);
        Search = findViewById(R.id.Button_NewSearch);

        displayData = findViewById(R.id.DisplayData);
        Products = new ArrayList<String>();
        productsAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, Products);
        displayData.setAdapter(productsAdapter);

        query = getIntent().getStringExtra("query");

        ebayUrl ="https://ebay-product-search-scraper.p.rapidapi.com/index.php?query=" + query;
        amazonUrl = "https://amazon-data-scraper35.p.rapidapi.com/search/" + query + "?api_key=9df2c0810a9fe33226a6d618dea35d96";
        aliExpressUrl = "https://ali-express1.p.rapidapi.com/search?query=" + query;

        searchEbay(ebayUrl);
        searchAmazon(amazonUrl);
        //searchAliExpress(aliExpressUrl);
    }

    protected void searchEbay(String url){
        //Replace all Spaces in the Url with %20 for query
        url.replaceAll(" ", "%20");

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

                                Products.add(name);
                                Products.add(price);
                                Products.add(link);

                                displayData.setAdapter(productsAdapter);
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

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    protected void searchAmazon(String url){
        //Replace all Spaces in the Url with %20 for query
        url.replaceAll(" ", "%20");

        queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("results");

                            for(int i = 0; i < 1; i++){
                                JSONObject products = array.getJSONObject(i);

                                String name = products.getString("name");
                                String price = String.valueOf(products.getInt("price"));
                                String link = products.getString("url");

                                //Set Text Variables here
                                Products.add(name);
                                Products.add(price);
                                Products.add(link);

                                displayData.setAdapter(productsAdapter);
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
                headers.put("x-rapidapi-host", "amazon-data-scraper35.p.rapidapi.com");
                headers.put("x-rapidapi-key", "721f9e5ae4msh6c1a025129c3019p187cfbjsnc2b39eb1b331");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    protected void searchAliExpress(String url){
        //Replace all Spaces in the Url with %20 for query
        url.replaceAll(" ", "%20");

        queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("data");

                            for(int i = 0; i < 1; i++){
                                JSONObject array = data.getJSONObject(i);

                                JSONArray searchResult = array.getJSONArray("searchResult");

                                JSONObject searchArray = searchResult.getJSONObject(i);

                                JSONArray mods = searchArray.getJSONArray("mods");

                                JSONObject modsArray = mods.getJSONObject(i);

                                JSONArray itemList = modsArray.getJSONArray("itemList");

                                JSONObject itemsArray = itemList.getJSONObject(i);

                                JSONArray content = itemsArray.getJSONArray("content");

                                JSONObject contentArray = content.getJSONObject(i);

                                JSONArray title = contentArray.getJSONArray("title");
                                JSONArray price = contentArray.getJSONArray("prices");

                                JSONObject titleArray = title.getJSONObject(i);

                                JSONObject priceArray = price.getJSONObject(i);

                                JSONArray salePrice = priceArray.getJSONArray("salePrice");

                                JSONObject salePriceArray = salePrice.getJSONObject(i);

                                String name = titleArray.getString("displayTitle");
                                String priceText = salePriceArray.getString("prices");

                                Products.add(name);
                                Products.add(priceText);
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
                headers.put("x-rapidapi-host", "ali-express1.p.rapidapi.com");
                headers.put("x-rapidapi-key", "721f9e5ae4msh6c1a025129c3019p187cfbjsnc2b39eb1b331");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }
}