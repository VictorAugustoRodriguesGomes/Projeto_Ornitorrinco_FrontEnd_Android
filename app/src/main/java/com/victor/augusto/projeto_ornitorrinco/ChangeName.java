package com.victor.augusto.projeto_ornitorrinco;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.victor.augusto.projeto_ornitorrinco.model.User;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeName extends AppCompatActivity {


    private Button BTNBackChangeName;
    private Button BTNConfirmChangeName;

    private EditText inputNameChangeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_name);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedDispatcher dispatcherBack = getOnBackPressedDispatcher();
        dispatcherBack.addCallback(ChangeName.this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ChangeName.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        BTNBackChangeName = findViewById(R.id.BTNBackChangeName);
        BTNConfirmChangeName = findViewById(R.id.BTNConfirmChangeName);

        inputNameChangeName = findViewById(R.id.inputNameChangeName);

        tokenActivity();

        BTNBackChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangeName.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        BTNConfirmChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameChangeName = inputNameChangeName.getText().toString().trim();

                if (nameChangeName == null || nameChangeName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Preencha o campo E-mail!", Toast.LENGTH_SHORT).show();
                } else {
                    BTNConfirmChangeName.setEnabled(false);
                    ChangeNameProfile(nameChangeName);
                }
            }
        });
    }

    private void ChangeNameProfile(String nameChangeName) {
        SharedPreferences dataTokenConfig = getSharedPreferences("tokenConfig", MODE_PRIVATE);
        String accessToken = dataTokenConfig.getString("access_token", null);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        User newUser = new User();
        newUser.setDisplayName(nameChangeName);

        Call<ResponseApi> call = apiService.updateUsersDisplayName("Bearer " + accessToken, newUser);

        call.enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseApi responseApi = response.body();
                    if (responseApi.getStatus().equals("success")) {
                        Intent intent = new Intent(ChangeName.this, Profile.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    BTNConfirmChangeName.setEnabled(true);
                    try {
                        String errorResponse = response.errorBody().string();
                        Gson gson = new Gson();
                        ResponseApi erroApi = gson.fromJson(errorResponse, ResponseApi.class);
                        if (erroApi.getCode() == 401) {
                            Toast.makeText(getApplicationContext(), "O \" token \" expirou ou é inválido!", Toast.LENGTH_SHORT).show();
                        }

                        if (erroApi.getCode() == 404) {
                            Toast.makeText(getApplicationContext(), "Email inexistente ou inválido!", Toast.LENGTH_SHORT).show();
                        }

                        Intent intent = new Intent(ChangeName.this, Login.class);
                        startActivity(intent);
                        finish();

                    } catch (IOException e) {
                        Log.e("Erro", "Falha ao ler corpo do erro de updateUsersDisplayName: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "ERRO(updateUsersDisplayName): Por favor informe o desenvolvedor!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                BTNConfirmChangeName.setEnabled(true);
                Log.e("Erro", "Falha na requisição de updateUsersDisplayName: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "ERRO(updateUsersDisplayName): Por favor informe o desenvolvedor!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tokenActivity() {
        SharedPreferences dataTokenConfig = getSharedPreferences("tokenConfig", MODE_PRIVATE);
        String accessToken = dataTokenConfig.getString("access_token", null);

        if (accessToken != null) {
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<ResponseApi> call = apiService.getUserProfile("Bearer " + accessToken);
            call.enqueue(new Callback<ResponseApi>() {
                @Override
                public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseApi responseApi = response.body();
                        User userResponseApi = responseApi.getUser();
                        if (responseApi.getStatus().equals("success")) {
                            inputNameChangeName.setText(userResponseApi.getDisplayName());
                        }
                    } else {
                        Intent intent = new Intent(ChangeName.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResponseApi> call, Throwable t) {
                    Log.e("Erro", "Falha na requisição de getUserProfile: " + t.getMessage());
                    Toast.makeText(getApplicationContext(), "ERRO(getUserProfile): Por favor informe o desenvolvedor!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Intent intent = new Intent(ChangeName.this, Login.class);
            startActivity(intent);
            finish();
        }
    }

}