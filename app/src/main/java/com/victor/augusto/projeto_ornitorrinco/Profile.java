package com.victor.augusto.projeto_ornitorrinco;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.victor.augusto.projeto_ornitorrinco.ApiConnection.Connection;
import com.victor.augusto.projeto_ornitorrinco.img.LoadImageTask;
import com.victor.augusto.projeto_ornitorrinco.model.ApiService;
import com.victor.augusto.projeto_ornitorrinco.model.ResponseApi;
import com.victor.augusto.projeto_ornitorrinco.model.RetrofitClient;
import com.victor.augusto.projeto_ornitorrinco.model.User;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {

    private ImageView imageViewProfileUser;

    private TextView textViewNameProfileUser;
    private TextView textViewEmailProfileUser;

    private Button BTNChangeNameProfile;
    private Button BTNChangePhotoProfile;
    private Button BTNChangePasswordProfile;
    private Button BTNDeleteAccountProfile;
    private Button BTNLogOutProfile;
    private Button BTNAboutProfile;

    private boolean doubleClick = false;
    private boolean doubleClickDelete = false;
    private boolean doubleClickLogOut = false;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedDispatcher dispatcherBack = getOnBackPressedDispatcher();
        dispatcherBack.addCallback(Profile.this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleClick) {
                    if (backToast != null) backToast.cancel();
                    Logout();
                } else {
                    doubleClick = true;
                    backToast = Toast.makeText(getApplicationContext(), "Pressione 2 vezes para Logout!", Toast.LENGTH_SHORT);
                    backToast.show();

                    new android.os.Handler().postDelayed(() -> doubleClick = false, 3000);
                }
            }
        });

        imageViewProfileUser = findViewById(R.id.imageViewProfileUser);

        textViewNameProfileUser = findViewById(R.id.textViewNameProfileUser);
        textViewEmailProfileUser = findViewById(R.id.textViewEmailProfileUser);

        BTNChangeNameProfile = findViewById(R.id.BTNChangeNameProfile);
        BTNChangePhotoProfile = findViewById(R.id.BTNChangePhotoProfile);
        BTNChangePasswordProfile = findViewById(R.id.BTNChangePasswordProfile);
        BTNDeleteAccountProfile = findViewById(R.id.BTNDeleteAccountProfile);
        BTNLogOutProfile = findViewById(R.id.BTNLogOutProfile);
        BTNAboutProfile = findViewById(R.id.BTNAboutProfile);

        tokenActivity();

        BTNChangeNameProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, ChangeName.class);
                startActivity(intent);
                finish();
            }
        });

        BTNChangePhotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, ChangePhoto.class);
                startActivity(intent);
                finish();
            }
        });

        BTNChangePasswordProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, ChangePassword.class);
                startActivity(intent);
                finish();
            }
        });

        BTNDeleteAccountProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (doubleClickDelete) {
                    if (backToast != null) backToast.cancel();
                    DeleteAccount();
                } else {
                    doubleClickDelete = true;
                    backToast = Toast.makeText(getApplicationContext(), "Pressione 2 vezes para deletar sua conta!", Toast.LENGTH_SHORT);
                    backToast.show();

                    new android.os.Handler().postDelayed(() -> doubleClickDelete = false, 3000);
                }
            }
        });

        BTNLogOutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (doubleClickLogOut) {
                    if (backToast != null) backToast.cancel();
                    Logout();
                } else {
                    doubleClickLogOut = true;
                    backToast = Toast.makeText(getApplicationContext(), "Pressione 2 vezes para Logout!", Toast.LENGTH_SHORT);
                    backToast.show();

                    new android.os.Handler().postDelayed(() -> doubleClickLogOut = false, 3000);
                }
            }
        });

        BTNAboutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, About.class);
                intent.putExtra("adm", "adm");
                startActivity(intent);
                finish();
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
                        User userResponseApi = responseApi.getUser();
                        if (responseApi.getStatus().equals("success")) {
                            textViewNameProfileUser.setText(userResponseApi.getDisplayName());
                            textViewEmailProfileUser.setText(userResponseApi.getEmail());
                            String linkConnection = Connection.connectionAPI();
                            linkConnection = linkConnection.substring(0, linkConnection.length() - 1);
                            new LoadImageTask(imageViewProfileUser).execute(linkConnection + userResponseApi.getPhoto());
                        }
                    } else {
                        Intent intent = new Intent(Profile.this, Login.class);
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
            Intent intent = new Intent(Profile.this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    private void DeleteAccount() {
        SharedPreferences dataTokenConfig = getSharedPreferences("tokenConfig", MODE_PRIVATE);
        String accessToken = dataTokenConfig.getString("access_token", null);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseApi> call = apiService.deleteUsers("Bearer " + accessToken);

        call.enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseApi responseApi = response.body();
                    if (responseApi.getStatus().equals("success")) {
                        SharedPreferences.Editor editor = dataTokenConfig.edit();
                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(Profile.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Gson gson = new Gson();
                        ResponseApi erroApi = gson.fromJson(errorResponse, ResponseApi.class);
                        if (erroApi.getCode() == 401) {
                            Toast.makeText(getApplicationContext(), "O \" token \" expirou ou é inválido!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Profile.this, Login.class);
                            startActivity(intent);
                            finish();
                        }

                        if (erroApi.getCode() == 404) {
                            Toast.makeText(getApplicationContext(), "User inexistente ou inválido!", Toast.LENGTH_SHORT).show();
                            Log.e("Erro", "Falha ao ler corpo do erro de deleteUsers ");
                            Toast.makeText(getApplicationContext(), "ERRO(deleteUsers): Por favor informe o desenvolvedor!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        Log.e("Erro", "Falha ao ler corpo do erro de deleteUsers: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "ERRO(deleteUsers): Por favor informe o desenvolvedor!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                Log.e("Erro", "Falha na requisição de deleteUsers: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "ERRO(deleteUsers): Por favor informe o desenvolvedor!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Logout() {
        SharedPreferences dataTokenConfig = getSharedPreferences("tokenConfig", MODE_PRIVATE);
        SharedPreferences.Editor editor = dataTokenConfig.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(Profile.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}