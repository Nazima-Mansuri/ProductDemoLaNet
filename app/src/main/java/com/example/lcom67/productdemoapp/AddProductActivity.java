package com.example.lcom67.productdemoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lcom67.productdemoapp.AsyncTaskClass.GetPostMethodClass;
import com.example.lcom67.productdemoapp.BottomSheet.BottomSheet;
import com.example.lcom67.productdemoapp.BottomSheet.BottomSheetListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener , BottomSheetListener {
    String URL = "http://192.168.200.64:4000/product/";
    EditText productName, productPrice, description;
    ImageView image1, image2, image3, image4, image5, defaultImage;
    private final String TAG = "AddProductActivity";
    Button submit;
    String str_Name, str_Price, str_description;
    private Toolbar toolbar;
    JSONObject jsonObject;
    private GetPostMethodClass getPostMethodClass;
    ProgressDialog dialog;
    String productInsertURL = URL + "insert";
    String productUpdateURL = URL + "updateproduct";
    String imageUploadURL = URL + "productimages";
    String deleteImageURL = URL + "deleteproductimage";
    String updateDefaultImage = URL + "updatedefaultimage";

    int sign_id;
    int product_id;
    String setProductName, setProductPrice, setDescription;
    String strImageURL;
    ArrayList<String> imageList;
    private Bitmap bitmap;
    int PICK_IMAGE_MULTIPLE = 0, PICK_IMAGE1 = 1, PICK_IMAGE2 = 2, PICK_IMAGE3 = 3, PICK_IMAGE4 = 4, PICK_IMAGE5 = 5;
    String defaultPath, image1Path, image2Path, image3Path, image4Path, image5Path;
    List<String> pathList;
    Boolean isUpdate;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        jsonObject = new JSONObject();

        pathList = new ArrayList<>();
        imageList = new ArrayList<>();

        dialog = new ProgressDialog(AddProductActivity.this);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Add Product");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        sign_id = prefs.getInt("signup_id", 0);

        productName = (EditText) findViewById(R.id.et_product_name);
        productPrice = (EditText) findViewById(R.id.et_product_price);
        description = (EditText) findViewById(R.id.et_product_description);
        submit = (Button) findViewById(R.id.btn_add_product);
        defaultImage = (ImageView) findViewById(R.id.img_add_product_image);
        image1 = (ImageView) findViewById(R.id.product_image1);
        image2 = (ImageView) findViewById(R.id.product_image2);
        image3 = (ImageView) findViewById(R.id.product_image3);
        image4 = (ImageView) findViewById(R.id.product_image4);
        image5 = (ImageView) findViewById(R.id.product_image5);

        submit.setOnClickListener(this);
        defaultImage.setOnClickListener(this);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);
        image5.setOnClickListener(this);

        product_id = getIntent().getIntExtra("product_id", -1);
        setProductName = getIntent().getStringExtra("product_name");
        setProductPrice = getIntent().getStringExtra("product_price");
        setDescription = getIntent().getStringExtra("product_description");
        strImageURL = getIntent().getStringExtra("product_default_image");
        isUpdate = getIntent().getBooleanExtra("isUpdate",false);

        imageList = getIntent().getStringArrayListExtra("product_image_url");

        if (product_id != -1)
        {
            submit.setText("Update");
            productName.setText(setProductName);
            productPrice.setText(setProductPrice);
            description.setText(setDescription);

            Picasso.with(AddProductActivity.this)
                    .load(strImageURL)
                    .placeholder(R.drawable.default_product)
                    .into(defaultImage);

            if (imageList.size() >= 2 && imageList.get(1) != null) {
                Picasso.with(AddProductActivity.this)
                        .load(imageList.get(1))
                        .placeholder(R.drawable.add)
                        .into(image1);
            }
            if (imageList.size() >= 3 && imageList.get(2) != null) {
                Picasso.with(AddProductActivity.this)
                        .load(imageList.get(2))
                        .placeholder(R.drawable.add)
                        .into(image2);
            }
            if (imageList.size() >= 4 && imageList.get(3) != null) {
                Picasso.with(AddProductActivity.this)
                        .load(imageList.get(3))
                        .placeholder(R.drawable.add)
                        .into(image3);
            }
            if (imageList.size() >= 5 && imageList.get(4) != null) {
                Picasso.with(AddProductActivity.this)
                        .load(imageList.get(4))
                        .placeholder(R.drawable.add)
                        .into(image4);
            }
            if (imageList.size() >= 6 && imageList.get(5) != null) {
                Picasso.with(AddProductActivity.this)
                        .load(imageList.get(5))
                        .placeholder(R.drawable.add)
                        .into(image5);
            }

        }

    }

    @Override
    public void onClick(View view) {


        str_Name = productName.getText().toString();
        str_Price = productPrice.getText().toString();
        str_description = description.getText().toString();

        id = view.getId();

        if (id == R.id.btn_add_product)
        {
            if (product_id != -1)
            {
                try {
                    jsonObject.put("product_name", str_Name);
                    jsonObject.put("product_price", str_Price);
                    jsonObject.put("product_description", str_description);
                    jsonObject.put("product_id", product_id);

                    Log.d("Message", " UPDATE DATA : " + jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new UpdateOperation().execute(productUpdateURL);

                if(defaultPath != null) {
                    new UpdateDefaultImageOperation().execute(updateDefaultImage);

                }
                if(pathList.size() !=  0)
                {
                    new ImageOperation().execute(imageUploadURL);

                }
                else
                {
                    Intent intent = new Intent(AddProductActivity.this,ListOfProductActivity.class);
                    startActivity(intent);
                }
            }
            else
            {
                new LongOperation().execute(productInsertURL);
            }

        }
        else if (id == R.id.img_add_product_image)
        {
            if(isUpdate == true)
            {
               /* AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                builder.setTitle("Choose Image Source");
                builder.setItems(new CharSequence[] {"Pick Image From Gallery", "Delete"},
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case 0:

                                        Intent i = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(i, PICK_IMAGE_MULTIPLE);
                                        break;

                                    case 1:
                                        defaultImage.setImageResource(R.drawable.default_product);
                                        break;
                                }
                            }
                        });

                builder.show();*/

                new BottomSheet.Builder(this)
                        .setSheet(R.menu.grid_sheet)
                        .grid()
                        .setTitle("Options")
                        .setListener(this)
                        .show();
            }
            else
            {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_IMAGE_MULTIPLE);
            }

        } else if (id == R.id.product_image1)
        {
            if(isUpdate == true)
            {
                /*AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                builder.setTitle("Choose Image Source");
                builder.setItems(new CharSequence[] {"Pick Image From Gallery", "Delete"},
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case 0:

                                        Intent i = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(i, PICK_IMAGE1);
                                        break;

                                    case 1:
                                        image1.setImageResource(R.drawable.add);
                                        try
                                        {
                                            jsonObject.put("product_image_url",imageList.get(1));
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                        new DeleteImageOperation().execute();
                                        break;
                                }
                            }
                        });

                builder.show();*/

                new BottomSheet.Builder(this)
                        .setSheet(R.menu.grid_sheet)
                        .grid()
                        .setTitle("Options")
                        .setListener(this)
                        .show();
            }
            else
            {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_IMAGE1);
            }

        } else if (id == R.id.product_image2) {
            if(isUpdate == true)
            {
               /* AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                builder.setTitle("Choose Image Source");
                builder.setItems(new CharSequence[] {"Pick Image From Gallery", "Delete"},
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case 0:

                                        Intent i = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(i, PICK_IMAGE2);
                                        break;

                                    case 1:
                                        image2.setImageResource(R.drawable.add);
                                        try
                                        {
                                            jsonObject.put("product_image_url",imageList.get(2));
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                        new DeleteImageOperation().execute();
                                        break;
                                }
                            }
                        });

                builder.show();*/

                new BottomSheet.Builder(this)
                        .setSheet(R.menu.grid_sheet)
                        .grid()
                        .setTitle("Options")
                        .setListener(this)
                        .show();
            }
            else
            {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_IMAGE2);
            }

        } else if (id == R.id.product_image3) {
            if(isUpdate == true)
            {
              /*  AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                builder.setTitle("Choose Image Source");
                builder.setItems(new CharSequence[] {"Pick Image From Gallery", "Delete"},
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case 0:

                                        Intent i = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(i, PICK_IMAGE3);
                                        break;

                                    case 1:
                                        image3.setImageResource(R.drawable.add);
                                        try
                                        {
                                            jsonObject.put("product_image_url",imageList.get(3));
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                        new DeleteImageOperation().execute();
                                        break;
                                }
                            }
                        });

                builder.show();*/

                new BottomSheet.Builder(this)
                        .setSheet(R.menu.grid_sheet)
                        .grid()
                        .setTitle("Options")
                        .setListener(this)
                        .show();
            }
            else
            {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_IMAGE3);
            }

        } else if (id == R.id.product_image4) {
            if(isUpdate == true)
            {
                /*AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                builder.setTitle("Choose Image Source");
                builder.setItems(new CharSequence[] {"Pick Image From Gallery", "Delete"},
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case 0:

                                        Intent i = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(i, PICK_IMAGE4);
                                        break;

                                    case 1:
                                        image4.setImageResource(R.drawable.add);
                                        try
                                        {
                                            jsonObject.put("product_image_url",imageList.get(4));
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                        new DeleteImageOperation().execute();
                                        break;
                                }
                            }
                        });

                builder.show();*/

                new BottomSheet.Builder(this)
                        .setSheet(R.menu.grid_sheet)
                        .grid()
                        .setTitle("Options")
                        .setListener(this)
                        .show();
            }
            else
            {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_IMAGE4);
            }

        } else if (id == R.id.product_image5) {
            if(isUpdate == true)
            {
                /*AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
                builder.setTitle("Choose Image Source");
                builder.setItems(new CharSequence[] {"Pick Image From Gallery", "Delete"},
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case 0:

                                        Intent i = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(i, PICK_IMAGE5);
                                        break;

                                    case 1:
                                        image5.setImageResource(R.drawable.add);
                                        try
                                        {
                                            jsonObject.put("product_image_url",imageList.get(5));
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                        new DeleteImageOperation().execute();
                                        break;
                                }
                            }
                        });

                builder.show();*/

                new BottomSheet.Builder(this)
                        .setSheet(R.menu.grid_sheet)
                        .grid()
                        .setTitle("Options")
                        .setListener(this)
                        .show();
            }
            else
            {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_IMAGE5);
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                defaultPath = cursor.getString(columnIndex);
                cursor.close();

                defaultImage.setImageBitmap(BitmapFactory.decodeFile(defaultPath));
//                Toast.makeText(this, defaultPath, Toast.LENGTH_LONG).show();
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                defaultImage.setImageBitmap(bitmap);
            }
            super.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == PICK_IMAGE1 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                image1Path = cursor.getString(columnIndex);
                cursor.close();

                image1.setImageBitmap(BitmapFactory.decodeFile(image1Path));
