package com.example.basiccameraupload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApis {
    @Multipart
    @POST("upload_image")
    Call<ResponseBody> uploadImage(@Part("requestBody") RequestBody requestBody, @Part MultipartBody.Part part);
}
