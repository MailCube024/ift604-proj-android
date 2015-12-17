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
import java.util.Map;

/**
 * Created by Benoit on 2015-12-15.
 */
public class LoginActivity extends CommunicationActivity {

    EditText txtUsername;
    EditText txtPassword;
    EditText txtEmail;
    Spinner dropRegion;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = (EditText)findViewById(R.id.txtUsername);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        dropRegion = (Spinner)findViewById(R.id.dropRegion);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                String email = txtEmail.getText().toString();
                String region = String.valueOf(dropRegion.getSelectedItemPosition());

                boundService.ChangeLoginProperties(username, password, email, region);
                if(boundService.IsLogged())
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        Map<String,String> m = boundService.GetLoginInfo();
        Map<String,String> regions = boundService.GetRegions();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_item, new ArrayList<>(regions.values()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropRegion.setAdapter(adapter);

        int position = 0;
        for(int i = 0; i < regions.keySet().toArray().length; ++i) {
            if(regions.keySet().toArray()[i].equals(m.get("region")))
                position = i;
        }

        txtUsername.setText(m.get("username"));
        txtPassword.setText(m.get("password"));
        txtEmail.setText(m.get("email"));
        dropRegion.setSelection(position);
    }
}
