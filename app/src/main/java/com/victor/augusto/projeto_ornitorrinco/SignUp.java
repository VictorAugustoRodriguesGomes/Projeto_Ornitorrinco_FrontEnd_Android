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

public class SignUp extends AppCompatActivity {

    private Button BTNBackSignUp;
    private Button BTNCreateSignUp;

    private EditText inputNameSignUp;
    private EditText inputEmailSignUp;
    private EditText inputPasswordSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputNameSignUp = findViewById(R.id.inputNameSignUp);
        inputEmailSignUp = findViewById(R.id.inputEmailSignUp);
        inputPasswordSignUp = findViewById(R.id.inputPasswordSignUp);

        BTNBackSignUp = findViewById(R.id.BTNBackSignUp);
        BTNCreateSignUp = findViewById(R.id.BTNCreateSignUp);

        BTNBackSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });

        BTNCreateSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameSignUp = inputNameSignUp.getText().toString().trim();
                String emailSignUp = inputEmailSignUp.getText().toString().trim();
                String passwordSignUp = inputPasswordSignUp.getText().toString().trim();

                if (nameSignUp == null || nameSignUp.isEmpty() || emailSignUp == null || emailSignUp.isEmpty() || passwordSignUp == null || passwordSignUp.isEmpty() || passwordSignUp.length() < 8 ) {
                    if (nameSignUp == null || nameSignUp.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Preencha o campo nome!", Toast.LENGTH_SHORT).show();
                    }

                    if (emailSignUp == null || emailSignUp.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Preencha o campo E-mail!", Toast.LENGTH_SHORT).show();
                    }

                    if (passwordSignUp == null || passwordSignUp.isEmpty() || passwordSignUp.length() < 8) {
                        Toast.makeText(getApplicationContext(), "A senha deve ter pelo menos 8 caracteres", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    BTNCreateSignUp.setEnabled(false);
                    createUser(nameSignUp, emailSignUp, passwordSignUp);
                }
            }
        });
    }

    private void createUser(String nameSignUp, String emailSignUp, String passwordSignUp) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        User newUser = new User();
        newUser.setDisplayName(nameSignUp);
        newUser.setEmail(emailSignUp);
        newUser.setPassword(passwordSignUp);

        apiService.createUser(newUser).enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseApi responseApi = response.body();
                    User userResponseApi = responseApi.getUser();

                    if (responseApi.getStatus().equals("success")) {
                        Intent intent = new Intent(SignUp.this, VerifyCode2FA.class);
                        intent.putExtra("userUID", userResponseApi.getUid());
                        startActivity(intent);
                        finish();
                    }
                } else {
                    BTNCreateSignUp.setEnabled(true);
                    try {
                        String errorResponse = response.errorBody().string();
                        Gson gson = new Gson();
                        ResponseApi erroApi = gson.fromJson(errorResponse, ResponseApi.class);

                        if ( erroApi.getCode() == 404 ) {
                            Toast.makeText(getApplicationContext(), "ERRO: Por favor informe o desenvolvedor !", Toast.LENGTH_SHORT).show();
                        }

                        if ( erroApi.getCode() == 409 ) {
                            Toast.makeText(getApplicationContext(), "Email já existente ou inválido !", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        Log.e("Erro", "Falha ao ler corpo do erro: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "ERRO: Por favor informe o desenvolvedor !", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                BTNCreateSignUp.setEnabled(true);
                Log.e("Erro", "Falha na requisição: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "ERRO: Por favor informe o desenvolvedor !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}