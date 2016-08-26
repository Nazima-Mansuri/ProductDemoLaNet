package com.example.lcom67.productdemoapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;


public class GalleryViewDemo extends AppCompatActivity
{
    Gallery gallery ;
    ImageView image1;
    Integer[] GalleryImagesList = {R.drawable.profile_icon,R.drawable.profile_image,R.drawable.ic_group,R.drawable.lock};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view_demo);

        gallery = (Gallery) findViewById(R.id.gallery1);
        image1 = (ImageView) findViewById(R.id.image1);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                    image1.setImageResource(GalleryImagesList[i]);
            }
        });
    }

    private class ImageAdapter extends BaseAdapter {
        public ImageAdapter(Context context) {
        }

        @Override
        public int getCount() {
            return GalleryImagesList.length;
        }

        @Override
        public Object getItem(int i) {
            return GalleryImagesList[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView = new ImageView(GalleryViewDemo.this);
            imageView.setImageResource(GalleryImagesList[i]);
            imageView.setLayoutParams(new Gallery.LayoutParams(200, 300));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            return imageView;
        }
    }
}
