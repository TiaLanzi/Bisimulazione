package com.example.bisimulazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private final String TAG = "Bisimulazione";
    private EditText firstName;
    private EditText lastName;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private String userFullName = "";
    private FirebaseAuth auth;
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // initialize reference into database
        reference = database.getReference().child("users");

        // listener for button sign up
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if all fields are correct
                if (isValidName(firstName) & isValidName(lastName) & isValidName(username) & isValidEmail(email) & isValidPwd(password, confirmPassword)) {  //& validateEmail(email) & validatePwd(password)) {
                    // get text written in edittext
                    String nome = fromEditTextToString(firstName).trim();
                    //Log.i(TAG, nome);
                    String cognome = fromEditTextToString(lastName).trim();
                    //Log.i(TAG, cognome);
                    String nomeUtente = fromEditTextToString(username).trim();
                    //Log.i(TAG, nomeUtente);
                    String mail = fromEditTextToString(email).trim();
                    //Log.i(TAG, mail);
                    String pwd = fromEditTextToString(password);
                    //Log.i(TAG, pwd);
                    final User utente = new User(nome, cognome, nomeUtente, mail, pwd);
                    // check if lastName + firstName already exist
                    reference.child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            userFullName = utente.getLastName() + " " + utente.getFirstName();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Log.i(TAG, (String) dataSnapshot.getValue());
                                if (((String) dataSnapshot.getValue()).equalsIgnoreCase(userFullName)) {
                                    userFullName = userFullName + "x";
                                    Log.i(TAG, userFullName);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                    auth.createUserWithEmailAndPassword(mail, pwd)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(getApplicationContext(), getString(R.string.sign_up_successful), Toast.LENGTH_SHORT).show();
                                        //Log.d(TAG, getString(R.string.msg_create_user_success));
                                        sendData(utente, userFullName);
                                        passData(utente);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        //Toast.makeText(getApplicationContext(), getString(R.string.sign_up_failed) + task.getException(), Toast.LENGTH_SHORT).show();
                                        //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUp.this, getString(R.string.sign_up_failed),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private String fromEditTextToString(EditText editText) {
        return editText.getText().toString();
    }

    private boolean isValidName(EditText editText) {
        String string = fromEditTextToString(editText).trim();
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

    private boolean isValidEmail(EditText editText) {
        return true;
    }

    private boolean isValidPwd(EditText editText, EditText et) {
        String pass = fromEditTextToString(editText);
        String confPass = fromEditTextToString(et);

        return pass.equalsIgnoreCase(confPass);
    }

    private void sendData(User utente, String userFullName) {
        HashMap<String, String> map = new HashMap<>();
        map.put("firstName", utente.getFirstName());
        map.put("lastName", utente.getLastName());
        map.put("username", utente.getUsername());
        map.put("email", utente.getMail());
        map.put("pwd", utente.getPassword());

        // if lastName + firstName already exists --> add a counter (or something like this)
        if (userFullName.equalsIgnoreCase("")) {
            reference.child(utente.getLastName() + " " + utente.getFirstName()).setValue(map);
        } else {
            reference.child(userFullName).setValue(map);
        }
    }

    private void passData(User utente) {
        // pass email and password to Login Activity...
        Intent intent = new Intent(SignUp.this, Login.class);
        intent.putExtra("email", utente.getMail());
        intent.putExtra("pwd", utente.getPassword());
        // ... and open it ...
        startActivity(intent);
        // ... and close this activity
        finish();
    }
}