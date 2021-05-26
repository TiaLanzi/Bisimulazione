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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private final String TAG = "Bisimulazione";
    private EditText firstName;
    private EditText lastName;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
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
        reference = database.getReference("users");

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
        String email = fromEditTextToString(editText).trim();
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    private boolean isValidPwd(EditText editText, EditText et) {
        String pass = fromEditTextToString(editText);
        String confPass = fromEditTextToString(et);

        if (!pass.equalsIgnoreCase(confPass)) {
            return false;
        }

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher m = p.matcher(pass);

        // Return if the password
        // matched the ReGex
        return m.matches();
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