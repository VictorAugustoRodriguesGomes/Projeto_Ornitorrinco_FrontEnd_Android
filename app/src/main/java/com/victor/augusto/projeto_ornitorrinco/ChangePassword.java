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

public class ChangePassword extends AppCompatActivity {

    private Button BTNBackChangePassword;
    private Button BTNConfirmChangePassword;

    private EditText inputPasswordOneChangePassword;
    private EditText inputPasswordTwoChangePassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedDispatcher dispatcherBack = getOnBackPressedDispatcher();
        dispatcherBack.addCallback(ChangePassword.this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ChangePassword.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        BTNBackChangePassword = findViewById(R.id.BTNBackChangePassword);
        BTNConfirmChangePassword = findViewById(R.id.BTNConfirmChangePassword);

        inputPasswordOneChangePassword = findViewById(R.id.inputPasswordOneChangePassword);
        inputPasswordTwoChangePassword = findViewById(R.id.inputPasswordTwoChangePassword);

        BTNBackChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangePassword.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        BTNConfirmChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwordOne = inputPasswordOneChangePassword.getText().toString().trim();
                String passwordTwo = inputPasswordTwoChangePassword.getText().toString().trim();

                if (passwordOne.isEmpty() || passwordTwo.isEmpty() || passwordOne.length() < 8 || passwordTwo.length() < 8 || !passwordOne.equals(passwordTwo)) {

                    if (passwordOne.isEmpty() || passwordTwo.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Preencha os campos de senha!", Toast.LENGTH_SHORT).show();
                    }
                    if (passwordOne.length() < 8 || passwordTwo.length() < 8) {
                        Toast.makeText(getApplicationContext(), "As senhas devem ter pelo menos 8 caracteres!", Toast.LENGTH_SHORT).show();
                    }
                    if (!passwordOne.equals(passwordTwo)) {
                        Toast.makeText(getApplicationContext(), "As senhas devem ser iguais!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    BTNConfirmChangePassword.setEnabled(false);
                    changePassword(passwordOne);
                }
            }
        });
    }

    private void changePassword(String passwordOne) {
        SharedPreferences dataTokenConfig = getSharedPreferences("tokenConfig", MODE_PRIVATE);
        String accessToken = dataTokenConfig.getString("access_token", null);

        if (accessToken != null) {
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

            User newUser = new User();
            newUser.setPassword(passwordOne);

            Call<ResponseApi> call = apiService.updateUsersPassword("Bearer " + accessToken, newUser);

            call.enqueue(new Callback<ResponseApi>() {
                @Override
                public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseApi responseApi = response.body();
                        if (responseApi.getStatus().equals("success")) {
                            Intent intent = new Intent(ChangePassword.this, Profile.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        BTNConfirmChangePassword.setEnabled(true);
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

                            Intent intent = new Intent(ChangePassword.this, Login.class);
                            startActivity(intent);
                            finish();

                        } catch (IOException e) {
                            Log.e("Erro", "Falha ao ler corpo do erro de updateUsersPassword: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "ERRO(updateUsersPassword): Por favor informe o desenvolvedor!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseApi> call, Throwable t) {
                    BTNConfirmChangePassword.setEnabled(true);
                    Log.e("Erro", "Falha na requisição de updateUsersPassword: " + t.getMessage());
                    Toast.makeText(getApplicationContext(), "ERRO(updateUsersPassword): Por favor informe o desenvolvedor!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Intent intent = new Intent(ChangePassword.this, Login.class);
            startActivity(intent);
            finish();
        }

    }
}