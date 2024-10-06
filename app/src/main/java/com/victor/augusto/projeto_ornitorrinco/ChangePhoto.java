package com.victor.augusto.projeto_ornitorrinco;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.content.CursorLoader;

import com.google.gson.Gson;
import com.victor.augusto.projeto_ornitorrinco.ApiConnection.Connection;
import com.victor.augusto.projeto_ornitorrinco.img.LoadImageTask;
import com.victor.augusto.projeto_ornitorrinco.model.ApiService;
import com.victor.augusto.projeto_ornitorrinco.model.ResponseApi;
import com.victor.augusto.projeto_ornitorrinco.model.RetrofitClient;
import com.victor.augusto.projeto_ornitorrinco.model.User;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePhoto extends AppCompatActivity {

    private ImageView imageViewChangePhoto;

    private Button BTNBackChangePhoto;
    private Button BNTAttachImageChangePhoto;
    private Button BTNUpdatePhotoChangePhoto;

    private ActivityResultLauncher<Intent> getImageLauncher;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_photo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedDispatcher dispatcherBack = getOnBackPressedDispatcher();
        dispatcherBack.addCallback(ChangePhoto.this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ChangePhoto.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        imageViewChangePhoto = findViewById(R.id.imageViewChangePhoto);

        BTNBackChangePhoto = findViewById(R.id.BTNBackChangePhoto);
        BNTAttachImageChangePhoto = findViewById(R.id.BNTAttachImageChangePhoto);
        BTNUpdatePhotoChangePhoto = findViewById(R.id.BTNUpdatePhotoChangePhoto);

        BTNUpdatePhotoChangePhoto.setVisibility(View.GONE);

        tokenActivity();

        getImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            imageViewChangePhoto.setImageBitmap(bitmap);
                            BTNUpdatePhotoChangePhoto.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            Toast.makeText(ChangePhoto.this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        BTNBackChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangePhoto.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        BNTAttachImageChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        BTNUpdatePhotoChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    Upload(imageUri);
                } else {
                    Toast.makeText(getApplicationContext(), "Nenhuma imagem selecionada! ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Upload(Uri imageUri) {
        SharedPreferences dataTokenConfig = getSharedPreferences("tokenConfig", MODE_PRIVATE);
        String accessToken = dataTokenConfig.getString("access_token", null);

        if (accessToken != null) {
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

            String filePath = getRealPathFromURI(imageUri);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part image = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            Call<ResponseApi> call = apiService.updateUsersPhoto("Bearer " + accessToken, image);

            call.enqueue(new Callback<ResponseApi>() {
                @Override
                public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        ResponseApi responseApi = response.body();
                        User userResponseApi = responseApi.getUser();
                        if (responseApi.getStatus().equals("success")) {
                            Toast.makeText(getApplicationContext(), "Por favor aguarde, atualizando sua foto!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ChangePhoto.this, Profile.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        try {
                            String errorResponse = response.errorBody().string();
                            Gson gson = new Gson();
                            ResponseApi erroApi = gson.fromJson(errorResponse, ResponseApi.class);

                            if (erroApi.getCode() == 400) {
                                Toast.makeText(getApplicationContext(), "Image upload failed!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChangePhoto.this, Login.class);
                                startActivity(intent);
                                finish();
                            }

                            if (erroApi.getCode() == 401) {
                                Toast.makeText(getApplicationContext(), "O \" token \" expirou ou é inválido!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChangePhoto.this, Login.class);
                                startActivity(intent);
                                finish();
                            }

                            if (erroApi.getCode() == 500) {
                                Toast.makeText(getApplicationContext(), "Imagens deve ter e máximo 1 Megabytes!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            Log.e("Erro", "Falha ao ler corpo do erro de updateUsersPhoto: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "ERRO(updateUsersPhoto): Por favor informe o desenvolvedor!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseApi> call, Throwable t) {
                    Log.e("Erro", "Falha na requisição de updateUsersPhoto: " + t.getMessage());
                    Toast.makeText(getApplicationContext(), "ERRO(updateUsersPhoto): Por favor informe o desenvolvedor!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Intent intent = new Intent(ChangePhoto.this, Login.class);
            startActivity(intent);
            finish();
        }
    }


    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            openGallery();
        }

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getImageLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permissão negada para acessar a galeria", Toast.LENGTH_SHORT).show();
            }
        }
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
                            String linkConnection = Connection.connectionAPI();
                            linkConnection = linkConnection.substring(0, linkConnection.length() - 1);
                            new LoadImageTask(imageViewChangePhoto).execute(linkConnection + userResponseApi.getPhoto());
                        }
                    } else {
                        Intent intent = new Intent(ChangePhoto.this, Login.class);
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
            Intent intent = new Intent(ChangePhoto.this, Login.class);
            startActivity(intent);
            finish();
        }
    }
}