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

public class LoginActivity extends AppCompatActivity {

    EditText emailText;
    EditText passText;
    Button loginBtn;
    TextView tokenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText.findViewById(R.id.EmailAddress);
        passText.findViewById(R.id.TextPassword);
        loginBtn.findViewById(R.id.LoginButton);
        tokenText.findViewById(R.id.TestToken);

        loginBtn.setOnClickListener(view -> {
            String url = "https://tools-management-dbms-project.herokuapp.com/api/account/signin";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            tokenText.setText("Token: " + response.toString());
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            Toast.makeText(LoginActivity.this, "Sign in error. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }
}