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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class Login extends AppCompatActivity {

    private static final String TAG = "Bisimulazione";
    private EditText resetEmail;
    private Button reset;
    private EditText username;
    private EditText pwd;
    private CheckBox rememberMe;
    private SharedPreferences.Editor editor;
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

        TextView notMemberYet = findViewById(R.id.login_not_a_member_yet_sign_up_here);
        notMemberYet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String email = extras.getString("email");
            String password = extras.getString("pwd");
            username.setText(email);
            pwd.setText(password);
            rememberMe.setChecked(true);
        }

        // initialize shared preferences
        SharedPreferences sharedPreferences = this.getSharedPreferences("sharedPreferencesLogin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // to reset shared preferences
        //editor.putBoolean("saveLoginData", false);
        // editor.remove("username");
        // editor.remove("pwd");
        editor.apply();
        // get shared preferences
        boolean saveLoginData = sharedPreferences.getBoolean("saveLoginData", false);
        // if previously was set to checked, set with login data
        if (saveLoginData) {
            username.setText(sharedPreferences.getString("username", ""));
            pwd.setText(sharedPreferences.getString("pwd", ""));
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

        forgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initialize and add bottom sheet
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Login.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_reset_pwd);
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.show();
                // initialize views in bottom sheet
                resetEmail = bottomSheetDialog.findViewById(R.id.bottom_sheet_reset_pwd_input);
                reset = bottomSheetDialog.findViewById(R.id.bottom_sheet_reset_pwd_reset_button);
                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        auth.sendPasswordResetEmail(fromEditTextToString(resetEmail))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Login.this, getString(R.string.login_bottom_sheet_mail_sent_to), Toast.LENGTH_LONG).show();
                                            username.setText(fromEditTextToString(resetEmail));
                                            rememberMe.setChecked(true);
                                            bottomSheetDialog.dismiss();
                                        }
                                    }
                                });

                    }
                });
            }
        });
    }

    private String fromEditTextToString(EditText editText) {
        return editText.getText().toString();
    }
}