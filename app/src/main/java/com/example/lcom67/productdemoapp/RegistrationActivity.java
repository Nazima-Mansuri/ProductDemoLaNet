package com.example.lcom67.productdemoapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lcom67.productdemoapp.AsyncTaskClass.GetPostMethodClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText firstName,lastname,userName,password,emailId,address;
    Button submit,login;
    ImageView profileImage;
    String URL = "http://192.168.200.64:4000/register/";
    String fName,lName,uName,email,passwd,adrs;
    TextInputLayout firstNameWrapper,lastNameWrapper,usernameWrapper,emailIdWrapper,passwordWrapper,addressWrapper;
    private static final int ACTION_REQUEST_GALLERY = 1;
    private static final int ACTION_REQUEST_CAMERA = 2;
    private Bitmap bitmap;
    private Toolbar toolbar;

    GetPostMethodClass getPostMethodClass;
    JSONObject jsonObject ;
    ProgressDialog dialog;
    String insertURL = URL + "insert";
    String insertWithoutImageURL = URL + "insertwithoutimage";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private String picturePath;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        jsonObject = new JSONObject();

        dialog = new ProgressDialog(RegistrationActivity.this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Registration");
        setSupportActionBar(toolbar);

        profileImage = (ImageView) findViewById(R.id.img_profile);
        firstName = (EditText) findViewById(R.id.et_first_name);
        lastname = (EditText) findViewById(R.id.et_last_name);
        userName = (EditText) findViewById(R.id.et_register_username);
        emailId = (EditText) findViewById(R.id.et_emailId);
        password = (EditText) findViewById(R.id.et_register_password);
        address = (EditText) findViewById(R.id.et_address);

        submit = (Button) findViewById(R.id.btn_submit);
        login = (Button) findViewById(R.id.btn_login);

        // TextInputLayout ID
        firstNameWrapper = (TextInputLayout) findViewById(R.id.firstNameWrapper);
        lastNameWrapper = (TextInputLayout) findViewById(R.id.lastNameWrapper);
        usernameWrapper = (TextInputLayout) findViewById(R.id.register_usernameWrapper);
        emailIdWrapper = (TextInputLayout) findViewById(R.id.emailIdWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.register_passwordWrapper);
        addressWrapper = (TextInputLayout) findViewById(R.id.addressWrapper);

        editTextMethods();

        profileImage.setOnClickListener(this);
        submit.setOnClickListener(this);
        login.setOnClickListener(this);

    }

    public void editTextMethods()
    {
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                fName = firstName.getText().toString();
                if(fName.equals("") || fName.equals(null))
                {
                    firstNameWrapper.setError("Must Enter First Name");
                }
                else
                {
                    firstNameWrapper.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        lastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lName = lastname.getText().toString();
                if(lName.equals("") || lName.equals(null))
                {
                    lastNameWrapper.setError("Must Enter Last Name");
                }
                else
                {
                    lastNameWrapper.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                uName = userName.getText().toString();
                if(uName.equals("") || uName.equals(null))
                {
                    usernameWrapper.setError("Must Enter User Name");
                }
                else
                {
                    usernameWrapper.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        emailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email= emailId.getText().toString();

                if(!email.equals("") || email.matches(emailPattern))
                {
                    emailIdWrapper.setError("");
                }

                emailId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b)
                    {
                        if(!b)
                        {
                            if(email.equals("") || !email.matches(emailPattern))
                            {
                                emailIdWrapper.setError("Please Enter Valid Email Address");
                            }
                            else
                            {
                                emailIdWrapper.setError("");
                            }
                        }

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                passwd = password.getText().toString();

                if(passwd.length()>=6)
                {
                    passwordWrapper.setError("");
                }
                password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b)
                    {

                        passwd = password.getText().toString();
                        if(!b)
                        {
                            if(passwd.equals("") || passwd.length() < 6)
                            {
                                passwordWrapper.setError("You must more characters in your password");
                            }
                            else
                            {
                                passwordWrapper.setError("");
                            }
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });
    }
    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        fName = firstName.getText().toString();
        lName = lastname.getText().toString();
        uName = userName.getText().toString();
        email = emailId.getText().toString().trim();
        passwd = password.getText().toString();
        adrs = address.getText().toString();

        if(id == R.id.img_profile)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
            builder.setTitle("Choose Image Source");
            builder.setItems(new CharSequence[] {"Gallery", "Camera"},
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
                                    startActivityForResult(i, ACTION_REQUEST_GALLERY);
                                    break;

                                case 1:
                                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                    File photo = new File(Environment.getExternalStorageDirectory(), timeStamp + ".jpg");
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(photo));
                                    imageUri = Uri.fromFile(photo);
                                    startActivityForResult(intent, ACTION_REQUEST_CAMERA);
                                    break;
                            }
                        }
                    });

            builder.show();
        }
        else if(id == R.id.btn_submit)
        {
            if(picturePath != null)
            {
                new LongOperation().execute(insertURL);
            }
            else
            {
                try
                {
                    jsonObject.put("firstname",fName);
                    jsonObject.put("lastname",lName);
                    jsonObject.put("username",uName);
                    jsonObject.put("email_id",email);
                    jsonObject.put("password",passwd);
                    jsonObject.put("address",adrs);

                    Log.d("Message"," JSON : " + jsonObject);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                new InsertOperation().execute();
            }
        }
        else if(id == R.id.btn_login)
        {
            Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_REQUEST_CAMERA && resultCode == Activity.RESULT_OK)
        {
            super.onActivityResult(requestCode, resultCode, data);
            Uri selectedImage = imageUri;
            getContentResolver().notifyChange(selectedImage, null);
            ContentResolver cr = getContentResolver();
            try
            {
                bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                picturePath = selectedImage.getPath();
                profileImage.setImageBitmap(bitmap);
                Toast.makeText(this, picturePath,Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                Log.d("Message","Camera"+ e.toString());
            }

        }
        else if(requestCode == ACTION_REQUEST_GALLERY && resultCode == Activity.RESULT_OK)
        {
            if(data.getData()!=null)
            {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

                profileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                Toast.makeText(this,picturePath ,Toast.LENGTH_LONG).show();
            }
            else
            {
                bitmap=(Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(bitmap);
            }


            super.onActivityResult(requestCode, resultCode, data);
        }
    }

        private class LongOperation  extends AsyncTask<String, Void, String>
        {
        @Override
        protected void onPreExecute()
        {
            dialog.setMessage("Please wait..");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings)
        {
            String result = uploadFileAndParam(insertURL, fName , lName ,uName ,email ,passwd , adrs , picturePath);

            Log.d("Message"," Result iS :  " + result);
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
                    Toast.makeText(RegistrationActivity.this," User Registered..",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                else if(json.getString("status").equals("2"))
                {
                    Toast.makeText(RegistrationActivity.this," User Already Exist.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(RegistrationActivity.this," Something Went Wrong",Toast.LENGTH_SHORT).show();
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class InsertOperation  extends AsyncTask<String, Void, String>
    {
        String data;


        @Override
        protected void onPreExecute()
        {
            dialog.setMessage("Please wait..");
            dialog.show();

            // Set Request parameter
            data = jsonObject.toString();

            Log.d("Message"," DATA : " + data);
//            Toast.makeText(RegistrationActivity.this,data,Toast.LENGTH_SHORT).show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings)
        {
            getPostMethodClass = new GetPostMethodClass();
            String result = getPostMethodClass.sendPostRequest(insertWithoutImageURL,data);

            Log.d("Message"," Result iS :  " + result);
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
                    Toast.makeText(RegistrationActivity.this," User Registered..",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                else if(json.getString("status").equals("2"))
                {
                    Toast.makeText(RegistrationActivity.this," User Already Exist.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(RegistrationActivity.this," Something Went Wrong",Toast.LENGTH_SHORT).show();
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String uploadFileAndParam(String urlUploadPic,String firstname , String lastname , String username , String emailId,
                                     String password , String address , String sourceFileUri)
    {
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

        if (!sourceFile.isFile())
        {
            Log.e("uploadFile", "Source File not exist :" + fileName);
            return null;
        }
        else
        {
            try
            {
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
                conn.setRequestProperty( "Accept-Encoding", "" );
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"firstname\"" + lineEnd);
                dos.writeBytes(lineEnd);

                dos.writeBytes(firstname);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"lastname\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(lastname);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"username\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(username);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"email_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(emailId);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"password\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(password);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"address\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(address);
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

            }
            catch (MalformedURLException ex)
            {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e("Exception", "" + e.getMessage(), e);
            }
            return jsonString;
        }
    }
}

