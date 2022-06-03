package com.example.swimstest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {
    EditText emailText;
    EditText passText;
    Button loginBtn;
    TextView tokenText;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        emailText = findViewById(R.id.EmailID);
        passText = findViewById(R.id.TextPassword);
        loginBtn = findViewById(R.id.LoginButton);
        tokenText = findViewById(R.id.TestToken);

        loginBtn.setOnClickListener(view -> {
            loginRequest();
        });

    }

    public void loginRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
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
                tokenText.setText("String Response : "+ response.toString());
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tokenText.setText("Error getting response");
            }
        });
        requestQueue.add(jsonObjectRequest);

    }
}