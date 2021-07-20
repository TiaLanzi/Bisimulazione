package com.example.bisimulazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import interfaces.CallbackGameCount;

public class MyAccount extends AppCompatActivity implements CallbackGameCount {

    private final String TAG = "Bisimulazione";

    private FirebaseUser user;

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
        user = auth.getCurrentUser();
        if (user != null) {
            email.setText(user.getEmail());
            String fullName = user.getDisplayName();
            assert fullName != null;
            int index = fullName.indexOf(",");
            cognome = fullName.substring(0, index);
            nome = fullName.substring((index + 1));
            firstName.setText(nome);
            lastName.setText(cognome);
        }

        setGameCount();
    }

    private void setGameCount() {
        getGameCount(new CallbackGameCount() {
            @Override
            public void onCallbackWinCount(String value) {
                winCount.setText(value);
            }

            @Override
            public void onCallbackLoseCount(String value) {
                loseCount.setText(value);
            }
        });
    }

    private void getGameCount(CallbackGameCount callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.i(TAG, "Key " + dataSnapshot.getKey());
                    String fullName = Objects.requireNonNull(user.getDisplayName()).replace(",", "");
                    if (Objects.requireNonNull(dataSnapshot.getKey()).equalsIgnoreCase(fullName)) {
                        String win = Objects.requireNonNull(dataSnapshot.child("winCount").getValue()).toString();
                        callback.onCallbackWinCount(win);
                        String lose = Objects.requireNonNull(dataSnapshot.child("loseCount").getValue()).toString();
                        callback.onCallbackLoseCount(lose);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCallbackWinCount(String value) {

    }

    @Override
    public void onCallbackLoseCount(String value) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }
}