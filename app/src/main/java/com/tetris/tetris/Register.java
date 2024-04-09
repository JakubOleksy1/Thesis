package com.tetris.tetris;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.*;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
//import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;





public class Register extends AppCompatActivity {
    Database databaseHelper;

    EditText et_username, et_password, et_cpassword;
    Button btn_register, btn_login;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        databaseHelper = new Database(this);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_cpassword = (EditText) findViewById(R.id.et_cpassword);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_login = (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                String confirm_password = et_cpassword.getText().toString();

                if (username.equals("") || password.equals("") || confirm_password.equals("")) {
                    Toast.makeText(getApplicationContext(), "Fields Required", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(confirm_password)) {
                        Boolean checkusername = databaseHelper.CheckUsername(username);
                        if (checkusername) {
                            checkUsernameOnServer(username, password);
                            if (checkusername == true) {
                                Boolean insert = databaseHelper.Insert(username, password);
                                if (insert == true) {
                                    Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_SHORT).show();
                                    postUserPass();
                                    et_username.setText("");
                                    et_password.setText("");
                                    et_cpassword.setText("");

                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Username already taken", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void postUserPass() {

        String authorization_username = "JakubOleksy";
        String authorization_password = "KubaaS_01";

        String credentials = authorization_username + ":" + authorization_password;
        String base64Credentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        EditText et_username = findViewById(R.id.et_username);
        EditText et_password = findViewById(R.id.et_password);
        String created_at = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

       // int intValue = 42;
       // String stringValue = "" + intValue;

        Map<String, String> params = new HashMap<>();
        //params.put("id", stringValue);
        params.put("username", username);
        params.put("password", password);
       // params.put("created_at", created_at);

        JSONObject json = new JSONObject(params);

        String url = "http://jakuboleksy.pythonanywhere.com/users/add"; // dodaj s lub nie

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, json, //null albo json
                response -> {
                    Toast.makeText(getApplicationContext(), "Sent to server", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Couldn't send to server", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("Authorization", "Basic " + base64Credentials);
                return headers;
            }
        };

       // RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonRequest);
    }

    private void checkUsernameOnServer(String username, String password) {
        String url = "http://jakuboleksy.pythonanywhere.com/users/check-username/" + username;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        boolean usernameExists = response.getBoolean("exists");
                        if (!usernameExists) {
                            // Username doesn't exist on the server, proceed to insert
                            Boolean insert = databaseHelper.Insert(username, password);
                            if (insert) {
                                Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_SHORT).show();
                                postUserPass();
                                et_username.setText("");
                                et_password.setText("");
                                et_cpassword.setText("");
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Username already taken", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Couldn't check username on server", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        requestQueue.add(jsonRequest);
    }
}