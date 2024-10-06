package com.victor.augusto.projeto_ornitorrinco;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class About extends AppCompatActivity {

    private Button BTNLinkGitHub;
    private Button BTNLinkLinkedIn;
    private Button BTNLinkPortfolio;
    private Button BTNLinkCurriculum;

    private Button BTNBackAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String superAdm = intent.getStringExtra("adm");

        OnBackPressedDispatcher dispatcherBack = getOnBackPressedDispatcher();
        dispatcherBack.addCallback(About.this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (superAdm != null ){
                    Intent intent = new Intent(About.this, Profile.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(About.this, Information.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        BTNLinkGitHub = findViewById(R.id.BTNLinkGitHub);
        BTNLinkLinkedIn = findViewById(R.id.BTNLinkLinkedIn);
        BTNLinkPortfolio = findViewById(R.id.BTNLinkPortfolio);
        BTNLinkCurriculum = findViewById(R.id.BTNLinkCurriculum);

        BTNBackAbout = findViewById(R.id.BTNBackAbout);

        BTNLinkGitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Aguarde... Abrindo Link.", Toast.LENGTH_LONG).show();
                String urlGitHub = "https://github.com/VictorAugustoRodriguesGomes";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlGitHub));
                startActivity(intent);
            }
        });

        BTNLinkLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Aguarde... Abrindo Link.", Toast.LENGTH_LONG).show();
                String urlLinkedIn = "https://www.linkedin.com/in/victor-augusto-desenvolvedor/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlLinkedIn));
                startActivity(intent);
            }
        });

        BTNLinkPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Aguarde... Abrindo Link.", Toast.LENGTH_LONG).show();
                String urlPortfolio = "https://victor-augusto.netlify.app/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlPortfolio));
                startActivity(intent);
            }
        });

        BTNLinkCurriculum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Aguarde... Abrindo Link.", Toast.LENGTH_LONG).show();
                String urlCurriculum = "https://github.com/VictorAugustoRodriguesGomes/VictorAugustoRodriguesGomes/blob/main/src/doc/Victor%20Augusto%20Rodrigues%20Gomes%20-%20curr%C3%ADculo.pdf";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlCurriculum));
                startActivity(intent);
            }
        });

        BTNBackAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (superAdm != null ){
                    Intent intent = new Intent(About.this, Profile.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(About.this, Information.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}