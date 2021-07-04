package com.example.bisimulazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private static final String TAG = "Bisimulazione";

    private ListView listView;
    private Button createRoom;

    private List<String> roomsList;
    private ArrayAdapter<String> adapter;

    private String playerName;
    private String roomName;
    private String specialColour;
    private String getColor;

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
                roomNameRef.child("show").setValue("true");
                specialColour = setColour(roomNameRef);
                sendDataP1(roomNameRef, playerName);
                startActivity(roomName, true, specialColour);
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
                // not to show when room is full
                roomNameRef = roomsRef;
                roomNameRef.child("show").setValue("false");
                specialColour = getColour(roomNameRef);
                Log.i(TAG, "IL COLORE[0] QUI 3: " + specialColour);
                startActivity(roomName, false, specialColour);
                finish();
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
                    adapter = new ArrayAdapter<>(MatchmakingRoom.this, android.R.layout.simple_list_item_1, roomsList);
                    listView.setAdapter(adapter);
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

    private void startActivity(String roomName, boolean player1, String specialColour) {
        Intent intent = new Intent(MatchmakingRoom.this, Table.class);
        intent.putExtra("roomName", roomName);
        intent.putExtra("player 1", player1);
        intent.putExtra("specialColour", specialColour);
        startActivity(intent);
        finish();
    }

    private String remove(String roomN) {
        String remove = getString(R.string.matchmaking_room_property) + " ";
        return roomN.replace(remove, "");
    }

    private int getNumber() {
        return (int) (Math.random() * 4);
    }

    private String setColour(DatabaseReference roomNameRef) {
        int[] colours = {getResources().getColor(R.color.red), getResources().getColor(R.color.green), getResources().getColor(R.color.black),
                getResources().getColor(R.color.primaryColor)};
        // get random colour
        int colour = colours[getNumber()];
        String colore = "";
        switch (colour) {
            case -237502:
                colore = getString(R.string.table_red);
                break;
            case -16711895:
                colore = getString(R.string.table_green);
                break;
            case -16777216:
                colore = getString(R.string.table_black);
                break;
            case -15774591:
                colore = getString(R.string.table_blue);
                break;
            default:
                break;
        }
        // set special colour
        roomNameRef.child("specialColour").setValue(colore);
        return colore;
    }

    private String getColour(DatabaseReference roomNameRef) {
        final String[] color = {""};
        roomNameRef.child("specialColour").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                color[0] = snapshot.getValue().toString();
                Log.i(TAG, "IL COLORE QUI:" + color[0]);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        Log.i(TAG, "IL COLORE QUI 2:" + color[0]);
        return color[0];
        /*
        final String[] color = {""};
        roomNameRef.child("specialColour").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    color[0] = task.getResult().getValue().toString();
                    Log.i(TAG, "IL COLORE[0] QUI:" + color[0]);
                }
            }
        });
        Log.i(TAG, "IL COLORE[0] QUI 2:" + color[0]);
        return color[0];*/
    }

    private void setGetColor(String color) {
        this.getColor = color;
    }

    private String getGetColor() {
        return this.getColor;
    }
}