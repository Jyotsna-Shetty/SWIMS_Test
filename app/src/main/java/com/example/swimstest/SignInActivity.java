package com.example.swimstest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {
    EditText emailText;
    EditText passText;
    Button loginBtn, signupPgBtn;
    //TextView tokenText;
    RequestQueue requestQueue;
    public static String ACCESS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        emailText = findViewById(R.id.EmailLogin);
        passText = findViewById(R.id.PasswordLogin);
        loginBtn = findViewById(R.id.LoginButton);
        signupPgBtn = findViewById(R.id.SignupPgButton);


        loginBtn.setOnClickListener(view -> {
            loginRequest();
        });

        signupPgBtn.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

    }
    public String trimMessage(String json, String key){
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    public void loginRequest() {
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        JSONObject object = new JSONObject()    ;
        try {
            //input your API parameters
            object.put("email", emailText.getText().toString());
            object.put("password", passText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "https://tools-management-dbms-project.herokuapp.com/api/account/signin/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ACCESS_TOKEN = response.getString("token");
                    Toast.makeText(SignInActivity.this, "Login succesful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInActivity.this,MainmenuActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;

                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null) {
                    json = new String(response.data);
                    json = trimMessage(json, "error");
                    //if (json != null) tokenText.setText(json);
                    if (json != null) Toast.makeText(SignInActivity.this,json,Toast.LENGTH_SHORT).show();
                    }
                }});
        requestQueue.add(jsonObjectRequest);
    }
}