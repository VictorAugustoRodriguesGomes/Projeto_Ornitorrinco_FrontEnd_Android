package com.victor.augusto.projeto_ornitorrinco;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.victor.augusto.projeto_ornitorrinco.model.ApiService;
import com.victor.augusto.projeto_ornitorrinco.model.ResponseApi;
import com.victor.augusto.projeto_ornitorrinco.model.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Information extends AppCompatActivity {

    private Button BTNAboutInformation;
    private Button BTNStartInformation;

    private boolean doubleClick = false;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_information);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedDispatcher dispatcherBack = getOnBackPressedDispatcher();
        dispatcherBack.addCallback(Information.this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleClick) {
                    if (backToast != null) backToast.cancel();
                    finish();
                } else {
                    doubleClick = true;
                    backToast = Toast.makeText(getApplicationContext(), "Pressione 2 vezes para fechar do aplicativo!", Toast.LENGTH_SHORT);
                    backToast.show();

                    new android.os.Handler().postDelayed(() -> doubleClick = false, 3000);
                }
            }
        });

        BTNAboutInformation = findViewById(R.id.BTNAboutInformation);
        BTNStartInformation = findViewById(R.id.BTNStartInformation);

        BTNAboutInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Information.this, About.class);
                startActivity(intent);
            }
        });

        BTNStartInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTNStartInformation.setEnabled(false);
                tokenActivity();
            }
        });
    }

    private void tokenActivity() {
        SharedPreferences dataTokenConfig = getSharedPreferences("tokenConfig", MODE_PRIVATE);
        String accessToken = dataTokenConfig.getString("access_token", null);

        if(accessToken != null){
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<ResponseApi> call = apiService.getUserProfile("Bearer " + accessToken);
            call.enqueue(new Callback<ResponseApi>() {
                @Override
                public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseApi responseApi = response.body();
                        if (responseApi.getStatus().equals("success")) {
                            BTNStartInformation.setEnabled(true);
                            Intent intent = new Intent(Information.this, Profile.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        BTNStartInformation.setEnabled(true);
                        try {
                            String errorResponse = response.errorBody().string();
                            Gson gson = new Gson();
                            ResponseApi erroApi = gson.fromJson(errorResponse, ResponseApi.class);
                            if ( erroApi.getCode() == 401 ) {
                                Intent intent = new Intent(Information.this, Login.class);
                                startActivity(intent);
                            }
                        } catch (IOException e) {
                            Log.e("Erro", "Falha ao ler corpo do erro de signInWithEmailAndPassword: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "ERRO(signInWithEmailAndPassword): Por favor informe o desenvolvedor!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onFailure(Call<ResponseApi> call, Throwable t) {
                    BTNStartInformation.setEnabled(true);
                    Log.e("Erro", "Falha na requisição de getUserProfile: " + t.getMessage());
                    Toast.makeText(getApplicationContext(), "ERRO(getUserProfile): Por favor informe o desenvolvedor!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            BTNStartInformation.setEnabled(true);
            Intent intent = new Intent(Information.this, Login.class);
            startActivity(intent);
        }
    }
}