package com.example.bisimulazione;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivePlayers extends AppCompatActivity {

    private ListView listView;
    private List<String> playersList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_players);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.active_players));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.active_players_list_view);

        playersList = new ArrayList<>();

        getPlayersList();
    }

    private void getPlayersList() {
        FirebaseDatabase.getInstance().getReference().child("players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                playersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        playersList.add(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                    }
                    adapter = new ArrayAdapter<>(ActivePlayers.this, android.R.layout.simple_list_item_1, playersList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

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
}