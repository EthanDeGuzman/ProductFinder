package edu.edeguzman.productfinder;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ExListAdapter  extends BaseExpandableListAdapter {

    private Context context;
    private List<List> productLists;
    private List<String> parentList;


    ExListAdapter(Context context, List<List> ProductLists, List<String> ParentList)
    {
        this.context = context;
        this.productLists = ProductLists;
        this.parentList = ParentList;
    }

    @Override
    public int getGroupCount() {
       return parentList.size();
    }

    @Override
    public Object getGroup(int i) {
        return parentList.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.parent_row,null);
        }

        ImageView icon = view.findViewById(R.id.iconImage);
        if(b)
        {
            icon.setImageResource(R.drawable.whiteuparrow);
        }
        else
        {
            icon.setImageResource(R.drawable.whitedownarrow);
        }

        TextView item = view.findViewById(R.id.myPNameView);
        item.setText(parentList.get(i));

      return view;
    }

    @Override
    public int getChildrenCount(int i) {
        return productLists.get(i).size();
    }

    @Override
    public Object getChild(int i, int i1) {
        return productLists.get(i).get(i1);
    }

    @Override
    public long getChildId(int i, int i1) { return i1; }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final Object productinfo = productLists.get(i).get(i1);
        Products pInfoStrings = (Products) (productinfo);

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.child_row, null);

            }


        TextView itemName = view.findViewById(R.id.myCNameView);
        TextView itemPrice = view.findViewById(R.id.myCPriceView);

        if (pInfoStrings.getpName().equals("Fetching Results...")) {
            itemName.setTextSize(24);
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {

                int count = 0;

                @Override
                public void run() {
                    count++;

                    if (count == 1) {
                        itemName.setText("Fetching Results.");
                        itemPrice.setText("");
                    } else if (count == 2) {
                        itemName.setText("Fetching Results..");
                        itemPrice.setText("");
                    } else if (count == 3) {
                        itemName.setText("Fetching Results...");
                        itemPrice.setText("");
                    }

                    if (count == 3)
                        count = 0;

                    handler.postDelayed(this, 1 * 1000);
                }
            };
            handler.postDelayed(runnable, 0);
        }//end if
        else{
            itemName.setTextSize(16);
            itemName.setText(pInfoStrings.getpName());
            itemPrice.setText(pInfoStrings.getpPrice());
        }



        LinearLayout productTab = view.findViewById(R.id.ChildLayout);

        productTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pInfoStrings.getpName().equals("No Results Found") && !pInfoStrings.getpName().equals("Fetching Results...")) {
                    Intent intent = new Intent(context, ProductPage.class);
                    Bundle b = ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle();
                    intent.putExtra("ProductName", pInfoStrings.getpName());
                    intent.putExtra("ProductLink", pInfoStrings.getpLink());
                    intent.putExtra("ProductPrice", pInfoStrings.getpPrice());
                    intent.putExtra("ProductImage", pInfoStrings.getpImage());
                    context.startActivity(intent, b);
                }
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
