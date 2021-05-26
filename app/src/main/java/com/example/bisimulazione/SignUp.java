package com.example.bisimulazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bisimulazione.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    private final String TAG = "Bisimulazione";
    private EditText firstName;
    private EditText lastName;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;

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

        // initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // initialize database
        database = FirebaseDatabase.getInstance();

        // initialize reference into database
        reference = database.getReference("users");

        // listener for button sign up
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if all fields are correct
                if (validate(firstName) & validate(lastName) & validate(username)) {  //& validateEmail(email) & validatePwd(password)) {
                    // get text written in edittext
                    String nome = firstName.getText().toString().trim();
                    Log.i(TAG, nome);
                    String cognome = lastName.getText().toString().trim();
                    Log.i(TAG, cognome);
                    String nomeUtente = username.getText().toString().trim();
                    Log.i(TAG, nomeUtente);
                    String mail = email.getText().toString().trim();
                    Log.i(TAG, mail);
                    String pwd = password.getText().toString().trim();
                    Log.i(TAG, pwd);
                    final User utente = new User(nome, cognome, nomeUtente, mail, pwd);
                    auth.createUserWithEmailAndPassword(mail, pwd)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        // Log.d(TAG, getString(R.string.msg_create_user_success));
                                        FirebaseUser user = auth.getCurrentUser();
                                        sendData(utente);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUp.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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

    private void sendData(User user) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                reference.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });
    }
}