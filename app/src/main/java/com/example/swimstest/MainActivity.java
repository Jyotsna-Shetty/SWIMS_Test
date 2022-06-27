package com.example.swimstest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;  //camera request code
    public String result;
    Button camBtn, takeBtn, returnBtn;
    String currentPhotoPath, rawValue;
    ImageView selectedImage;
    Bitmap bitmap;
    TextView scannedText;
    EditText statusText, remarksText;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);  //fixes orientation to PORTRAIT mode

        camBtn =findViewById(R.id.CameraButton);
        takeBtn =findViewById(R.id.TakeButton);
        returnBtn =findViewById(R.id.ReturnButton);
        selectedImage = findViewById(R.id.ImageView);
        scannedText = findViewById(R.id.ScannedText);
        statusText = findViewById(R.id.StatusEditText);
        remarksText = findViewById(R.id.RemarksEditText);

        camBtn.setOnClickListener(view -> askCameraPermissions());
        takeBtn.setOnClickListener(view -> takeRequest());
        returnBtn.setOnClickListener(view -> returnRequest());
    }


    //using checkSelfPermission method of ContextCompact checks whether permission is granted or not
    private void askCameraPermissions() {
        //checks for permission from manifest file using PackageManager.PERMISSION_GRANTED
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //permission for camera is passed within the string
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }
    }

    //if camera permissions are not given override the askCameraPermission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //checking  permission  by comparing request code of camera with request code passed to onRequestPermissionResult
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){  //camera permission given
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permission to use camera is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //onActivityResult method is used to display and save image as data of this app
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File newfile = new File(currentPhotoPath);  //creating file  from currentPhotoPath

                Log.d("tag", "Absolute Url of Image is " + Uri.fromFile(newfile));  //display absolute url of the file

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(newfile);
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);
                bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                selectedImage.setImageBitmap(bitmap);
                InputImage image = InputImage.fromBitmap(bitmap, 0);
                try {
                    result = scanBarcodes(image);
                    //Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //This method creates a unique file name for new photo using data time stamp
    //This method is used when photoFile is null
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());  //creates time stamp
        String imageFileName = ";JPEG_" + timeStamp + "_";  //creating image file
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //creating image file using method of CreateTempFile
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );
        //gets absolute path of image
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //this method opens the camera and save our image file into the directory
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //ensures camera is ready
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create the File where the photo should go
            File photoFile = null;
            //to prevent IOException
            try {
                photoFile = createImageFile();  //this returns image
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            //Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileProvider", photoFile);  //using file provider create URI
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);  //can add extra input
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);  //restarting the activity
            }
        }
    }

    private String scanBarcodes(InputImage image) {
        // [START set_detector_options]
        Log.d("BARCODE","Function executed");
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_CODE_39)
                        .build();
        //String rawValue;

        BarcodeScanner scanner = BarcodeScanning.getClient();
        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        // Task completed successfully
                        Log.d("BARCODE","Inner Function executed");
                        for (Barcode barcode: barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            rawValue = barcode.getRawValue();
                            scannedText.setTextColor(Color.parseColor("#000000"));
                            scannedText.setText(rawValue);
                            Log.d("BARCODE",rawValue);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Barcode scanning fail",Toast.LENGTH_SHORT).show();
                        Log.d("BARCODE","Inner Function not executed");
                    }
                });
        return rawValue;
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

    public void takeRequest() {
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("encryption_code", rawValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "https://tools-management-dbms-project.herokuapp.com/api/tools/take";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, "Tool taken successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    json = new String(response.data);
                    json = trimMessage(json, "error");
                    if (json != null)
                        Toast.makeText(MainActivity.this, json, Toast.LENGTH_SHORT).show();
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", "bearer " + SignInActivity.ACCESS_TOKEN);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void returnRequest() {
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        JSONObject object = new JSONObject();
        //Log.d("BARCODE",result);
        try {
            //input your API parameters
            object.put("encryption_code", rawValue);
            object.put("status", statusText.getText().toString());
            object.put("remarks", remarksText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "https://tools-management-dbms-project.herokuapp.com/api/tools/return";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this,"Tool returned successfully", Toast.LENGTH_SHORT).show();
                        Log.d("Return","done");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    json = new String(response.data);
                    json = trimMessage(json, "error");
                    if (json != null)
                        Toast.makeText(MainActivity.this, json, Toast.LENGTH_SHORT).show();
                    Log.d("Return","error");
                    Log.d("Return",json);
                }
            }
        })
            {
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", "bearer " + SignInActivity.ACCESS_TOKEN);
                return headers;
                }
            };
        requestQueue.add(jsonObjectRequest);
    }
}