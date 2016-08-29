package com.example.lcom67.productdemoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lcom67.productdemoapp.AdapterClass.MyAdapter;
import com.example.lcom67.productdemoapp.AsyncTaskClass.GetPostMethodClass;
import com.example.lcom67.productdemoapp.Beans.Contacts;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.R.attr.data;

public class ListOfProductActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    String URL = "http://192.168.200.64:4000/";
    ImageView profileImage;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    TextView usename;
    private Toolbar toolbar;
    private long mBackPressed;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private MyAdapter myAdapter;
    List<Contacts> contactList;
    int sign_id;
    JSONObject jsonObject;
    JSONArray jsonArray;
    GetPostMethodClass getPostMethodClass;
    String selectusernameURL = URL + "profile/selectusername";
    String str_username, str_profileImage, headerImage;
    String deleteProductURL = URL + "product/deleteproduct";
    private int product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_product);
        jsonObject = new JSONObject();
        contactList = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("List of Product");
        setSupportActionBar(toolbar);

        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        sign_id = prefs.getInt("signup_id", 0);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        if (isTablet(this)) {

            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(ListOfProductActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", contactList.get(position).getId());
                Log.d("Message ", " PRODUCT ID : " + contactList.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ListOfProductActivity.this);
                alert.setTitle("Alert!!");
                alert.setMessage("Are you sure to delete Note ?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        product_id = contactList.get(position).getId();

                        try {
                            jsonObject.put("product_id", product_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        new DeleteOperation().execute(deleteProductURL);
                        contactList.remove(position);
                        dialogInterface.dismiss();
                        myAdapter.notifyDataSetChanged();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.show();
            }
        }));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.header);

        profileImage = (ImageView) headerView.findViewById(R.id.header_profile_image);
        usename = (TextView) headerView.findViewById(R.id.header_username);

        profileImage.setOnClickListener(this);

        try {
            jsonObject.put("signup_id", sign_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new LongOperation().execute(selectusernameURL);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 111) {
            headerImage =data.getStringExtra("profile_image");
            Picasso.with(ListOfProductActivity.this)
                    .load(headerImage)
                    .into(profileImage);
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.header_profile_image) {

        } else if (id == R.id.fab)
        {
            Intent intent = new Intent(ListOfProductActivity.this, AddProductActivity.class);
            startActivity(intent);
        }

    }

    public static boolean isTablet(Context ctx) {
        return (ctx.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_Logout)
        {
            Intent intent = new Intent(ListOfProductActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.nav_Profile)
        {
            Intent intent= new Intent(ListOfProductActivity.this,ProfileDetailActivity.class);
            startActivityForResult(intent, 111);
        }
        return false;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        String data;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(ListOfProductActivity.this);

            dialog.setMessage("Please Wait..");
            dialog.show();

            data = jsonObject.toString();

            Log.d("Message", " DATA : " + data);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            getPostMethodClass = new GetPostMethodClass();
            String result = getPostMethodClass.sendPostRequest(selectusernameURL, data);

            Log.d("Message", "RESULT : " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            if (dialog.isShowing())
                dialog.dismiss();

            try {
                JSONObject json = new JSONObject(s);

                str_username = json.getString("username");
                str_profileImage = json.getString("profile_image");

                usename.setText(str_username);

                Picasso.with(ListOfProductActivity.this)
                        .load(str_profileImage)
                        .placeholder(R.drawable.profile_image)
                        .into(profileImage);


                jsonArray = json.getJSONArray("productlist");

                Log.d("Message", " LIST : " + json.getJSONArray("productlist"));
                Log.d("Message", " userNM : " + str_username);


                if (json.getString("status").equals("1")) {
                    for (int i = 0; i < jsonArray.length(); i++) {

                        Contacts contacts = new Contacts();

                        JSONObject jsonProduct = jsonArray.getJSONObject(i);
                        contacts.setProductName(jsonProduct.getString("PRODUCT_NAME"));

                        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                        String price = formatter.format( Float.parseFloat(jsonProduct.getString("PRODUCT_PRICE")));

                        contacts.setProductPrice(price);
                        contacts.setDescription(jsonProduct.getString("PRODUCT_DESCRIPTION"));
                        contacts.setImagePath(jsonProduct.getString("PRODUCT_DEFAULT_IMAGE"));
                        contacts.setId(jsonProduct.getInt("PRODUCT_ID"));

                        contactList.add(contacts);
                        Log.d("Message ", "CONTACT LIST : " + contactList);
                    }

                    myAdapter = new MyAdapter(ListOfProductActivity.this, contactList);
                    recyclerView.setAdapter(myAdapter);

                } else if (json.getString("status").equals("update")) {
                    for (int i = 0; i < jsonArray.length(); i++) {

                        Contacts contacts = new Contacts();

                        JSONObject jsonProduct = jsonArray.getJSONObject(i);
                        contacts.setProductName(jsonProduct.getString("PRODUCT_NAME"));

                        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                        String price = formatter.format( Float.parseFloat(jsonProduct.getString("PRODUCT_PRICE")));

                        contacts.setProductPrice(price);
                        contacts.setDescription(jsonProduct.getString("PRODUCT_DESCRIPTION"));
                        contacts.setId(jsonProduct.getInt("PRODUCT_ID"));

                        contactList.add(contacts);
                        Log.d("Message ", "CONTACT LIST : " + contactList);
                    }

                    myAdapter = new MyAdapter(ListOfProductActivity.this, contactList);
                    recyclerView.setAdapter(myAdapter);

                } else if (json.getString("status").equals("0")) {
                    Toast.makeText(ListOfProductActivity.this, " No Product Available..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ListOfProductActivity.this, " No User Exist..", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class DeleteOperation extends AsyncTask<String, Void, String> {
        String data;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(ListOfProductActivity.this);

            dialog.setMessage("Please Wait..");
            dialog.show();

            data = jsonObject.toString();

            Log.d("Message", " DATA : " + data);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            getPostMethodClass = new GetPostMethodClass();
            String result = getPostMethodClass.sendPostRequest(deleteProductURL, data);

            Log.d("Message", "RESULT : " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }
}

class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
    private GestureDetector gestureDetector;
    private ListOfProductActivity.ClickListener clickListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ListOfProductActivity.ClickListener clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }
            }

        });
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, rv.getChildPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
