package edu.edeguzman.productfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class Results extends AppCompatActivity {
    private String ebayUrl, amazonUrl, aliExpressUrl, query;
    private RequestQueue queue;
    private String s1[], s2[], s3[];
    private List<Products> productsList;
    private RecyclerView rview;
    private int counter=1;
    private myAdapter recyclerAdapter;
    private myAdapter.RecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        rview = findViewById(R.id.recyclerView);

        s1 = getResources().getStringArray(R.array.ProductName);
        s2 = getResources().getStringArray(R.array.ProductPrice);
        s3 = getResources().getStringArray(R.array.ProductLink);

        Bundle extras = getIntent().getExtras();
        query = extras.getString("query");

        //Declare URls and call methods (Euro Based)
        ebayUrl ="https://ebay-products-search-scraper.p.rapidapi.com/products?query=" + query + "&page=1&Item_Location=europe";
        amazonUrl = "https://amazon-deutschland-data-scraper.p.rapidapi.com/search/" + query + "?api_key=7c3c12edf5e0523209099e036c847ef1";
        aliExpressUrl = "https://magic-aliexpress1.p.rapidapi.com/api/products/search?name=" + query + "&sort=SALE_PRICE_ASC&page=1&targetCurrency=EUR&lg=en";

        getData(ebayUrl, amazonUrl, aliExpressUrl);
        setRecyclerView();
    }

    protected void setRecyclerView(){
        setOnClickListener();
        recyclerAdapter = new myAdapter(productsList,listener);
        rview.setAdapter(recyclerAdapter);
        rview.setLayoutManager(new LinearLayoutManager(this));
        rview.setHasFixedSize(true);
    }

    private void setOnClickListener() {
        listener = new myAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent showProductPage = new Intent(getApplicationContext(),ProductPage.class);
                showProductPage.putExtra("ProductName", productsList.get(position).getpName());
                showProductPage.putExtra("ProductPrice", productsList.get(position).getpPrice());
                showProductPage.putExtra("ProductLink", productsList.get(position).getpLink());
                showProductPage.putExtra("ProductImage", productsList.get(position).getpImage());
                startActivity(showProductPage);
            }
        };
    }

    protected void getData(String ebayUrl, String amazonUrl, String aliExpressUrl) {
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
                            if (response == null || response == "" || response.isEmpty()){
                                productsList.add(new Products("No Results Found","","",""));

                                setRecyclerView();
                            }
                            else{
                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("products");

                                for (int i = 0; i < 5; i++) {
                                    JSONObject products = array.getJSONObject(i);

                                    String name = products.getString("title");
                                    String price = products.getString("price");
                                    String link = products.getString("productLink");
                                    String image = products.getString("image");


                                    productsList.add(new Products("" + name, "" + link, "€" + price, "" + image));

                                    counter++;

                                    rview.setAdapter(recyclerAdapter);

                                }
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
                            if (response == null || response == "" || response.isEmpty()){
                                productsList.add(new Products("No Results Found","","",""));

                                setRecyclerView();
                            }
                            else{
                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("results");

                                for(int i = 0; i < 5; i++){
                                    JSONObject products = array.getJSONObject(i);

                                    String name = products.getString("name");
                                    String price = products.getString("price");
                                    String link = products.getString("url");
                                    String image = products.getString("image");


                                    productsList.add(new Products("" + name, "" + link, "€" + price, "" + image));

                                    counter++;

                                    rview.setAdapter(recyclerAdapter);

                                }
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
                            if (response == null || response == "" || response.isEmpty()){
                                productsList.add(new Products("No Results Found","","",""));

                                setRecyclerView();
                            }
                            else{
                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("docs");

                                for(int i = 0; i < 5; i++){
                                    JSONObject data = array.getJSONObject(i);

                                    String name = data.getString("product_title");
                                    String price = data.getString("app_sale_price");
                                    String link = data.getString("product_detail_url");
                                    String image = data.getString("product_main_image_url");

                                    s1[counter] = name;
                                    s2[counter] = "€" + price;
                                    s3[counter] = link;

                                    productsList.add(new Products("" + name, "" + link, "€" + price, "" + image));

                                    counter++;

                                    rview.setAdapter(recyclerAdapter);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Something went wrong in Ali Express API", Toast.LENGTH_SHORT).show();
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