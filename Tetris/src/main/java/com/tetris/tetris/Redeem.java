package com.tetris.tetris;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



public class Redeem extends AppCompatActivity {

    Button redeem_code;
    EditText code;

    boolean generate_once = false;

    int machine_id = 1;

    public int gamePoints = 1;

    public boolean pause = false;

    public boolean generate_code = false;

    String randomCode = "ABCDEFGH";
    boolean isUsed = false;
    Button go_back;

    RequestQueue requestQueue;

    private SharedPreferences preferences;

    Database_Code dbCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        dbCode = new Database_Code(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        generate_once = preferences.getBoolean("generate_once", false);
        isUsed = preferences.getBoolean("isUsed", false);

        redeem_code = findViewById(R.id.redeemcode);
        code = findViewById(R.id.editTextcode);

        if (generate_once && generate_code) {
            TextView codeTextView = findViewById(R.id.code);
            codeTextView.setText(randomCode);
        } else {
            gamePoints = getIntent().getIntExtra("points", 0);
            Game GameInstance = new Game();
            generate_code = GameInstance.give_prize();

            //gamePoints = GameInstance.getPoints();
            pause = GameInstance.getPause();

            if (generate_code) {
                randomCode = CodeGenerator.generateRandomCode();
                TextView codeTextView = findViewById(R.id.code);
                codeTextView.setText(randomCode);

                dbCode.InsertCode(randomCode, isUsed);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("generate_once", true);
                editor.apply();

                isUsed = false;
                SharedPreferences.Editor editor1 = preferences.edit();
                editor1.putBoolean("isUsed", false);
                editor1.apply();

                postACode();

            } else {
                Toast.makeText(getApplicationContext(), "Maybe next time", Toast.LENGTH_SHORT).show();

                //tu wyslij kod i is used do serwera tylko musizz wylac go zanim isused zostanie zmieniony na false wtedy bd generowac bledne kody i bd git
            }
        }

        go_back = (Button) findViewById(R.id.goback);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Redeem.this, Login.class);
                startActivity(intent);
            }
        });


        redeem_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = code.getText().toString().trim();
                fetchAndUpdateFromServer(enteredCode);
                if (!isUsed) {
                    if (enteredCode.equalsIgnoreCase(randomCode)) {
                        Toast.makeText(getApplicationContext(), "Your code has been redeemed.", Toast.LENGTH_SHORT).show();
                        isUsed = true;
                        SharedPreferences.Editor editor1 = preferences.edit();
                        editor1.putBoolean("isUsed", true);
                        editor1.apply();

                        dbCode.UpdateIsUsedStatus(randomCode, true);

                        generate_once = false;
                        generate_code = false;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("generate_once", false);
                        editor.apply();
                    } else {
                        Toast.makeText(getApplicationContext(), "This code is invalid.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                }
            }
        });
    }

    public void postACode() {

        String authorization_username = "JakubOleksy";
        String authorization_password = "KubaaS_01";

        String credentials = authorization_username + ":" + authorization_password;
        String base64Credentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);


        Map<String, Object> params = new HashMap<>();
        params.put("code", randomCode);
        params.put("give_prize", generate_once);
        params.put("is_used", isUsed);
        params.put("machine_id", machine_id);
        params.put("pause", pause);
        params.put("points", gamePoints);

        JSONObject json = new JSONObject(params);

        String url = "http://jakuboleksy.pythonanywhere.com/action/add";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, json,
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

    private void fetchAndUpdateFromServer(String enteredCode) {
        String fetchUrl = "http://jakuboleksy.pythonanywhere.com/action/check"; // Update with your server's endpoint

        JSONObject fetchJson = new JSONObject();
        try {
            fetchJson.put("code", enteredCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest fetchRequest = new JsonObjectRequest(Request.Method.POST, fetchUrl, fetchJson,
                response -> {
                    try {
                        boolean isValid = response.getBoolean("is_valid");
                        boolean isUsedFromServer = response.getBoolean("is_used");

                        if (isValid && !isUsedFromServer) {
                            // Update the local 'isUsed' value and UI
                            isUsed = true;
                            SharedPreferences.Editor editor1 = preferences.edit();
                            editor1.putBoolean("isUsed", true);
                            editor1.apply();

                            dbCode.UpdateIsUsedStatus(enteredCode, true); // Update status for the entered code

                            // Perform other necessary updates
                            // ...

                            // Call the function to update the code's status on the server
                            updateCodeStatusOnServer(enteredCode, true);

                            Toast.makeText(getApplicationContext(), "Code is valid and updated from server", Toast.LENGTH_SHORT).show();
                        } else if (!isValid) {
                            Toast.makeText(getApplicationContext(), "Invalid code", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Code is already used", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Error checking code on server", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        requestQueue.add(fetchRequest);
    }

    private void updateCodeStatusOnServer(String code, boolean isUsed) {
        String updateUrl = "http://jakuboleksy.pythonanywhere.com/action/update_status/" + code;

        JSONObject updateJson = new JSONObject();
        try {
            updateJson.put("is_used", isUsed);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.PUT, updateUrl, updateJson,
                response -> {
                    // Handle the response if needed
                },
                error -> {
                    // Handle the error if needed
                    error.printStackTrace();
                }
        );

        requestQueue.add(updateRequest);
    }
}

