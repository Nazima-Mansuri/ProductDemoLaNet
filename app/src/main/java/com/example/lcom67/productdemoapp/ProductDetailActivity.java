package com.example.lcom67.productdemoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lcom67.productdemoapp.AsyncTaskClass.GetPostMethodClass;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    TextView productName , productPrice, productDescription ;
    ImageView defaultImage;
    String str_name , str_price , str_description , str_defaultImage;
    private Toolbar toolbar;
    int product_id = -1;
    JSONObject jsonObject;
    String productDetailURL = "http://192.168.200.64:4000/product/productdetail";
    private GetPostMethodClass getPostMethodClass;
    FloatingActionButton fabEdit;
    private JSONArray jsonArray;
    ArrayList<String> imageList;
    Gallery gallery;
    LinearLayout mDotsLayout;
    int mDotsCount;
    static TextView mDotsText[];
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        jsonObject = new JSONObject();

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Product Detail");
        setSupportActionBar(toolbar);

        productName = (TextView) findViewById(R.id.detail_product_name);
        productPrice = (TextView) findViewById(R.id.detail_product_price);
        productDescription = (TextView) findViewById(R.id.detail_description);
//        defaultImage = (ImageView) findViewById(R.id.img_detail_product_image);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);


        mDotsLayout = (LinearLayout)findViewById(R.id.image_count);
        //here we count the number of images we have to know how many dots we need

        fabEdit = (FloatingActionButton) findViewById(R.id.fab_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        product_id = getIntent().getIntExtra("product_id" , 0);

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent ii = new Intent(ProductDetailActivity.this, AddProductActivity.class);
                ii.putExtra("product_id", product_id);
                ii.putExtra("product_name",str_name);
                ii.putExtra("product_price",str_price);
                ii.putExtra("product_description",str_description);
                ii.putExtra("product_default_image",str_defaultImage);
                ii.putStringArrayListExtra("product_image_url",imageList);
                ii.putExtra("isUpdate",true);
                startActivity(ii);
            }
        });


        try
        {
            jsonObject.put("product_id",product_id);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        new LongOperation().execute(productDetailURL);
    }

    private class LongOperation  extends AsyncTask<String, Void, String>
    {
        String data;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute()
        {
            dialog = new ProgressDialog(ProductDetailActivity.this);

            dialog.setMessage("Please wait..");
            dialog.show();
            data = jsonObject.toString();

            Log.d("Message"," DATA : " + data);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings)
        {
            getPostMethodClass = new GetPostMethodClass();
            String result = getPostMethodClass.sendPostRequest(productDetailURL,data);

            Log.d("Message" , "RESULT : " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String s)
        {

            super.onPostExecute(s);
            dialog.dismiss();

            try
            {
                JSONObject json = new JSONObject(s);


                if(json.getString("status").equals("1"))
                {
                    str_name = json.getString("product_name");
                    str_price = json.getString("product_price");
                    str_description = json.getString("product_description");
                    str_defaultImage = json.getString("product_default_image");

                    jsonArray = json.getJSONArray("imagelist");
                    imageList = new ArrayList<>();

                    toolbar.setTitle(str_name);

                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                    String price = formatter.format( Float.parseFloat(json.getString("product_price")));

                    productName.setText(str_name);
                    productPrice.setText(price);
                    productDescription.setText(str_description);
                    imageList.add(str_defaultImage);

                   /* Picasso.with(ProductDetailActivity.this)
                            .load(str_defaultImage)
                            .into(defaultImage);*/

                    for(int i=0 ; i<jsonArray.length() ; i++)
                    {
                        JSONObject jsonProduct = jsonArray.getJSONObject(i);

                        imageList.add(jsonProduct.getString("product_image_url"));
                    }

                    mDotsCount = imageList.size();

                    mDotsText = new TextView[mDotsCount];

                    //here we set the dots
                    for (int i = 0; i < mDotsCount; i++) {
                        mDotsText[i] = new TextView(ProductDetailActivity.this);
                        mDotsText[i].setText(".");
                        mDotsText[i].setTextSize(45);
                        mDotsText[i].setTypeface(null, Typeface.BOLD);
                        mDotsText[i].setTextColor(android.graphics.Color.GRAY);
                        mDotsLayout.addView(mDotsText[i]);
                    }

                    mViewPager.setAdapter(new ViewPagerAdapter());
                    mViewPager.setOnPageChangeListener(new ViewPageChangeListener());
                    /*gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                        {
                           *//*Picasso.with(ProductDetailActivity.this)
                                   .load(imageList.get(i))
                                   .into(defaultImage);*//*


                            for (int j = 0; j < mDotsCount; j++) {
                                ProductDetailActivity.mDotsText[j]
                                        .setTextColor(Color.GRAY);
                            }

                            ProductDetailActivity.mDotsText[i]
                                    .setTextColor(Color.WHITE);
                        }
                    });*/
                }
                else if(json.getString("status").equals("2"))
                {
                    str_name = json.getString("product_name");
                    str_price = json.getString("product_price");
                    str_description = json.getString("product_description");

                    productName.setText(str_name);
                    productPrice.setText(str_price);
                    productDescription.setText(str_description);


                }
                else
                {
                    Toast.makeText(ProductDetailActivity.this," Something Went Wrong..",Toast.LENGTH_SHORT).show();
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ImageAdapter extends BaseAdapter {
        public ImageAdapter(Context context) {
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public Object getItem(int i) {
            return imageList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            ImageView imageView = new ImageView(ProductDetailActivity.this);
            Picasso.with(ProductDetailActivity.this)
                    .load(imageList.get(i))
                    .into(imageView);
//            imageView.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.300, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setLayoutParams(new Gallery.LayoutParams(150, 150));
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            return imageView;
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View imageViewContainer = inflater.inflate(R.layout.viewpager_single_item, null);
            ImageView imageView = (ImageView) imageViewContainer.findViewById(R.id.image_view);

            Picasso.with(ProductDetailActivity.this)
                    .load(imageList.get(position))
                    .into(imageView);

            ((ViewPager) container).addView(imageViewContainer, 0);
            return imageViewContainer;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView((View)object);
        }
    }

    private class ViewPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            // Here is where you should show change the view of page indicator
            for (int j = 0; j < mDotsCount; j++) {
                ProductDetailActivity.mDotsText[j]
                        .setTextColor(Color.GRAY);
            }

            ProductDetailActivity.mDotsText[position]
                    .setTextColor(Color.BLACK);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
