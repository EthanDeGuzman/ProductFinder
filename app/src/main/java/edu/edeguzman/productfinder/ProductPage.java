package edu.edeguzman.productfinder;

import androidx.appcompat.app.AppCompatActivity;

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

    public void callBack(View view) {
        finish();
    }

}