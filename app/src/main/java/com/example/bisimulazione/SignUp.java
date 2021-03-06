package com.example.bisimulazione;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bisimulazione.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

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
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.sign_up));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        // initialize shared preferences and persistent counter
        sharedPreferences = this.getSharedPreferences("sharedPreferencesCounter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // to reset counter
        //editor.putInt("Counter", 1);
        editor.apply();

        // listener for button sign up
        signUp.setOnClickListener(v -> {
            // check if all fields are correct
            if (isValidName(firstName) & isValidName(lastName) & isValidName(username) & isValidPwd(password, confirmPassword)) { // & isValidEmail(email) & isValidPwd(password, confirmPassword)) {
                // get text written in edittext
                String nome = fromEditTextToString(firstName).trim();
                String cognome = fromEditTextToString(lastName).trim();
                String nomeUtente = fromEditTextToString(username).trim();
                String mail = fromEditTextToString(email).trim();
                String pwd = fromEditTextToString(password);
                final User utente = new User(nome, cognome, nomeUtente, mail, pwd);
                // check if lastName + firstName already exist
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        userFullName = utente.getLastName() + " " + utente.getFirstName();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot != null) {
                                String userSearch = Objects.requireNonNull(dataSnapshot.child("lastName").getValue()).toString() + " " + Objects.requireNonNull(dataSnapshot.child("firstName").getValue()).toString();
                                if (userSearch.equalsIgnoreCase(userFullName)) {
                                    int count = sharedPreferences.getInt("Counter", 0);
                                    String counter = String.valueOf(sharedPreferences.getInt("Counter", 0));
                                    userFullName = userFullName + " " + counter;
                                    count++;
                                    editor.putInt("Counter", count);
                                    editor.apply();
                                }
                            } else {
                                Log.i(TAG, "Data snapshot is null");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                auth.createUserWithEmailAndPassword(mail, pwd).
                        addOnCompleteListener(SignUp.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = auth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(utente.getLastName() + ", " + utente.getFirstName()).build();
                                assert user != null;
                                user.updateProfile(profileUpdates).addOnCompleteListener(Task::isSuccessful);
                                Toast.makeText(getApplicationContext(), getString(R.string.sign_up_successful), Toast.LENGTH_SHORT).show();
                                sendData(utente, userFullName);
                                passData(utente);
                            } else {
                                // If sign in fails, display a message to the user.
                                //Toast.makeText(getApplicationContext(), getString(R.string.sign_up_failed) + task.getException(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(SignUp.this, getString(R.string.sign_up_failed),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
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

    private boolean isValidPwd(EditText editText, EditText et) {
        String pass = fromEditTextToString(editText);
        String confPass = fromEditTextToString(et);

        if (pass.equalsIgnoreCase(confPass)) {
            if (pass.length() <= 4) {
                editText.setError(getString(R.string.error_too_short_edittext));
                et.setError(getString(R.string.error_too_short_edittext));
                return false;
            } else {
                return true;
            }
        } else {
            editText.setError(getString(R.string.sign_up_pwds_dont_match));
            return false;
        }
    }

    private void sendData(User utente, String userFullName) {
        HashMap<String, String> map = new HashMap<>();
        map.put("firstName", utente.getFirstName());
        map.put("lastName", utente.getLastName());
        map.put("username", utente.getUsername());
        map.put("email", utente.getMail());
        map.put("pwd", utente.getPassword());
        map.put("winCount", String.valueOf(0));
        map.put("loseCount", String.valueOf(0));

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
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(SignUp.this, Login.class);
        startActivity(intent);
        this.finish();
        return true;
    }
}