package edu.edeguzman.productfinder;

import android.content.Intent;
import android.view.View;

public class Products {

    private String pName, pLink, pPrice, pImage;
    private boolean expandable;

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public Products(String pName, String pLink, String pPrice, String pImage) {
        this.pName = pName;
        this.pLink = pLink;
        this.pPrice = pPrice;
        this.pImage = pImage;
        this.expandable = false;
    }

    public String getpName() {
        return pName;
    }

    public String getpLink() {
        return pLink;
    }

    public String getpPrice() {
        return pPrice;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public void setpLink(String pLink) {
        this.pLink = pLink;
    }

    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
    }

    public void setpImage(String pImage) { this.pImage = pImage; }

    @Override
    public String toString() {
        return "Products{" +
                " pName='" + pName + '\'' +
                ", pLink='" + pLink + '\'' +
                ", pPrice='" + pPrice + '\'' +
                ", pImage='" + pImage + '\'' +
                '}';
    }



}
