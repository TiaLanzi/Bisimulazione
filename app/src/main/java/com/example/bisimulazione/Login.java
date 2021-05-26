package com.example.bisimulazione;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private static final String TAG = "Bisimulazione";
    private EditText pwd;
    private CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username = findViewById(R.id.login_username_input);
        pwd = findViewById(R.id.login_password_input);
        rememberMe = findViewById(R.id.login_remember_me_checkbox);

        TextView forgotPwd = findViewById(R.id.login_click_here_link);
        Button login = findViewById(R.id.login_sign_in_button);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        /*login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validEmail(user))
            }
        });*/
    }
}