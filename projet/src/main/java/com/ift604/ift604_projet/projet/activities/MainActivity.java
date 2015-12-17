package com.ift604.ift604_projet.projet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.ift604.ift604_projet.projet.MapActivity;
import com.ift604.ift604_projet.projet.R;

public class MainActivity extends CommunicationActivity {

    Button btnLogin;
    Button btnRanking;
    Button btnSettings;
    Button btnStatistics;
    Button btnEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnRanking = (Button)findViewById(R.id.btnRanking);
        btnSettings = (Button)findViewById(R.id.btnSettings);
        btnStatistics = (Button)findViewById(R.id.btnStatistics);
        btnEvent = (Button)findViewById(R.id.btnEvent);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RankingActivity.class));
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });

        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
            }
        });

        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }
}