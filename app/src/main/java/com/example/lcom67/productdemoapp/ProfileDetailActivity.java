package com.example.lcom67.productdemoapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileDetailActivity extends AppCompatActivity implements View.OnClickListener,BottomSheetListener {
    String URL = "http://192.168.200.64:4000/";
    TextView firstname, lastname, username, address;
    ImageView profileImage;
    String str_firstname, str_lastname, str_username, str_address, str_profileImage;
    private Toolbar toolbar;
    JSONObject jsonObject;
    int sign_id;
    private GetPostMethodClass getPostMethodClass;
    String selectURL = URL + "profile/select";
    String updateImageURL = URL + "profile/updateimage";
    private static final int ACTION_REQUEST_GALLERY = 1;
    private static final int ACTION_REQUEST_CAMERA = 2;
    private Bitmap bitmap;
    int mState;
    private String picturePath;
    MenuItem save;
    private Uri imageUri;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        jsonObject = new JSONObject();

        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        sign_id = prefs.getInt("signup_id", 0);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Profile Detail");
        setSupportActionBar(toolbar);

        firstname = (TextView) findViewById(R.id.detail_first_name);
        lastname = (TextView) findViewById(R.id.detail_last_name);
        username = (TextView) findViewById(R.id.detail_user_name);
        address = (TextView) findViewById(R.id.detail_address);
        profileImage = (ImageView) findViewById(R.id.img_profile);

        mState = 1;
        invalidateOptionsMenu();
        profileImage.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        try {
            jsonObject.put("signup_id", sign_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new LongOperation().execute(selectURL);
    }

    @Override
    public void onClick(View view)
    {
        id = view.getId();
        if (id == R.id.img_profile)
        {

           /* AlertDialog.Builder builder = new AlertDialog.Builder(ProfileDetailActivity.this);
            builder.setTitle("Choose Image Source");
            builder.setItems(new CharSequence[]{"Gallery", "Camera"},
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:

                                    Intent i = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(i, ACTION_REQUEST_GALLERY);
                                    break;

                                case 1:
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                    File photo = new File(Environment.getExternalStorageDirectory(), timeStamp + ".jpg");
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(photo));
                                    imageUri = Uri.fromFile(photo);
                                    startActivityForResult(intent, ACTION_REQUEST_CAMERA);
                                    break;

                                default:
                                    break;
                            }
                        }
                    });

            builder.show();*/

            new BottomSheet.Builder(this)
                    .setSheet(R.menu.profile_sheet)
                    .grid()
                    .setTitle("Options")
                    .setListener(this)
                    .show();

            mState = 0;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mState = 0;
        invalidateOptionsMenu();
        if (requestCode == ACTION_REQUEST_CAMERA && resultCode == Activity.RESULT_OK)
        {
            super.onActivityResult(requestCode, resultCode, data);

            Uri selectedImage = imageUri;
            getContentResolver().notifyChange(selectedImage, null);
            ContentResolver cr = getContentResolver();
            try {
                bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                picturePath = selectedImage.getPath();
                profileImage.setImageBitmap(bitmap);
                Toast.makeText(this, picturePath, Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                Log.d("Message", "Camera" + e.toString());
            }


        } else if (requestCode == ACTION_REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {

            if (data.getData() != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

                profileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            } else {
                bitmap = (Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(bitmap);
            }

            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        save = menu.findItem(R.id.btn_save);
        if (mState == 1) {
            save.setVisible(false);
        } else {
            save.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_save) {
            new UpdateImageOperation().execute(updateImageURL);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSheetShown(@NonNull BottomSheet bottomSheet) {

    }

    @Override
    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem item)
    {
        if(item.getItemId() == R.id.profile_gallery)
        {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, ACTION_REQUEST_GALLERY);

        }
        else if(item.getItemId() == R.id.profile_camera)
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File photo = new File(Environment.getExternalStorageDirectory(), timeStamp + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photo));
            imageUri = Uri.fromFile(photo);
            startActivityForResult(intent, ACTION_REQUEST_CAMERA);
        }
        else if(item.getItemId() == R.id.profile_delete)
        {
            profileImage.setImageResource(R.drawable.profile_image);
        }

    }

    @Override
    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @DismissEvent int dismissEvent) {

    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        String data;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(ProfileDetailActivity.this);

            dialog.setMessage("Please wait..");
            dialog.show();
            data = jsonObject.toString();
            Log.d("Message", " DATA : " + data);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            getPostMethodClass = new GetPostMethodClass();
            String result = getPostMethodClass.sendPostRequest(selectURL, data);

            Log.d("Message", "RESULT : " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            dialog.dismiss();

            try {
                JSONObject json = new JSONObject(s);

                str_firstname = json.getString("firstname");
                str_lastname = json.getString("lastname");
                str_username = json.getString("username");
                str_address = json.getString("address");
                str_profileImage = json.getString("profile_image");

                Log.d("Message", " firstNM : " + str_firstname);
                Log.d("Message", " lastNM : " + str_lastname);
                Log.d("Message", " userNM : " + str_username);
                Log.d("Message", " addrs : " + str_address);
                Log.d("Message", " Profile : " + str_profileImage);

                if (json.getString("status").equals("1")) {
                    firstname.setText(str_firstname);
                    lastname.setText(str_lastname);
                    username.setText(str_username);
                    address.setText(str_address);

                    Picasso.with(ProfileDetailActivity.this)
                            .load(str_profileImage)
                            .placeholder(R.drawable.profile_image)
                            .into(profileImage);
                }
                else
                {
                    Toast.makeText(ProfileDetailActivity.this, " No User Exist..", Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
    }

    private class UpdateImageOperation extends AsyncTask<String, Void, String>
    {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute()
        {
            dialog = new ProgressDialog(ProfileDetailActivity.this);
            dialog.setMessage("Please wait..");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings)
        {
            String result = uploadFileAndParam(updateImageURL, sign_id, picturePath);
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

                if (json.getString("status").equals("1")) {
                    str_profileImage = json.getString("profile_image");
/*
                    Picasso.with(ProfileDetailActivity.this)
                            .load(str_profileImage)
                            .placeholder(R.drawable.profile_image)
                            .into(profileImage);*/

                    Toast.makeText(ProfileDetailActivity.this, " Profile Pic Update..", Toast.LENGTH_SHORT).show();
                    save.setVisible(false);

                    Intent ii = new Intent();
                    ii.putExtra("profile_image", str_profileImage);
                    setResult(RESULT_OK, ii);
                    finish();
                }
                else
                {
                    Picasso.with(ProfileDetailActivity.this)
                            .load(str_profileImage)
                            .placeholder(R.drawable.profile_image)
                            .into(profileImage);
                    Toast.makeText(ProfileDetailActivity.this, " Something Went Wrong", Toast.LENGTH_SHORT).show();
                    save.setVisible(false);
                }

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public String uploadFileAndParam(String urlUploadPic, int signup_id, String sourceFileUri) {
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
                dos.writeBytes("Content-Disposition: form-data; name=\"signup_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(String.valueOf(signup_id));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"profile_image\";filename=\"" + fileName + "\"" + lineEnd);
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
