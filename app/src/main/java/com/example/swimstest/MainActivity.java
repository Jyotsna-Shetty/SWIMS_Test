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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;  //camera request code
    Button camBtn;
    String currentPhotoPath;
    ImageView selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);  //fixes orientation to PORTRAIT mode

        camBtn =findViewById(R.id.CameraButton);
        selectedImage = findViewById(R.id.ImageView);

        camBtn.setOnClickListener(view -> {
            askCameraPermissions();
        });
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
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                selectedImage.setImageBitmap(bitmap);
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
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.FileProvider", photoFile);  //using file provider create URI
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);  //can add extra input
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);  //restarting the activity
            }
        }
    }
}