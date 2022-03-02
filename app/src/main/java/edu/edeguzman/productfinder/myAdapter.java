package edu.edeguzman.productfinder;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.MyViewHolder> {

    List<Products> productsList;
    private RecyclerViewClickListener listener;

    public myAdapter(List<Products> productsList, RecyclerViewClickListener listener) {
        this.productsList = productsList;
        this.listener= listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Products products = productsList.get(position);

        holder.myText1.setText(products.getpName());
        holder.myText2.setText(products.getpPrice());
        holder.myText3.setText(products.getpLink());

        boolean isExpandable = productsList.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView myText1, myText2, myText3;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.myNameView);
            myText2 = itemView.findViewById(R.id.myPriceView);
            myText3 = itemView.findViewById(R.id.myLinkView);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);

            itemView.setOnClickListener(this);

            //linearLayout.setOnClickListener(new View.OnClickListener() {
            //    @Override
             //   public void onClick(View v) {
            //        Products products = productsList.get(getAdapterPosition());
             //       products.setExpandable(!products.isExpandable());
            //        notifyItemChanged(getAdapterPosition());
            //    }
          //  });

        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }


    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }



}
