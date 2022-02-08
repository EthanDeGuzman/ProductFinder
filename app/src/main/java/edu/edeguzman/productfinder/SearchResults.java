package edu.edeguzman.productfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class SearchResults extends AppCompatActivity {
    private String ebayUrl, amazonUrl, aliExpressUrl, query;
    private RequestQueue queue;
    private String s1[], s2[], s3[];
    private RecyclerView rview;
    private int counter=1;
    private myAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        rview = findViewById(R.id.recyclerView);

        s1 = getResources().getStringArray(R.array.ProductName);
        s2 = getResources().getStringArray(R.array.ProductPrice);
        s3 = getResources().getStringArray(R.array.ProductLink);

        recyclerAdapter = new myAdapter(this, s1, s2, s3);
        rview.setAdapter(recyclerAdapter);
        rview.setLayoutManager(new LinearLayoutManager(this));

        SearchView search =  findViewById(R.id.searchView);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                query = search.getQuery().toString();
                callApis();
                search.setQuery("", false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }

    protected void callApis(){
        //Declare URls and call methods (Euro Based)
        ebayUrl ="https://ebay-products-search-scraper.p.rapidapi.com/products?query=" + query + "&page=1&Item_Location=europe";
        amazonUrl = "https://amazon-deutschland-data-scraper.p.rapidapi.com/search/" + query + "?api_key=7c3c12edf5e0523209099e036c847ef1";
        aliExpressUrl = "https://magic-aliexpress1.p.rapidapi.com/api/products/search?name=" + query + "&sort=SALE_PRICE_ASC&page=1&targetCurrency=EUR&lg=en";

        searchEbay(ebayUrl);
        searchAmazon(amazonUrl);
        searchAliExpress(aliExpressUrl);
    }

    protected void searchEbay(String url) {
        //Replace all Spaces in the Url with %20 for query
        url = url.replaceAll(" ", "%20");

        queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("products");

                            for (int i = 0; i < 5; i++) {
                                JSONObject products = array.getJSONObject(i);

                                String name = products.getString("title");
                                String price = products.getString("price");
                                String link = products.getString("productLink");

                                s1[counter] = name;
                                s2[counter] = price;
                                s3[counter] = link;

                                counter++;

                                rview.setAdapter(recyclerAdapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Something went wrong in Ebay API", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("x-rapidapi-host", "ebay-products-search-scraper.p.rapidapi.com");
                headers.put("x-rapidapi-key", "721f9e5ae4msh6c1a025129c3019p187cfbjsnc2b39eb1b331");
                return headers;
            }

        };

        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    protected void searchAmazon(String url){
        //Replace all Spaces in the Url with %20 for query
        url = url.replaceAll(" ", "%20");

        queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("results");

                            for(int i = 0; i < 5; i++){
                                JSONObject products = array.getJSONObject(i);

                                String name = products.getString("name");
                                String price = products.getString("price");
                                String link = products.getString("url");

                                s1[counter] = name;
                                s2[counter] = "€" + price;
                                s3[counter] = link;

                                counter++;

                                rview.setAdapter(recyclerAdapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Something went wrong in Amazon API", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("x-rapidapi-host", "amazon-deutschland-data-scraper.p.rapidapi.com");
                headers.put("x-rapidapi-key", "721f9e5ae4msh6c1a025129c3019p187cfbjsnc2b39eb1b331");
                return headers;
            }

        };

        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

    }

    protected void searchAliExpress(String url){
        //Replace all Spaces in the Url with %20 for query
        url = url.replaceAll(" ", "%20");

        queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("docs");

                            for(int i = 0; i < 5; i++){
                                JSONObject data = array.getJSONObject(i);

                                String name = data.getString("product_title");
                                String price = data.getString("app_sale_price");
                                String link = data.getString("product_detail_url");

                                s1[counter] = name;
                                s2[counter] = "€" + price;
                                s3[counter] = link;

                                counter++;

                                rview.setAdapter(recyclerAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Something went wrong in AliExpress API", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("x-rapidapi-host", "magic-aliexpress1.p.rapidapi.com");
                headers.put("x-rapidapi-key", "721f9e5ae4msh6c1a025129c3019p187cfbjsnc2b39eb1b331");
                return headers;
            }

        };

        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public void callHome(View view) {
        finish();
    }

    public void callSearch(View view) {
        finish();
    }

    public void CallRecentSearches(View view) {
        finish();
    }

    public void callImageScanner(View view) {
        finish();
    }

}