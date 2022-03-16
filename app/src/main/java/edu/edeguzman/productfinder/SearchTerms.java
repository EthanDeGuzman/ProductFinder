package edu.edeguzman.productfinder;

public class SearchTerms {
    String id;
    String searchTerm;

    public SearchTerms(String id, String SearchTerm) {
        this.id = id;
        this.searchTerm = SearchTerm;
    }

    public SearchTerms(){}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

}
