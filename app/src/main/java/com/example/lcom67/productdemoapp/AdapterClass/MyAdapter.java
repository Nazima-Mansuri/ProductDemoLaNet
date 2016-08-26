package com.example.lcom67.productdemoapp.AdapterClass;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lcom67.productdemoapp.Beans.Contacts;
import com.example.lcom67.productdemoapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by lcom67 on 5/8/16.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
{
    private List<Contacts> contactsList;
    Context context;
    private AdapterView.OnItemClickListener mItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView productName,productPrice,description;
        public ImageView Image,delete;
        public CardView mCardView;
        RelativeLayout rl_main;

        public MyViewHolder(View view)
        {
            super(view);
            mCardView = (CardView) view.findViewById(R.id.card_view);
            productName = (TextView) view.findViewById(R.id.list_pproduct_name);
            productPrice = (TextView) view.findViewById(R.id.list_product_price);
            description = (TextView) view.findViewById(R.id.list_product_description);
            Image = (ImageView) view.findViewById(R.id.list_product_image);
        }


    }

    public MyAdapter(Context context,List<Contacts> contactsList)
    {
        this.context = context;
        this.contactsList = contactsList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        //inflate your layout and pass it to view holder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {

        Contacts contacts =  contactsList.get(position);

        holder.productName.setText(contacts.getProductName());
        holder.productPrice.setText(contacts.getProductPrice() + "");
        holder.description.setText(contacts.getDescription());
        Picasso.with(context)
                .load(contacts.getImagePath())
                .placeholder(R.drawable.profile_image)
                .into(holder.Image);
//        holder.Image.setImageResource(contacts.getImagePath());

    }

    @Override
    public int getItemCount()
    {
        return contactsList.size();
    }

    public void setOnItemClickListener(final AdapterView.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void remove(int position)
    {
        contactsList.remove(position);
        notifyItemRemoved(position);
    }

}
