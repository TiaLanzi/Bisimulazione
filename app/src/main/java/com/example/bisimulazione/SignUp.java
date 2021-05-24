package com.example.bisimulazione;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.bisimulazione.models.User;

public class SignUp extends AppCompatActivity {

    private final String TAG = "Bisimulazione";
    private EditText firstName;
    private EditText lastName;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // initialize views
        Button signUp = findViewById(R.id.sign_up_sign_up_button);
        firstName = findViewById(R.id.sign_up_name_input);
        lastName = findViewById(R.id.sign_up_surname_input);
        username = findViewById(R.id.sign_up_username_input);
        email = findViewById(R.id.sign_up_mail_input);
        password = findViewById(R.id.sign_up_pwd_input);
        confirmPassword = findViewById(R.id.sign_up_confirm_pwd_input);

        // listener for button sign up
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if all fields are correct
                if (validate(firstName) & validate(lastName) & validate(username) & validateEmail(email) & validatePwd(password)) {
                    // get text written in edittext
                    String nome = firstName.getText().toString().trim();
                    String cognome = lastName.getText().toString().trim();
                    String nomeUtente = username.getText().toString().trim();
                    String mail = email.getText().toString().trim();
                    String pwd = password.getText().toString().trim();
                    final User user = new User(nome, cognome, nomeUtente, mail, pwd);
                }
            }
        });

    }

    private boolean validate(EditText editText) {
        String string = editText.getText().toString().trim();
        // firstName, lastName and username must be longer than one single character and cannot be empty
        if (string.equalsIgnoreCase("")) {
            editText.setError(getString(R.string.error_empty_edittext));
            return false;
        } else if (string.length() < 2) {
            editText.setError(getString(R.string.error_too_short_edittext));
            return false;
        } else {
            return true;
        }
    }
}