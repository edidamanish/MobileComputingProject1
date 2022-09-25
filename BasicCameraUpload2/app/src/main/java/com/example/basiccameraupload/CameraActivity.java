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

//    public void saveImageToGallery(Bitmap image){
//        if(isExternalStorageWritable()){
//            saveImage(image);
//        }else{
//            Toast.makeText(CameraActivity.this, "Permission to write to Gallery not granted.", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private boolean isExternalStorageWritable(){
//        String state = Environment.getExternalStorageState();
//        if(Environment.MEDIA_MOUNTED.equals(state)){
//            return true;
//        }
//        return false;
//    }

//    private void saveImage(Bitmap image){
//        String rootDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
//        File savedImageDirectory = new File(rootDirectory + "/saved_images");
//        savedImageDirectory.mkdirs();
//        String filename = String.format("%s.jpg", System.currentTimeMillis());
//        File file = new File(savedImageDirectory, filename);
//
//        if (file.exists()) file.delete();
//        try{
//            FileOutputStream outFile = new FileOutputStream(file);
//            image.compress(Bitmap.CompressFormat.JPEG, 90, outFile);
//            outFile.flush();
//            outFile.close();
//            Toast.makeText(CameraActivity.this, "Image Saved successfully.", Toast.LENGTH_LONG).show();
//        }catch(Exception err){
//            err.printStackTrace();
//            Toast.makeText(CameraActivity.this, "Error with saving Image.", Toast.LENGTH_LONG).show();
//        }
//
//        uploadImage(savedImageDirectory, filename);
//
//    }

    public void launchCameraActivity(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraActivityResultLauncher.launch(intent);
    }

//    public void uploadImage(File directory, String filePath){
//        File file = new File(directory, filePath);
//        RequestBody requestBody = RequestBody.create(file, MediaType.parse("image/*"));
//        MultipartBody.Part parts = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
//
//        RequestBody someData = RequestBody.create( "This is a new image", MediaType.parse("text/plain"));
//
//        Retrofit retrofit = RetrofitClient.getRetrofit();
//        UploadApis uploadApis = retrofit.create(UploadApis.class);
//        Call call = uploadApis.uploadImage(someData, parts, c);
//        call.enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) {
//                Toast.makeText(CameraActivity.this, "Image Successfully Upload", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(Call call, Throwable t) {
//                Log.d("Error_TAG", "onFailure: Error: " + t.getMessage());
//                Toast.makeText(CameraActivity.this, "Image Failed to Upload", Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    public void openUploadActivity(){
        Intent intent = new Intent(this, UploadActivity.class);
        intent.putExtra("clickedImage", clickedImage);
        startActivity(intent);

    }

}