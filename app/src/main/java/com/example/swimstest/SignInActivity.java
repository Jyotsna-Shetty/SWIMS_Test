package com.example.swimstest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {
    EditText emailText;
    EditText passText;
    Button loginBtn;
    TextView tokenText;

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

    }
}