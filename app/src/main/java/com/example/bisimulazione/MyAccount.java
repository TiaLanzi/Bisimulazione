package com.example.bisimulazione;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import interfaces.CallbackLoseCount;
import interfaces.CallbackWinCount;

public class MyAccount extends AppCompatActivity implements CallbackWinCount, CallbackLoseCount {

    private String playerName;

    private TextView winCount;
    private TextView loseCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.my_account));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView firstName = findViewById(R.id.my_account_first_name);
        TextView lastName = findViewById(R.id.my_account_last_name);
        TextView email = findViewById(R.id.my_account_email);
        winCount = findViewById(R.id.my_account_win_count);
        loseCount = findViewById(R.id.my_account_lose_count);

        String nome;
        String cognome;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            email.setText(user.getEmail());
            playerName = user.getDisplayName();
            assert playerName != null;
            int index = playerName.indexOf(",");
            cognome = playerName.substring(0, index);
            nome = playerName.substring((index + 1));
            firstName.setText(nome);
            lastName.setText(cognome);
        }

        setWinCount();
        setLoseCount();
    }

    private void setWinCount() {
        getWinCount(new CallbackWinCount() {
            @Override
            public void onCallbackWinCount(String value) {
                if (value != null) {
                    winCount.setText(value);
                }
            }
        });
    }

    private void getWinCount(CallbackWinCount callback) {
        FirebaseDatabase.getInstance().getReference().child("users").child(playerName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("winCount").getValue() != null) {
                    callback.onCallbackWinCount(Objects.requireNonNull(snapshot.child("winCount").getValue()).toString().trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLoseCount() {
        getLoseCount(new CallbackLoseCount() {
            @Override
            public void onCallbackLoseCount(String value) {
                if (value != null) {
                    loseCount.setText(value);
                }
            }
        });
    }

    private void getLoseCount(CallbackLoseCount callback) {
        FirebaseDatabase.getInstance().getReference().child("users").child(playerName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("loseCount").getValue() != null) {
                    callback.onCallbackLoseCount(Objects.requireNonNull(snapshot.child("loseCount").getValue()).toString().trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    @Override
    public void onCallbackLoseCount(String value) {

    }

    @Override
    public void onCallbackWinCount(String value) {

    }
}