package com.victor.augusto.projeto_ornitorrinco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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

public class Login extends AppCompatActivity {

    private Button BTNLogin;
    private Button BTNSignUpLogin;

    private TextView BTNForgotPasswordLogin;

    private EditText inputEmailLogin;
    private EditText inputPasswordLogin;

    private CheckBox BTNCheckBoxLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BTNSignUpLogin = findViewById(R.id.BTNSignUpLogin);
        BTNForgotPasswordLogin = findViewById(R.id.BTNForgotPasswordLogin);

        BTNLogin = findViewById(R.id.BTNEnterLogin);

        inputEmailLogin = findViewById(R.id.inputEmailForgotPassword);
        inputPasswordLogin = findViewById(R.id.inputPasswordLogin);

        BTNCheckBoxLogin = findViewById(R.id.BTNCheckBoxLogin);

        checkBoxActivity();

        BTNSignUpLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        BTNForgotPasswordLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        BTNLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnected(Login.this)) {
                    String emailLogin = inputEmailLogin.getText().toString().trim();
                    String passwordLogin = inputPasswordLogin.getText().toString().trim();

                    if (emailLogin == null || emailLogin.isEmpty() || passwordLogin == null || passwordLogin.isEmpty()) {
                        if (emailLogin == null || emailLogin.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Preencha o campo E-mail!", Toast.LENGTH_SHORT).show();
                        }

                        if (passwordLogin == null || passwordLogin.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Preencha o campo senha!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        BTNLogin.setEnabled(false);
                        loginUser(emailLogin, passwordLogin);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Conexão com a internet necessária!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void checkBoxActivity() {
        SharedPreferences dataAccessLoginConfig = getSharedPreferences("accessLoginConfig", MODE_PRIVATE);
        String access_Check_Box = dataAccessLoginConfig.getString("access_Check_Box", null);

        if (access_Check_Box != null) {
            String access_Login = dataAccessLoginConfig.getString("access_Login", "");
            String access_password = dataAccessLoginConfig.getString("access_password", "");
            BTNCheckBoxLogin.setChecked(true);
            inputEmailLogin.setText(access_Login.trim());
            inputPasswordLogin.setText(access_password.trim());
        }
    }

    private void loginUser(String emailLogin, String passwordLogin) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        User newUser = new User();
        newUser.setEmail(emailLogin);
        newUser.setPassword(passwordLogin);

        apiService.signInWithEmailAndPassword(newUser).enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ResponseApi responseApi = response.body();
                    User userResponseApi = responseApi.getUser();

                    if (responseApi.getStatus().equals("success")) {
                        SharedPreferences dataAccessLoginConfig = getSharedPreferences("accessLoginConfig", MODE_PRIVATE);
                        SharedPreferences.Editor editorAccessLoginConfig = dataAccessLoginConfig.edit();

                        if (BTNCheckBoxLogin.isChecked()) {
                            editorAccessLoginConfig.putString("access_Login", emailLogin);
                            editorAccessLoginConfig.putString("access_Check_Box", "true");
                            editorAccessLoginConfig.putString("access_password", passwordLogin);
                            editorAccessLoginConfig.apply();
                        } else {
                            editorAccessLoginConfig.clear();
                            editorAccessLoginConfig.apply();
                        }

                        BTNLogin.setEnabled(true);
                        Intent intent = new Intent(Login.this, VerifyCode2FA.class);
                        intent.putExtra("userUID", userResponseApi.getUid());
                        startActivity(intent);
                        finish();
                    }
                } else {
                    BTNLogin.setEnabled(true);
                    try {
                        String errorResponse = response.errorBody().string();
                        Gson gson = new Gson();
                        ResponseApi erroApi = gson.fromJson(errorResponse, ResponseApi.class);

                        if (erroApi.getCode() == 404) {
                            Toast.makeText(getApplicationContext(), "E-mail ou senha incorretos!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        Log.e("Erro", "Falha ao ler corpo do erro de signInWithEmailAndPassword: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "ERRO(signInWithEmailAndPassword): Por favor informe o desenvolvedor !", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                BTNLogin.setEnabled(true);
                Log.e("Erro", "Falha na requisição de signInWithEmailAndPassword: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "ERRO(signInWithEmailAndPassword): Por favor informe o desenvolvedor !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}