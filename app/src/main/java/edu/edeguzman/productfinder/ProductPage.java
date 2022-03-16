package edu.edeguzman.productfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProductPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        TextView productNameTV = findViewById(R.id.ProductTitle);
        TextView productPriceTV = findViewById(R.id.ProductPrice);
        ImageView productImageIV = findViewById(R.id.ProductImage);
        TextView webSellerLink = findViewById(R.id.SellerWebLink);

      String linkText="";


        webSellerLink.setClickable(true);
        webSellerLink.setMovementMethod(LinkMovementMethod.getInstance());


        String pName ="";
        String pPrice ="";
        String pLink ="";
        String pImage="";

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            pName = extras.getString("ProductName");
            pPrice = extras.getString("ProductPrice");
            pLink = extras.getString("ProductLink");
            pImage = extras.getString("ProductImage");
        }



        productNameTV.setText(pName);
        productPriceTV.setText(pPrice);
        Picasso.get().load(pImage).into(productImageIV);




        linkText ="<a href='" + pLink + "'>Go to Seller Website:</a>";
        webSellerLink.setText(Html.fromHtml(linkText, Html.FROM_HTML_MODE_COMPACT));


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

    public void callBack(View view) {
        finish();
    }



}