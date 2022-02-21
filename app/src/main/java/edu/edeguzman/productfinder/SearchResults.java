package edu.edeguzman.productfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResults extends AppCompatActivity {
    private String ebayUrl, amazonUrl, aliExpressUrl, query;
    private RequestQueue queue;
    private RecyclerView rview;
    private int counter=1;
    private myAdapter recyclerAdapter;
    private SearchesDataSource datasource;
    private List<Products> productsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        rview = findViewById(R.id.recyclerView);
        datasource = new SearchesDataSource(this);
        datasource.open();
        SearchView search =  findViewById(R.id.searchView);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                query = search.getQuery().toString();
                datasource.createSearch_Term(query);
                callApis(query);
                search.setQuery("", false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        recyclerAdapter = new myAdapter(productsList);
    }

    protected void setRecyclerView(){

        recyclerAdapter = new myAdapter(productsList);
        rview.setAdapter(recyclerAdapter);
        rview.setLayoutManager(new LinearLayoutManager(this));
        rview.setHasFixedSize(true);
    }

    protected void callApis(String query){
        //Declare URls and call methods (Euro Based)
        ebayUrl ="https://ebay-products-search-scraper.p.rapidapi.com/products?query=" + query + "&page=1&Item_Location=europe";
        amazonUrl = "https://amazon-deutschland-data-scraper.p.rapidapi.com/search/" + query + "?api_key=7c3c12edf5e0523209099e036c847ef1";
        aliExpressUrl = "https://magic-aliexpress1.p.rapidapi.com/api/products/search?name=" + query + "&sort=SALE_PRICE_ASC&page=1&targetCurrency=EUR&lg=en";

        productsList = new ArrayList<>();

        searchEbay(ebayUrl,productsList);
        searchAmazon(amazonUrl,productsList);
        searchAliExpress(aliExpressUrl,productsList);
    }

    protected void searchEbay(String url, List<Products> productsList) {
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

                                productsList.add(new Products(""+ name,"" + link,"€" + price));


                                counter++;

                                setRecyclerView();

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

    protected void searchAmazon(String url, List<Products> productsList){
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


                                productsList.add(new Products(""+ name,"€" + price,"" + link));

                                counter++;

                                setRecyclerView();

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

    protected void searchAliExpress(String url, List<Products> productsList){
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


                                productsList.add(new Products(""+ name,"€" + price,"" + link));

                                counter++;

                                setRecyclerView();
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
        Intent showHome= new Intent(this, MainActivity.class);
        startActivity(showHome);
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