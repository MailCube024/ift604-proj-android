package com.ift604.ift604_projet.projet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ift604.ift604_projet.projet.R;

public class MainActivity extends CommunicationActivity {

    Button btnRanking;
    Button btnSettings;
//    Button btnStatistics;
    Button btnEvent;
    TextView txtUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRanking = (Button)findViewById(R.id.btnRanking);
        btnSettings = (Button)findViewById(R.id.btnSettings);
//        btnStatistics = (Button)findViewById(R.id.btnStatistics);
        btnEvent = (Button)findViewById(R.id.btnEvent);
        txtUsername = (TextView)findViewById(R.id.txtUsername);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });

        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RankingActivity.class));
            }
        });

//        btnStatistics.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
//            }
//        });

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

        boolean logged = boundService.Login();

        if(logged) {
            txtUsername.setText(boundService.GetLoginInfo().get("username"));
        } else {
            txtUsername.setText("");
        }
        btnRanking.setEnabled(logged);
//        btnStatistics.setEnabled(logged);
        btnEvent.setEnabled(logged);
    }
}
