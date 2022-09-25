package com.example.basiccameraupload;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CameraActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button saveImageButton;
    private Bitmap clickedImage;
    public static final int RequestPermissionCode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Button takePhotoButton = findViewById(R.id.takeImageButton);
        imageView = findViewById(R.id.imageView);
        saveImageButton = findViewById(R.id.saveImageButton);
        saveImageButton.setVisibility(View.GONE);
        enableRuntimePermission();
        takePhotoButton.setOnClickListener(view -> launchCameraActivity());
        saveImageButton.setOnClickListener(view -> openUploadActivity());
    }

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        clickedImage = bitmap;
                        imageView.setImageBitmap(bitmap);
                        saveImageButton.setVisibility(View.VISIBLE);
                    }
                    else{
                        saveImageButton.setVisibility(View.GONE);
                    }
                }
            }
    );

    public void enableRuntimePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(CameraActivity.this, Manifest.permission.CAMERA)){
            Toast.makeText(CameraActivity.this, "CAMERA permission allows us to access Camera App", Toast.LENGTH_LONG).show();
        }
        else{
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(CameraActivity.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CameraActivity.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void launchCameraActivity(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraActivityResultLauncher.launch(intent);
    }

    public void openUploadActivity(){
        Intent intent = new Intent(this, UploadActivity.class);
        intent.putExtra("clickedImage", clickedImage);
        startActivity(intent);
    }

}