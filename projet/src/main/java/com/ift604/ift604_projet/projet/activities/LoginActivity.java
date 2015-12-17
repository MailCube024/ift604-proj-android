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
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = (EditText)findViewById(R.id.txtUsername);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                boundService.ChangeLoginProperties(username, password);
                if(boundService.IsLogged())
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        Map<String,String> m = boundService.GetLoginInfo();
        txtUsername.setText(m.get("username"));
        txtPassword.setText(m.get("password"));
    }
}
