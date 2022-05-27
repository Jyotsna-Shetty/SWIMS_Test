package com.example.swimstest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText emailText;
    EditText passText;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText.findViewById(R.id.EmailAddress);
        passText.findViewById(R.id.TextPassword);
        loginBtn.findViewById(R.id.LoginButton);

        loginBtn.setOnClickListener(view -> {

        });

    }
}