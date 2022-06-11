package com.example.swimstest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
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

    Button takeBtn, viewBtn;
    TextView toolsTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        takeBtn = findViewById(R.id.TakeButton);
        viewBtn = findViewById(R.id.ViewButton);
        toolsTaken = findViewById(R.id.ToolsText);
        takeBtn.setOnClickListener(view -> startActivity(new Intent(MainmenuActivity.this,MainActivity.class)));
        viewBtn.setOnClickListener(view -> viewinfo());
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

                        //Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                        Log.i("onResponse", response.toString());
                        try {
                            JSONObject json = response.getJSONObject(0);
                            //Toast.makeText(getApplicationContext(),json.getString("brand_name"),Toast.LENGTH_LONG).show();
                            String toolsDisplay = json.getString("brand_name")+" "+json.getString("tooltype_name")+" "+
                                    json.getString("subtype_name");
                            toolsTaken.setText(toolsDisplay);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

