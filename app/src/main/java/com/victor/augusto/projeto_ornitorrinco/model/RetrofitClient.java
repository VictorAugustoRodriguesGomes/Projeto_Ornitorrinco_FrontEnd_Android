package com.victor.augusto.projeto_ornitorrinco.model;

import com.victor.augusto.projeto_ornitorrinco.ApiConnection.Connection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
    private static final String linkConnection = Connection.connectionAPI();
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(linkConnection).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
