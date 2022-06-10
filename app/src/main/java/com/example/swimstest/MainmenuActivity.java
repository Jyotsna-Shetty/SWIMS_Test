package com.example.swimstest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainmenuActivity extends AppCompatActivity {

    Button Get_take;
    Button View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu2);
        Get_take = findViewById(R.id.get_take);
        View = findViewById(R.id.view);
        Get_take.setOnClickListener(view -> startActivity(new Intent(MainmenuActivity.this,MainActivity.class)));
        View.setOnClickListener(view -> viewinfo());
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

    public void viewinfo(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://tools-management-dbms-project.herokuapp.com/api/tools";


        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                        Log.i("onResponse", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String json = null;
                        Log.e("TAG", "Error " + error.getMessage());
                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null) {
                            json = new String(response.data);
                            json = trimMessage(json, "error");
                            if (json != null) Toast.makeText(getApplicationContext(),json,Toast.LENGTH_LONG).show();
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization","bearer "+SignInActivity.ACCESS_TOKEN );
                return headers;
        }

        };

        queue.add(jsonObjectRequest);
    }
}

