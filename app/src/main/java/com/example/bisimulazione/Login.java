package com.example.bisimulazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class Login extends AppCompatActivity {

    private static final String TAG = "Bisimulazione";
    private EditText username;
    private EditText pwd;
    private CheckBox rememberMe;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Boolean saveLoginData;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize views
        username = findViewById(R.id.login_username_input);
        pwd = findViewById(R.id.login_password_input);
        rememberMe = findViewById(R.id.login_remember_me_checkbox);

        TextView forgotPwd = findViewById(R.id.login_click_here_link);
        Button login = findViewById(R.id.login_sign_in_button);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String email = extras.getString("email");
            String password = extras.getString("pwd");
            username.setText(email);
            pwd.setText(password);
            rememberMe.setChecked(true);
        }

        // initialize shared preferences
        sharedPreferences = this.getSharedPreferences("sharedPreferencesLogin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
        // get shared prefences
        saveLoginData = sharedPreferences.getBoolean("saveLoginData", false);
        // if previously was set to checked, set with login data
        if (saveLoginData) {
            username.setText(sharedPreferences.getString("username", ""));
            username.setText(sharedPreferences.getString("pwd", ""));
            rememberMe.setChecked(true);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fromEditTextToString(username).isEmpty() && !fromEditTextToString(pwd).isEmpty()) {
                    auth.signInWithEmailAndPassword(fromEditTextToString(username), fromEditTextToString(pwd))
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // if checked write in shared preferences login data
                                    if (rememberMe.isChecked()) {
                                        editor.putBoolean("saveLoginData", true);
                                        editor.putString("username", fromEditTextToString(username));
                                        editor.putString("pwd", fromEditTextToString(pwd));
                                    } else {
                                        editor.clear();
                                    }
                                    editor.commit();
                                    Toast.makeText(Login.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private String fromEditTextToString(EditText editText) {
        return editText.getText().toString();
    }
}