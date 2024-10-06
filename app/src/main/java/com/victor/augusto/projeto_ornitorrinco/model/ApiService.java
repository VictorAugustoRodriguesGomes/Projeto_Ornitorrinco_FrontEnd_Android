package com.victor.augusto.projeto_ornitorrinco.model;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface ApiService {

    @POST("user/createUser")
    Call<ResponseApi> createUser(@Body User user);

    @PUT("user/signInWithEmailAndPassword")
    Call<ResponseApi> signInWithEmailAndPassword(@Body User user);

    @PUT("user/sendPasswordResetEmail")
    Call<ResponseApi> sendPasswordResetEmail(@Body User user);

    @PUT("user/validateVerificationCode")
    Call<ResponseApi> validateVerificationCode(@Body User user);

    @GET("user/profile")
    Call<ResponseApi> getUserProfile(@Header("Authorization") String token);

    @PATCH("user/profile/updateUsersDisplayName")
    Call<ResponseApi> updateUsersDisplayName(@Header("Authorization") String token, @Body User user);

    @PATCH("user/profile/updateUsersPassword")
    Call<ResponseApi> updateUsersPassword(@Header("Authorization") String token, @Body User user);

    @DELETE("user/profile/deleteUsers")
    Call<ResponseApi> deleteUsers(@Header("Authorization") String token);

    @Multipart
    @PATCH("user/profile/updateUsersPhoto")
    Call<ResponseApi> updateUsersPhoto( @Header("Authorization") String authorization, @Part MultipartBody.Part imagem );

}
