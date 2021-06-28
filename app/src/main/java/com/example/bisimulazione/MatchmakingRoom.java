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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MatchmakingRoom extends AppCompatActivity {

    private ListView listView;
    private Button createRoom;

    private List<String> roomPlayers;

    private String playerName;
    private String roomName = "";

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking_room);

        database = FirebaseDatabase.getInstance();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            playerName = user.getDisplayName();
        } else {
            playerName = getString(R.string.matchmaking_room_player_unknown_text);
        }
        roomName = playerName;
        // initialize ListView
        listView = findViewById(R.id.matchmaking_room_list_view);
        // initialize create room button
        createRoom = findViewById(R.id.matchmaking_room_create_room_button);
        // initialize room list
        roomPlayers = new ArrayList<String>();

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create room and add yourself as a player
                createRoom.setText(getString(R.string.matchmaking_room_creating_room_text));
                createRoom.setEnabled(false);
                roomName = playerName;
                reference = database.getReference("rooms/" + roomName + "/player1");
                addRoomEventListener();
                reference.setValue(playerName);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // join an existing room and add yourself as player 2
                roomName = roomPlayers.get(position);
                reference = database.getReference("rooms/" + roomName + "/player1");
                addRoomEventListener();
                reference.setValue(playerName);
            }
        });
        // show if new room is available
        addRoomEventListener();
    }

    private void addRoomEventListener() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                createRoom.setText(getString(R.string.matchmaking_room_create_room_text));
                createRoom.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(), Table.class);
                intent.putExtra("roomName", roomName);
                intent.putExtra("playerName", playerName);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                createRoom.setText(getString(R.string.matchmaking_room_creating_room_text));
                createRoom.setEnabled(false);
                Toast.makeText(MatchmakingRoom.this, getString(R.string.matchmaking_room_error_text), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRoomsEventListener() {
        reference = database.getReference("rooms/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                // show list of rooms
                roomPlayers.clear();
                Iterable<DataSnapshot> rooms = snapshot.getChildren();
                for (DataSnapshot snap : rooms) {
                    roomPlayers.add(snap.getKey());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MatchmakingRoom.this, android.R.layout.simple_list_item_1, roomPlayers);
                    listView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}