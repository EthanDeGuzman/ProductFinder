package edu.edeguzman.productfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Results extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String ebayUrl, amazonUrl, aliExpressUrl, query;
    private RequestQueue queue;

    private List<Products> EbayProducts;
    private List<Products> AmazonProducts;
    private List<Products> AliExpressProducts;
    private ArrayList<List> AllProducts;
    private ArrayList<List> LoadingProducts;

    private ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    ExpandableListAdapter expandableListAdapterLoading;

    private List<String> ParentList = new ArrayList<>();
    {
        ParentList.add("E-Bay");
        ParentList.add("Amazon");
        ParentList.add("Ali-Express");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        expandableListView = findViewById(R.id.expandListView1);

        Bundle extras = getIntent().getExtras();
        query = extras.getString("query");

        //Declare URls and call methods (Euro Based)
        ebayUrl ="https://ebay-products-search-scraper.p.rapidapi.com/products?query=" + query + "&page=1&Item_Location=europe";
        amazonUrl ="https://amazon24.p.rapidapi.com/api/product?keyword=" + query + "&country=DE&page=1";
        aliExpressUrl = "https://magic-aliexpress1.p.rapidapi.com/api/products/search?name=" + query + "&sort=SALE_PRICE_ASC&page=1&targetCurrency=EUR&lg=en";

        //Loading code
        LoadingProducts = new ArrayList<>();
        List<Products> LoadingProductsChild = new ArrayList<>();
        {
            LoadingProductsChild.add(new Products("Fetching Results...","","",""));
        }

        LoadingProducts.add(LoadingProductsChild);
        LoadingProducts.add(LoadingProductsChild);
        LoadingProducts.add(LoadingProductsChild);

        SetLoadingIndicator();

        //Calls API
        getData(ebayUrl, amazonUrl, aliExpressUrl);

        //Dropdown for sorting
        Spinner spinner = (Spinner) findViewById(R.id.SortFilters);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.SortFilters, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        AllProducts = new ArrayList<>();
        AllProducts.add(EbayProducts);
        AllProducts.add(AmazonProducts);
        AllProducts.add(AliExpressProducts);

        expandableListAdapter = new ExListAdapter(Results.this, AllProducts, ParentList);
    }

    public void SetLoadingIndicator()
    {
        expandableListAdapterLoading = new ExListAdapter(Results.this, LoadingProducts, ParentList);
        expandableListView.setAdapter(expandableListAdapterLoading);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selected = adapterView.getItemAtPosition(i).toString();
        switch(selected){
            case "Sort By:":
                break;
            case "Name":
                sortName();
                expandableListView.setAdapter(expandableListAdapter);
                break;
            case "Price Ascending":
                sortPriceAscending();
                expandableListView.setAdapter(expandableListAdapter);
                break;
            case "Price Descending":
                sortPriceDescending();
                expandableListView.setAdapter(expandableListAdapter);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    protected void getData(String ebayUrl, String amazonUrl, String aliExpressUrl) {
        EbayProducts = new ArrayList<>();
        AmazonProducts = new ArrayList<>();
        AliExpressProducts = new ArrayList<>();

        searchEbay(ebayUrl,EbayProducts);
        searchAmazon(amazonUrl,AmazonProducts);
        searchAliExpress(aliExpressUrl,AliExpressProducts);
    }



    protected void searchEbay(String url, List<Products> EbayProducts) {
        //Replace all Spaces in the Url with %20 for query
        url = url.replaceAll(" ", "%20");

        queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response == null || response == "" || response.isEmpty()){
                                EbayProducts.add(new Products("No Results Found","","",""));

                                expandableListView.setAdapter(expandableListAdapter);
                            }
                            else{
                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("products");

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject products = array.getJSONObject(i);

                                    String name = products.getString("title");
                                    String price = products.getString("price");
                                    String link = products.getString("productLink");
                                    String image = products.getString("image");

                                    String splitPrice[] = price.split(" ");
                                    for (int x = 0; x < splitPrice.length; x++) {

                                        if (!splitPrice[x].equals("to")){
                                            //Get rid of Dollar Sign
                                            splitPrice[x] = splitPrice[x].replace("$", "");
                                            splitPrice[x] = splitPrice[x].replaceAll(",", "");
                                            double tempPrice = Double.parseDouble(splitPrice[x]);

                                            //Change price to euro
                                            tempPrice = tempPrice * 0.88;

                                            //Update array and format to 2 decimal places
                                            splitPrice[x] = String.valueOf(tempPrice);
                                            splitPrice[x] = String.format("%.2f", new BigDecimal(splitPrice[x]));

                                            price = splitPrice[x];
                                        }
                                    }

                                    EbayProducts.add(new Products("" + name, "" + link, "€" + price, "" + image));



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
                                AmazonProducts.add(new Products("No Results Found","","",""));

                                expandableListView.setAdapter(expandableListAdapter);
                            }
                            else{
                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("docs");

                                //Amazon API sometimes responds with a result but empty
                                if (array.length() <= 0){
                                    AmazonProducts.add(new Products("No Results Found","","",""));
                                    expandableListView.setAdapter(expandableListAdapter);
                                }
                                else{
                                    for(int i = 0; i < array.length(); i++){
                                        JSONObject products = array.getJSONObject(i);

                                        String name = products.getString("product_title");
                                        String price = products.getString("app_sale_price");
                                        String link = products.getString("product_detail_url");
                                        String image = products.getString("product_main_image_url");

                                        if(price == "null"){
                                            price = "0";
                                        }

                                        price = price.replaceAll(",", ".");

                                        AmazonProducts.add(new Products("" + name, "" + link, "€" + price, "" + image));
                                    }
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
                headers.put("x-rapidapi-host", "amazon24.p.rapidapi.com");
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
                                AliExpressProducts.add(new Products("No Results Found","","",""));

                                expandableListView.setAdapter(expandableListAdapter);
                            }
                            else{
                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("docs");

                                for(int i = 0; i < array.length(); i++){
                                    JSONObject data = array.getJSONObject(i);

                                    String name = data.getString("product_title");
                                    String price = data.getString("app_sale_price");
                                    String link = data.getString("product_detail_url");
                                    String image = data.getString("product_main_image_url");

                                    link = link.replaceFirst("//", "https://");
                                    image = image.replaceFirst("https:https", "https");

                                    AliExpressProducts.add(new Products("" + name, "" + link, "€" + price, "" + image));
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

    public void sortName(){
        Collections.sort(EbayProducts, new Comparator<Products>() {
            @Override
            public int compare(Products products, Products t1) {
                return products.getpName().compareTo(t1.getpName());
            }
        });
        Collections.sort(AmazonProducts, new Comparator<Products>() {
            @Override
            public int compare(Products products, Products t1) {
                return products.getpName().compareTo(t1.getpName());
            }
        });
        Collections.sort(AliExpressProducts, new Comparator<Products>() {
            @Override
            public int compare(Products products, Products t1) {
                return products.getpName().compareTo(t1.getpName());
            }
        });
    }

    public void sortPriceAscending(){
        Collections.sort(EbayProducts, new Comparator<Products>() {
            @Override
            public int compare(Products products, Products t1) {
                String tempPrice1 = t1.getpPrice();
                String tempPrice2 = products.getpPrice();
                tempPrice1 = tempPrice1.replaceAll("€", "");
                tempPrice2 = tempPrice2.replaceAll("€", "");

                float f1 = Float.parseFloat(tempPrice1);
                float f2 = Float.parseFloat(tempPrice2);

                return Float.compare(f2, f1);
            }
        });

        Collections.sort(AmazonProducts, new Comparator<Products>() {
            @Override
            public int compare(Products products, Products t1) {
                String tempPrice1 = t1.getpPrice();
                String tempPrice2 = products.getpPrice();
                tempPrice1 = tempPrice1.replaceAll("€", "");
                tempPrice2 = tempPrice2.replaceAll("€", "");

                float f1 = Float.parseFloat(tempPrice1);
                float f2 = Float.parseFloat(tempPrice2);

                return Float.compare(f2, f1);
            }
        });

        Collections.sort(AliExpressProducts, new Comparator<Products>() {
            @Override
            public int compare(Products products, Products t1) {
                String tempPrice1 = t1.getpPrice();
                String tempPrice2 = products.getpPrice();
                tempPrice1 = tempPrice1.replaceAll("€", "");
                tempPrice2 = tempPrice2.replaceAll("€", "");

                float f1 = Float.parseFloat(tempPrice1);
                float f2 = Float.parseFloat(tempPrice2);

                return Float.compare(f2, f1);
            }
        });
    }

    public void sortPriceDescending(){
        Collections.sort(EbayProducts, new Comparator<Products>() {
            @Override
            public int compare(Products products, Products t1) {
                String tempPrice1 = t1.getpPrice();
                String tempPrice2 = products.getpPrice();
                tempPrice1 = tempPrice1.replaceAll("€", "");
                tempPrice2 = tempPrice2.replaceAll("€", "");

                float f1 = Float.parseFloat(tempPrice1);
                float f2 = Float.parseFloat(tempPrice2);

                return Float.compare(f1, f2);
            }
        });

        Collections.sort(AmazonProducts, new Comparator<Products>() {
            @Override
            public int compare(Products products, Products t1) {
                String tempPrice1 = t1.getpPrice();
                String tempPrice2 = products.getpPrice();
                tempPrice1 = tempPrice1.replaceAll("€", "");
                tempPrice2 = tempPrice2.replaceAll("€", "");

                float f1 = Float.parseFloat(tempPrice1);
                float f2 = Float.parseFloat(tempPrice2);

                return Float.compare(f1, f2);
            }
        });

        Collections.sort(AliExpressProducts, new Comparator<Products>() {
            @Override
            public int compare(Products products, Products t1) {
                String tempPrice1 = t1.getpPrice();
                String tempPrice2 = products.getpPrice();
                tempPrice1 = tempPrice1.replaceAll("€", "");
                tempPrice2 = tempPrice2.replaceAll("€", "");

                float f1 = Float.parseFloat(tempPrice1);
                float f2 = Float.parseFloat(tempPrice2);

                return Float.compare(f1, f2);
            }
        });
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
        startActivity(showImageScanner,b);
    }

}