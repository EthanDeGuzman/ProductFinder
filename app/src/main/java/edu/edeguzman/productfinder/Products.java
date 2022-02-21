package edu.edeguzman.productfinder;

public class Products {

    private String pName, pLink, pPrice;
    private boolean expandable;

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public Products(String pName, String pLink, String pPrice) {
        this.pName = pName;
        this.pLink = pLink;
        this.pPrice = pPrice;
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

    public void setpName(String pName) {
        this.pName = pName;
    }

    public void setpLink(String pLink) {
        this.pLink = pLink;
    }

    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
    }

    @Override
    public String toString() {
        return "Products{" +
                "pName='" + pName + '\'' +
                ", pLink='" + pLink + '\'' +
                ", pPrice='" + pPrice + '\'' +
                '}';
    }
}
