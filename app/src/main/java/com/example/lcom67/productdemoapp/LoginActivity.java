package com.example.lcom67.productdemoapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lcom67.productdemoapp.AsyncTaskClass.GetPostMethodClass;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    TextView forgotPassword;
    Button login,signup;
    private Toolbar toolbar;
    JSONObject jsonObject;
    String loginURL = "http://192.168.200.64:4000/register/signin";

    GetPostMethodClass getPostMethodClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        jsonObject = new JSONObject();
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);

        username = (EditText) findViewById(R.id.et_login_userName);
        password = (EditText) findViewById(R.id.et_login_password);
        forgotPassword = (TextView) findViewById(R.id.tv_forgotPassword);

        login = (Button) findViewById(R.id.btn_login);
        signup = (Button) findViewById(R.id.btn_signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_username = username.getText().toString();
                String str_password = password.getText().toString();

                try {
                    jsonObject.put("username", str_username);
                    jsonObject.put("password", str_password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Use AsyncTask execute Method To Prevent ANR Problem
                new LongOperation().execute(loginURL);
//                new LongOperation().execute(selectURL);

//                Intent intent = new Intent(LoginActivity.this,ListOfProductActivity.class);
//                startActivity(intent);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle("Forgot Password...");
                dialog.getWindow().setTitleColor(getResources().getColor(R.color.colorPrimary));

                Button dialogButton = (Button) dialog.findViewById(R.id.btn_reset_password);
                // if button is clicked, close the custom progressDialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this , RegistrationActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        String data;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(LoginActivity.this);

            progressDialog.setMessage("Please wait..");
            progressDialog.show();
            data = jsonObject.toString();
            getPostMethodClass = new GetPostMethodClass();
            // Set Request parameter
//            data = jsonObject.toString();

            Log.d("Message", " DATA : " + data);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            String result = getPostMethodClass.sendPostRequest(loginURL, data);

            Log.d("Message", "RESULT : " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);


            try {
                JSONObject json = new JSONObject(s);


                if (json.getString("status").equals("1")) {

                    int signup_id = json.getInt("signup_id");

                    Log.d("Message", " Signup_ID : " + json.getInt("signup_id"));

                    SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
                    editor.putInt("signup_id", signup_id);
                    editor.commit();

                    Toast.makeText(LoginActivity.this, " Successful..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, ListOfProductActivity.class);

                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, " No User Exist..", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }

            } catch (JSONException e) {
                e.printStackTrace();

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }


        }
    }
}
