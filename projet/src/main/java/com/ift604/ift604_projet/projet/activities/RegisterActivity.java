package com.ift604.ift604_projet.projet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.ift604.ift604_projet.projet.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Benoit on 2015-12-15.
 */
public class RegisterActivity extends CommunicationActivity {

    EditText txtUsername;
    EditText txtPassword;
    EditText txtPassword2;
    EditText txtEmail;
    Spinner dropRegion;
    Button btnRegister;

    Map<String,String> regionsMap;
    List<String> regions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtUsername = (EditText)findViewById(R.id.txtUsername);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        txtPassword2 = (EditText)findViewById(R.id.txtPassword2);
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        dropRegion = (Spinner)findViewById(R.id.dropRegion);
        btnRegister = (Button)findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                String password2 = txtPassword2.getText().toString();
                String email = txtEmail.getText().toString();
                String region = null;
                String regionValue = regions.get(dropRegion.getSelectedItemPosition());
                for(Map.Entry<String,String> e : regionsMap.entrySet()) {
                    if(e.getValue().equals(regionValue))
                        region = e.getKey();
                }

                boundService.Register(username, password, password2, email, region);
                if(boundService.IsLogged())
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        regionsMap = boundService.GetRegions();
        regions = new ArrayList<>(regionsMap.values());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, regions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropRegion.setAdapter(adapter);
    }
}