//                Toast.makeText(this,image1Path ,Toast.LENGTH_LONG).show();
                pathList.add(image1Path);
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                image1.setImageBitmap(bitmap);
            }
            super.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == PICK_IMAGE2 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                image2Path = cursor.getString(columnIndex);
                cursor.close();

                image2.setImageBitmap(BitmapFactory.decodeFile(image2Path));
//                Toast.makeText(this,image2Path ,Toast.LENGTH_LONG).show();
                pathList.add(image2Path);
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                image2.setImageBitmap(bitmap);
            }
            super.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == PICK_IMAGE3 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                image3Path = cursor.getString(columnIndex);
                cursor.close();

                image3.setImageBitmap(BitmapFactory.decodeFile(image3Path));
//                Toast.makeText(this,image3Path ,Toast.LENGTH_LONG).show();
                pathList.add(image3Path);
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                image3.setImageBitmap(bitmap);
            }
            super.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == PICK_IMAGE4 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                image4Path = cursor.getString(columnIndex);
                cursor.close();

                image4.setImageBitmap(BitmapFactory.decodeFile(image4Path));
//                Toast.makeText(this,image4Path ,Toast.LENGTH_LONG).show();
                pathList.add(image4Path);
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                image4.setImageBitmap(bitmap);
            }
            super.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == PICK_IMAGE5 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                image5Path = cursor.getString(columnIndex);
                cursor.close();

                image5.setImageBitmap(BitmapFactory.decodeFile(image5Path));
