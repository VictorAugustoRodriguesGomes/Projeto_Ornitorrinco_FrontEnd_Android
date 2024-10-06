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

public class VerifyCode2FA extends AppCompatActivity {
    private Button BTNVerifyCode2FA;
    private Button BTNCancelVerifyCode2FA;

    private EditText inputCodeVerifyCode2FA;

    private boolean doubleClick = false;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_code2_fa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedDispatcher dispatcherBack = getOnBackPressedDispatcher();
        dispatcherBack.addCallback(VerifyCode2FA.this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(VerifyCode2FA.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        String superUserUID = intent.getStringExtra("userUID");

        BTNVerifyCode2FA = findViewById(R.id.BTNVerifyCode2FA);
        BTNCancelVerifyCode2FA = findViewById(R.id.BTNCancelVerifyCode2FA);

        inputCodeVerifyCode2FA = findViewById(R.id.inputCodeVerifyCode2FA);

        BTNVerifyCode2FA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeVerifyCode2FA = inputCodeVerifyCode2FA.getText().toString().trim();

                if (codeVerifyCode2FA == null || codeVerifyCode2FA.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Preencha o campo E-mail!", Toast.LENGTH_SHORT).show();
                } else {
                    BTNVerifyCode2FA.setEnabled(false);
                    VerifyCode(codeVerifyCode2FA, superUserUID);
                }
            }
        });

        BTNCancelVerifyCode2FA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerifyCode2FA.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void VerifyCode(String codeVerifyCode2FA, String userUID) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        User newUser = new User();
        newUser.setUid(userUID);
        newUser.setCodeVerification(codeVerifyCode2FA);

        apiService.validateVerificationCode(newUser).enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseApi responseApi = response.body();

                    if (responseApi.getStatus().equals("success")) {
                        Log.d("TokenX", "Novo Token do user: " + responseApi.getToken());
                        SharedPreferences dataTokenConfig = getSharedPreferences("tokenConfig", MODE_PRIVATE);
                        SharedPreferences.Editor editor = dataTokenConfig.edit();
                        editor.putString("access_token", responseApi.getToken());
                        editor.apply();

                        Intent intent = new Intent(VerifyCode2FA.this, Profile.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    BTNVerifyCode2FA.setEnabled(true);
                    try {
                        String errorResponse = response.errorBody().string();
                        Gson gson = new Gson();
                        ResponseApi erroApi = gson.fromJson(errorResponse, ResponseApi.class);

                        if (erroApi.getCode() == 409) {
                            Toast.makeText(getApplicationContext(), "O \"código: " + newUser.getCodeVerification() + "\" expirou ou é inválido!", Toast.LENGTH_SHORT).show();
                        }

                        if (erroApi.getCode() == 404) {
                            Toast.makeText(getApplicationContext(), "Email inexistente ou inválido!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        Log.e("Erro", "Falha ao ler corpo do erro de validateVerificationCode: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "ERRO(validateVerificationCode): Por favor informe o desenvolvedor !", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                BTNVerifyCode2FA.setEnabled(true);
                Log.e("Erro", "Falha na requisição de validateVerificationCode: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "ERRO(validateVerificationCode): Por favor informe o desenvolvedor !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}