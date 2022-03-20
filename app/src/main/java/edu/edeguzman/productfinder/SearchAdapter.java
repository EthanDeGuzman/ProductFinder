package edu.edeguzman.productfinder;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends BaseAdapter {

    Context context;
    ArrayList<SearchTerms> arrayList;
    DatabaseHelper db;

    public SearchAdapter(Context context, ArrayList<SearchTerms> arrayList) {
        this.context=context;
        this.arrayList=arrayList;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public Object getItem(int position){
        return arrayList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.search_history_item,null);
        TextView Tv_term = (TextView) convertView.findViewById(R.id.ItemNameView);
        ImageView Iv_delete = (ImageView) convertView.findViewById((R.id.ItemImageView));

        db = new DatabaseHelper(context);
        SearchTerms searchterms = arrayList.get(position);

        String search = searchterms.searchTerm;
        String id = searchterms.id;

        Tv_term.setText(search);

        Tv_term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callResults(search);
            }
        });

        Iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                .setTitle("Delete entry")
                .setMessage("Remove this search term from your history?")

                .setPositiveButton("yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                       db.deleteSearchTerm(id);
                       notifyDataSetChanged();

                        Intent showHistory = new Intent(context, SearchHistory.class);
                        showHistory.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(showHistory);
                    }
                })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    private void callResults(String query){
        Intent showResults = new Intent(context, Results.class);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle();
        showResults.putExtra("query", query);
        context.startActivity(showResults, b);
    }
}
