package edu.edeguzman.productfinder;

public class Searches {
    private long id;
    private String search_term;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSearchTerm() {
        return search_term;
    }

    public void setSearchTerm(String search_term) {
        this.search_term = search_term;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return search_term;
    }

}
