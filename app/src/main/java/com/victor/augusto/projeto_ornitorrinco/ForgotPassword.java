package com.victor.augusto.projeto_ornitorrinco;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class ForgotPassword extends AppCompatActivity {

    private Button BTNBackForgotPassword;
    private Button BTNRequestOrderForgotPassword;

    private EditText inputEmailForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BTNBackForgotPassword = findViewById(R.id.BTNBackForgotPassword);
        BTNRequestOrderForgotPassword = findViewById(R.id.BTNRequestOrderForgotPassword);

        inputEmailForgotPassword = findViewById(R.id.inputEmailForgotPassword);

        BTNBackForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassword.this, Login.class);
                startActivity(intent);
            }
        });

        BTNRequestOrderForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailForgotPassword = inputEmailForgotPassword.getText().toString().trim();

                if (emailForgotPassword == null || emailForgotPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Preencha o campo E-mail!", Toast.LENGTH_SHORT).show();
                } else {
                    BTNRequestOrderForgotPassword.setEnabled(false);
                    requestOrder(emailForgotPassword);
                }
            }
        });
    }

    private void requestOrder(String emailForgotPassword) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        User newUser = new User();
        newUser.setEmail(emailForgotPassword);

        apiService.sendPasswordResetEmail(newUser).enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseApi responseApi = response.body();
                    User userResponseApi = responseApi.getUser();
                    if (responseApi.getStatus().equals("success")) {
                        Intent intent = new Intent(ForgotPassword.this, VerifyCode2FA.class);
                        intent.putExtra("userUID", userResponseApi.getUid());
                        startActivity(intent);
                        finish();
                    }

                } else {
                    BTNRequestOrderForgotPassword.setEnabled(true);
                    try {
                        String errorResponse = response.errorBody().string();
                        Gson gson = new Gson();
                        ResponseApi erroApi = gson.fromJson(errorResponse, ResponseApi.class);

                        if (erroApi.getCode() == 404) {
                            Toast.makeText(getApplicationContext(), "Email inexistente ou inválido!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        Log.e("Erro", "Falha ao ler corpo do erro de sendPasswordResetEmail: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "ERRO(sendPasswordResetEmail): Por favor informe o desenvolvedor !", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                BTNRequestOrderForgotPassword.setEnabled(true);
                Log.e("Erro", "Falha na requisição de sendPasswordResetEmail: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "ERRO(sendPasswordResetEmail): Por favor informe o desenvolvedor !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}