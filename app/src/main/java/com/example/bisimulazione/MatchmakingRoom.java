package com.example.bisimulazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchmakingRoom extends AppCompatActivity {

    private ListView listView;
    private Button createRoom;

    private List<String> roomsList;

    private String playerName;
    private String roomName;

    private DatabaseReference roomsRef;
    private DatabaseReference roomNameRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking_room);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            playerName = user.getDisplayName();
        } else {
            playerName = getString(R.string.matchmaking_room_player_unknown_text);
        }

        roomName = playerName;

        listView = findViewById(R.id.matchmaking_room_list_view);
        createRoom = findViewById(R.id.matchmaking_room_create_room_button);

        roomsList = new ArrayList<>();

        roomsRef = database.getReference().child("rooms");

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create room and add yourself as first player
                createRoom.setText(getString(R.string.matchmaking_room_creating_room_text));
                createRoom.setEnabled(false);
                roomNameRef = roomsRef.child(roomName);
                sendDataP1(roomNameRef, playerName);
                startActivity();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // join an existing room and add yourself as player two
                String roomN = roomsList.get(position);
                // Log.i(TAG, "Room name complete " + roomN);
                roomName = remove(roomN);
                // Log.i(TAG, "Room name " + roomName);
                //roomName = roomsList.get(position);
                roomsRef = roomsRef.child(roomName);
                // Log.i(TAG, "Room ref " + String.valueOf(roomsRef));
                sendDataP2(roomsRef, playerName);
                startActivity();
            }
        });
        // show new rooms created
        getRoomsList();
    }

    private void getRoomsList() {
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                roomsList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    roomsList.add(getString(R.string.matchmaking_room_property) + " " + data.getKey());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MatchmakingRoom.this, android.R.layout.simple_list_item_1, roomsList);
                    listView.setAdapter(adapter);
                    // Log.i(TAG, String.valueOf(listView.isShown()));
                }
            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });
    }

    private void sendDataP1(DatabaseReference reference, String playerName) {
        reference.child("Player 1/").setValue(playerName);
    }

    private void sendDataP2(DatabaseReference reference, String playerName) {
        reference.child("Player 2/").setValue(playerName);
    }

    private void startActivity() {
        Intent intent = new Intent(MatchmakingRoom.this, Third.class);
        startActivity(intent);
        finish();
    }

    private static String remove(String roomN) {
        // WARNING : TRANSLATION!!
        String remove = "Room of ";
        return roomN.replace(remove, "");
    }
}