package com.ift604.ift604_projet.projet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.ift604.ift604_projet.projet.R;

import java.util.Map;

/**
 * Created by Benoit on 2015-12-15.
 */
public class SettingActivity extends CommunicationActivity {
    EditText txtServer;
    EditText txtPort;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtServer = (EditText)findViewById(R.id.txtServer);
        txtPort = (EditText)findViewById(R.id.txtPort);
        btnSave = (Button)findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String server = txtServer.getText().toString();
                String port = txtPort.getText().toString();

                boundService.ChangeSettingsProperties(server, port);
                if(boundService.IsLogged())
                    startActivity(new Intent(SettingActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        Map<String,String> m = boundService.GetSettings();

        txtServer.setText(m.get("server"));
        txtPort.setText(m.get("port"));
    }
}
