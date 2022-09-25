package com.example.basiccameraupload;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String[] categories = {"Animals", "Cars", "Sceneries", "People", "Plants"};
    private int categoryPosition = 0;
    private Bitmap clickedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Spinner spin = (Spinner) findViewById(R.id.spinnerButton);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            clickedImage = (Bitmap) bundle.get("clickedImage");
        }
        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(view -> saveImage(clickedImage));
    }

    private void saveImage(Bitmap image){
        String rootDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File savedImageDirectory = new File(rootDirectory + "/saved_images");
        savedImageDirectory.mkdirs();
        String filename = String.format("%s.jpg", System.currentTimeMillis());
        File file = new File(savedImageDirectory, filename);
        if (file.exists()) file.delete();
        try{
            FileOutputStream outFile = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, outFile);
            outFile.flush();
            outFile.close();
            uploadImage(savedImageDirectory, filename);
        }catch(Exception err){
            err.printStackTrace();
        }

    }

    public void uploadImage(File directory, String filePath){
        File file = new File(directory, filePath);
        RequestBody requestBody = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part parts = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody someData = RequestBody.create( categories[categoryPosition], MediaType.parse("text/plain"));
        Retrofit retrofit = RetrofitClient.getRetrofit();
        UploadApis uploadApis = retrofit.create(UploadApis.class);
        Call call = uploadApis.uploadImage(someData, parts);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(UploadActivity.this, "Image Successfully Upload to Server", Toast.LENGTH_LONG).show();
                goToHome();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("Error_TAG", "onFailure: Error: " + t.getMessage());
                Toast.makeText(UploadActivity.this, "Image Failed to Upload to Server", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1 , int position, long id){
        categoryPosition = position;
    }

    public void goToHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void onNothingSelected(AdapterView<?> arg0){

    }
}

//Citation
//https://www.tutlane.com/tutorial/android/android-spinner-dropdown-list-with-examples