//                Toast.makeText(this,image5Path ,Toast.LENGTH_LONG).show();
                pathList.add(image5Path);
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                image5.setImageBitmap(bitmap);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onSheetShown(@NonNull BottomSheet bottomSheet)
    {

    }

    @Override
    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem item)
    {
            if(item.getItemId() == R.id.gallery)
            {
                switch (id)
                {
                    case R.id.img_add_product_image:
                        Intent i = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, PICK_IMAGE_MULTIPLE);
                        break;
                    case R.id.product_image1:
                        Intent i1 = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i1, PICK_IMAGE1);
                        break;
                    case R.id.product_image2:
                        Intent i2 = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i2, PICK_IMAGE2);
                        break;
                    case R.id.product_image3:
                        Intent i3 = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i3, PICK_IMAGE3);
                        break;
                    case R.id.product_image4:
                        Intent i4 = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i4, PICK_IMAGE4);
                        break;
                    case R.id.product_image5:
                        Intent i5 = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i5, PICK_IMAGE5);
                        break;
                }

            }
            else if(item.getItemId() == R.id.delete)
            {
                switch (id)
                {
                    case R.id.img_add_product_image:
                        defaultImage.setImageResource(R.drawable.default_product);
                        break;
                    case R.id.product_image1:
                        image1.setImageResource(R.drawable.add);
                        try
                        {
                            jsonObject.put("product_image_url",imageList.get(1));
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        new DeleteImageOperation().execute();
                        break;
                    case R.id.product_image2:
                        image2.setImageResource(R.drawable.add);
                        try
                        {
                            jsonObject.put("product_image_url",imageList.get(2));
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        new DeleteImageOperation().execute();
                        break;
                    case R.id.product_image3:
                        image3.setImageResource(R.drawable.add);
                        try
                        {
                            jsonObject.put("product_image_url",imageList.get(3));
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        new DeleteImageOperation().execute();
                        break;
                    case R.id.product_image4:
                        image4.setImageResource(R.drawable.add);
                        try
                        {
                            jsonObject.put("product_image_url",imageList.get(4));
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        new DeleteImageOperation().execute();
                        break;
                    case R.id.product_image5:
                        image5.setImageResource(R.drawable.add);
                        try
                        {
                            jsonObject.put("product_image_url",imageList.get(5));
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        new DeleteImageOperation().execute();
                        break;

                }

            }

    }

    @Override
    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @DismissEvent int dismissEvent)
    {

    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            String result = addFileAndParam(productInsertURL, str_Name, str_Price, str_description, sign_id, defaultPath);

            Log.d("Message", " Result iS :  " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();

            try {
                JSONObject json = new JSONObject(s);

                if (json.getString("status").equals("1")) {
                    Toast.makeText(AddProductActivity.this, " Product Added..", Toast.LENGTH_SHORT).show();
                    product_id = json.getInt("product_id");
                    new ImageOperation().execute(imageUploadURL);


                }
                if (json.getString("status").equals("2")) {
                    Toast.makeText(AddProductActivity.this, " Something Went Wrong.", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateOperation extends AsyncTask<String, Void, String> {
        String data;
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();
            data = jsonObject.toString();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            getPostMethodClass = new GetPostMethodClass();
            String result = getPostMethodClass.sendPostRequest(productUpdateURL, data);

//            String result = updateProductAndParam(productUpdateURL,str_Name,str_Price,str_description,product_id,defaultPath);
            Log.d("Message", " Result iS :  " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();

            try {
                JSONObject json = new JSONObject(s);

                Log.d("Message", " SSSSS : " + json);
                if (json.getString("status").equals("update"))
                {
                    Toast.makeText(AddProductActivity.this, " Product Updated..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddProductActivity.this, ListOfProductActivity.class);
                    finish();
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateDefaultImageOperation extends AsyncTask<String, Void, String>
    {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute()
        {
            dialog = new ProgressDialog(AddProductActivity.this);
            dialog.setMessage("Please wait..");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings)
        {

            String result = updateDefaultImageAndParam(updateDefaultImage, product_id, defaultPath);
            Log.d("Message", " Result iS :  " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            dialog.dismiss();

            try {
                JSONObject json = new JSONObject(s);

                if (json.getString("status").equals("1"))
                {
                    Toast.makeText(AddProductActivity.this, " Image Update..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddProductActivity.this, ListOfProductActivity.class);
                    finish();
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(AddProductActivity.this, " Something Went Wrong", Toast.LENGTH_SHORT).show();
                }

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private class DeleteImageOperation extends AsyncTask<String, Void, String> {
        String data;


        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();

            // Set Request parameter
            data = jsonObject.toString();

            Log.d("Message", " DELETE : " + data);
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            getPostMethodClass = new GetPostMethodClass();
            String result = getPostMethodClass.sendPostRequest(deleteImageURL, data);

            Log.d("Message", " Result iS :  " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();

            try {
                JSONObject json = new JSONObject(s);

                Log.d("Message", " SSSSS : " + json);
                if (json.getString("status").equals("delete")) {
                    Toast.makeText(AddProductActivity.this, " Image Delete..", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(AddProductActivity.this, " Something Went Wrong", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ImageOperation extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait..");
            dialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                for (int i = 0; i < pathList.size(); i++) {
                    result = uploadFileAndParam(imageUploadURL, product_id, pathList.get(i));
                }

                Log.d("Message", " Result iS :  " + result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();

            try {
                JSONObject json = new JSONObject(s);
                Log.d("Msg", "IMAGE SSS : " + json);

                if (json.getString("status").equals("1")) {
                    Toast.makeText(AddProductActivity.this, " Product Added..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddProductActivity.this, ListOfProductActivity.class);
                    finish();
                    startActivity(intent);
                }
                if (json.getString("status").equals("2")) {
                    Toast.makeText(AddProductActivity.this, " Something Went Wrong.", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public String addFileAndParam(String urlUploadPic, String productname, String productprice, String description, int signId, String sourceFileUri) {
        String fileName = sourceFileUri;
        String jsonString = "";
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :" + fileName);
            return null;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(urlUploadPic);
                conn = (HttpURLConnection) url.openConnection();
/*                conn.setConnectTimeout(500);
                conn.setReadTimeout(1000);*/
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Accept-Encoding", "");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"product_name\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(productname);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"product_price\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(productprice);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"product_description\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(description);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"signup_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(String.valueOf(signId));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"product_default_image\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e("uploadImg", "Headers are written");
                // create a buffer of maximum size

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // close streams

                int responseCode = conn.getResponseCode();
                Log.d("uploadImg", "File Sent, Response: " + responseCode);
                InputStream in = conn.getInputStream();
                byte data[] = new byte[1024];
                int counter = -1;
                while ((counter = in.read(data)) != -1) {
                    jsonString += new String(data, 0, counter);
                }
                Log.d("Debug", " JSON String: " + jsonString);

                fileInputStream.close();
                dos.flush();
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "" + e.getMessage(), e);
            }
            return jsonString;
        }
    }
/*    public String uploadFileAndParam(String urlUploadPic, int product_id ,List<String> imagePath) throws IOException
    {
        String jsonString = "";
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        FileInputStream fileInputStream = null;

        File sourceFile[] = new File[imagePath.size()];

        URL url = new URL(urlUploadPic);
        conn = (HttpURLConnection) url.openConnection();
//                conn.setConnectTimeout(500);
//                conn.setReadTimeout(1000);
        conn.setDoInput(true); // Allow Inputs
        conn.setDoOutput(true); // Allow Outputs
        conn.setUseCaches(false); // Don't use a Cached Copy
        conn.setRequestMethod("POST");

        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty( "Accept-Encoding", "" );
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

        dos = new DataOutputStream(conn.getOutputStream());

        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"product_id\"" + lineEnd);
        dos.writeBytes(lineEnd);
        dos.writeBytes(String.valueOf(product_id));
        dos.writeBytes(lineEnd);

        for (int i=0;i<imagePath.size();i++)
        {
            sourceFile[i] = new File(imagePath.get(i));

            if(!sourceFile[i].isFile())
            {
                Log.e("uploadFile", "Source File not exist :" + sourceFile[i]);
                return null;
            }
            else
            {
                try
                {
                    fileInputStream = new FileInputStream(sourceFile[i]) ;

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"images\";filename=\"" + sourceFile[i] + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    Log.e("uploadImg", "Headers are written");

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];
                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);



                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch (ProtocolException e)
                {
                    e.printStackTrace();
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

        }
        int responseCode = conn.getResponseCode();
        Log.d("uploadImg", "File Sent, Response: " + responseCode);
        InputStream in = conn.getInputStream();
        byte data[] = new byte[1024];
        int counter = -1;
        while ((counter = in.read(data)) != -1)
        {
            jsonString += new String(data, 0, counter);
            in.close();
        }

        Log.d("Debug", " JSON String: " + jsonString);
        fileInputStream.close();
        dos.flush();
        dos.close();

        return jsonString;
    }*/

    public String updateDefaultImageAndParam(String urlUploadPic, int product_id, String sourceFileUri) {
        String fileName = sourceFileUri;
        String jsonString = "";
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :" + fileName);
            return null;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(urlUploadPic);
                conn = (HttpURLConnection) url.openConnection();

                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Accept-Encoding", "");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"product_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(String.valueOf(product_id));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"product_default_image\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e("uploadImg", "Headers are written");
                // create a buffer of maximum size

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // close streams

                int responseCode = conn.getResponseCode();
                Log.d("uploadImg", "File Sent, Response: " + responseCode);
                InputStream in = conn.getInputStream();
                byte data[] = new byte[1024];
                int counter = -1;
                while ((counter = in.read(data)) != -1) {
                    jsonString += new String(data, 0, counter);
                }
                Log.d("Debug", " JSON String: " + jsonString);

                fileInputStream.close();
                dos.flush();
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "" + e.getMessage(), e);
            }
            return jsonString;
        }
    }

    public String uploadFileAndParam(String urlUploadPic, int product_id, String imagePath) throws IOException {
        String jsonString = "";
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        FileInputStream fileInputStream = null;

        File sourceFile = new File(imagePath);

        URL url = new URL(urlUploadPic);
        conn = (HttpURLConnection) url.openConnection();
//                conn.setConnectTimeout(500);
//                conn.setReadTimeout(1000);
        conn.setDoInput(true); // Allow Inputs
        conn.setDoOutput(true); // Allow Outputs
        conn.setUseCaches(false); // Don't use a Cached Copy
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        dos = new DataOutputStream(conn.getOutputStream());
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"product_id\"" + lineEnd);
        dos.writeBytes(lineEnd);
        dos.writeBytes(String.valueOf(product_id));
        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + lineEnd);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :" + sourceFile);
            return null;
        } else {
            try {
                fileInputStream = new FileInputStream(sourceFile);

                dos.writeBytes("Content-Disposition: form-data; name=\"product_image_url\";filename=\"" + sourceFile + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                Log.e("uploadImg", "Headers are written");
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                int responseCode = conn.getResponseCode();
                Log.d("uploadImg", "File Sent, Response: " + responseCode);
                InputStream in = conn.getInputStream();
                byte data[] = new byte[1024];
                int counter = -1;
                while ((counter = in.read(data)) != -1) {
                    jsonString += new String(data, 0, counter);
                    in.close();
                }
                Log.d("Debug", " JSON String: " + jsonString);

                fileInputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dos.flush();
        dos.close();
        return jsonString;
    }

    public String updateFileAndParam(String urlUploadPic, int product_id, String sourceFileUri) {
        String fileName = sourceFileUri;
        String jsonString = "";
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :" + fileName);
            return null;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(urlUploadPic);
                conn = (HttpURLConnection) url.openConnection();
/*                conn.setConnectTimeout(500);
                conn.setReadTimeout(1000);*/
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Accept-Encoding", "");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"product_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(String.valueOf(product_id));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"product_image_url\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e("uploadImg", "Headers are written");
                // create a buffer of maximum size

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // close streams


                int responseCode = conn.getResponseCode();
                Log.d("uploadImg", "File Sent, Response: " + responseCode);
                InputStream in = conn.getInputStream();
                byte data[] = new byte[1024];
                int counter = -1;
                while ((counter = in.read(data)) != -1) {
                    jsonString += new String(data, 0, counter);
                }
                Log.d("Debug", " JSON String: " + jsonString);

                fileInputStream.close();
                dos.flush();
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "" + e.getMessage(), e);
            }
            return jsonString;
        }
    }
}

