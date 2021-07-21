package com.example.bisimulazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import interfaces.CallbackColour;

public class MatchmakingRoom extends AppCompatActivity implements CallbackColour {

    private static final String TAG = "Bisimulazione";

    private ListView listView;
    private Button createRoom;

    private List<String> roomsList;
    private ArrayAdapter<String> adapter;

    private String playerName;
    private String roomName;
    private String specialColour;

    private DatabaseReference roomsRef;
    private DatabaseReference roomNameRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking_room);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.matchmaking_room));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        createRoom.setOnClickListener(v -> {
            // create room and add yourself as first player
            createRoom.setText(getString(R.string.matchmaking_room_creating_room_text));
            createRoom.setEnabled(false);
            roomNameRef = roomsRef.child(roomName);
            // set show
            roomNameRef.child("show").setValue("true");
            // set colour
            specialColour = setColour(roomNameRef);
            // set turn of
            setTurnOf(roomNameRef);
            // initialize to empty string last move colour
            initializeLastMoveColour(roomNameRef);
            initializeGraphs(roomNameRef);
            sendDataP1(roomNameRef, playerName);
            sendDataNoMoveButton(roomNameRef);
            sendDataGameInProgress(roomNameRef);
            startActivity(roomName, true, specialColour);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            // join an existing room and add yourself as player two
            String roomN = roomsList.get(position);
            roomName = remove(roomN);
            //roomName = roomsList.get(position);
            roomNameRef = roomsRef.child(roomName);
            sendDataP2(roomNameRef, playerName);
            // not to show when room is full
            roomNameRef.child("show").setValue("false");
            specialColour = "";
            getColour(roomNameRef, color -> {
                boolean retrievedColor = false;
                while (!retrievedColor) {
                    if (color != null) {
                        specialColour = color;
                        retrievedColor = true;
                        startActivity(roomName, false, specialColour);
                        finish();
                    }
                }
            });
        });
        // show new rooms created
        getRoomsList();
    }

    private void sendDataGameInProgress(DatabaseReference roomNameRef) {
        // game is on
        roomNameRef.child("gameInProgress").setValue("true");
    }

    private void getRoomsList() {
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                roomsList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.child("show").getValue() != null) {
                        if (Objects.requireNonNull(data.child("show").getValue()).toString().equalsIgnoreCase("true")) {
                            roomsList.add(getString(R.string.matchmaking_room_property) + " " + data.getKey());
                        }
                    }
                    adapter = new ArrayAdapter<>(MatchmakingRoom.this, android.R.layout.simple_list_item_1, roomsList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });
    }

    private void sendDataP1(DatabaseReference roomNameRef, String playerName) {
        roomNameRef.child("Player 1/").setValue(playerName);
    }

    private void sendDataP2(DatabaseReference roomNameRef, String playerName) {
        roomNameRef.child("Player 2/").setValue(playerName);
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
        // return random number between 0 and 3
        return (int) (Math.random() * 4);
    }

    private String colourToString(int colour) {
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
        return colore;
    }

    private String setColour(DatabaseReference roomNameRef) {
        int[] colours = {getResources().getColor(R.color.red), getResources().getColor(R.color.green), getResources().getColor(R.color.black),
                getResources().getColor(R.color.primaryColor)};
        // get random colour
        int colour = colours[getNumber()];
        String colore = colourToString(colour);
        // set special colour
        roomNameRef.child("specialColour").setValue(colore);
        return colore;
    }

    private void getColour(DatabaseReference roomNameRef, CallbackColour callback) {
        roomNameRef.child("specialColour").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String value = Objects.requireNonNull(snapshot.getValue()).toString();
                callback.onCallbackColour(value);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setTurnOf(DatabaseReference roomNameRef) {
        roomNameRef.child("turnOf").setValue(getString(R.string.matchmaking_room_attacker));
    }

    private void initializeLastMoveColour(DatabaseReference roomNameRef) {
        roomNameRef.child("lastMoveColour").setValue("");
    }

    private void initializeGraphs(DatabaseReference roomNameRef) {
        initializeGraph(roomNameRef.child("leftGraph"));
        initializeGraph(roomNameRef.child("rightGraph"));
    }

    private void initializeGraph(DatabaseReference graphReference) {
        DatabaseReference nodesReference;
        HashMap<String, String> map;
        int i = 0;
        while (i < 5) {
            if (i == 0) {
                nodesReference = getGraphReference(graphReference, "one");
                map = setMapGraph(true);
            } else {
                if (i == 1) {
                    nodesReference = getGraphReference(graphReference, "two");
                } else if (i == 2) {
                    nodesReference = getGraphReference(graphReference, "three");
                } else if (i == 3) {
                    nodesReference = getGraphReference(graphReference, "four");
                } else {
                    nodesReference = getGraphReference(graphReference, "five");
                }
                map = setMapGraph(false);
            }
            if (nodesReference != null) {
                sendDataNodes(nodesReference, map);
            }
            i++;
        }
    }

    private void sendDataNoMoveButton(DatabaseReference roomNameRef) {
        roomNameRef.child("noMoveButtonEnabled").setValue("false");
    }

    private void sendDataNodes(DatabaseReference nodesReference, HashMap<String, String> map) {
        nodesReference.setValue(map);
    }

    private DatabaseReference getGraphReference(DatabaseReference graphReference, String nodeNumber) {
        switch (nodeNumber) {
            case "one":
                graphReference = graphReference.child("Node one");
                return graphReference;
            case "two":
                graphReference = graphReference.child("Node two");
                return graphReference;
            case "three":
                graphReference = graphReference.child("Node three");
                return graphReference;
            case "four":
                graphReference = graphReference.child("Node four");
                return graphReference;
            case "five":
                graphReference = graphReference.child("Node five");
                return graphReference;
            default:
                break;
        }
        Log.d(TAG, "Reference is null");
        return null;
    }

    private HashMap<String, String> setMapGraph(boolean root) {
        HashMap<String, String> map = new HashMap<>();
        if (root) {
            map.put("Selected", String.valueOf(true));
            map.put("Colour", "blue");
        } else {
            map.put("Selected", String.valueOf(false));
            map.put("Colour", "black");
        }
        return map;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            removeData();
            this.finish();
        }
        return true;
    }

    private void removeData() {
        FirebaseDatabase.getInstance().getReference().child("players").child(playerName).removeValue();
    }

    @Override
    public void onCallbackColour(String color) {

    }
}