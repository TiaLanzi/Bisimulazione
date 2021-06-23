package com.example.bisimulazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bisimulazione.directedgraph.DirectedGraph;
import com.example.bisimulazione.directedgraph.Edge;
import com.example.bisimulazione.directedgraph.Node;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Table extends AppCompatActivity {

    private final String TAG = "Bisimulazione";

    private String playerName;
    private String roomName;
    private String role;
    private String message;
    private FirebaseDatabase database;
    private DatabaseReference referenceMessage;
    private Button buttonOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        // initialize database
        database = FirebaseDatabase.getInstance();
        // initialize reference
        referenceMessage = database.getReference("rooms/" + roomName + "/message");

        Intent intent = getIntent();
        if (intent != null) {
            roomName = intent.getExtras().getString("roomName");
            playerName = intent.getExtras().getString("playerName");
            if (roomName.equals(playerName)) {
                role = "host";
            } else {
                role = "guest";
            }
        }
        // initialize layout
        LinearLayout tableLeftDirectedGraphLayout = findViewById(R.id.table_left_directed_graph_layout);

        // initialize root node
        int shiftStartVertical = 140;
        Node root = new Node(0, shiftStartVertical, true, false, false, true);
        Node second = new Node(root.getX(), root.getY(), false, true, false, true);
        Node third = new Node(root.getX(), root.getY(), false, false, true, true);
        Node fourth = new Node(second.getX(), second.getY(), false, false, false, true);
        Node fifth = new Node(third.getX(), third.getY(), false, false, false, true);
        //initialize array of edges
        Edge[] edges = new Edge[10];
        // initialize first edge
        Edge uno = new Edge(root, second, getResources().getColor(R.color.red), true, false);
        // assign first edge to index 0 of edges array
        edges[0] = uno;
        Edge due = new Edge(root, third, getResources().getColor(R.color.red), true, false);
        edges[1] = due;
        Edge tre = new Edge(second, root, getResources().getColor(R.color.green), false, false);
        edges[2] = tre;
        Edge quattro = new Edge(third, root, getResources().getColor(R.color.green), false, false);
        edges[3] = quattro;
        Edge cinque = new Edge(third, fifth, getResources().getColor(R.color.grey), true, true);
        edges[4] = cinque;
        Edge sei = new Edge(second, fourth, getResources().getColor(R.color.primaryColor), true, true);
        edges[5] = sei;
        Edge sette = new Edge(fifth, fourth, getResources().getColor(R.color.primaryColor), false, true);
        edges[6] = sette;
        //initialize DirectedGraph
        DirectedGraph directedGraphLeft = new DirectedGraph(this, edges);
        // directedGraphLeft.setBackgroundColor(getResources().getColor(R.color.red));

        /*directedGraphRight = new DirectedGraph(this);
        directedGraphRight.setBackgroundColor(getResources().getColor(R.color.background_color));*/

        tableLeftDirectedGraphLayout.addView(directedGraphLeft);
        //tableRightDirectedGraphLayout.addView(directedGraphRight);

        buttonOne = findViewById(R.id.button_one);
        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOne.setEnabled(false);
                message = role + ": Poked!";
                referenceMessage.setValue(message);
            }
        });

        // listen for incoming messages
        referenceMessage = database.getReference("rooms/" + roomName + "/message");
        message = role + ":Poked!";
        referenceMessage.setValue(message);
        addRoomEventListener();
    }

    private void addRoomEventListener() {
        referenceMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                buttonOne.setEnabled(true);
                if (role.equals("host")) {
                    if (snapshot.getValue(String.class).contains("guest:")) {
                        Toast.makeText(Table.this, "" + snapshot.getValue(String.class).replace("guest:", ""), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (snapshot.getValue(String.class).contains("host:")) {
                        Toast.makeText(Table.this, "" + snapshot.getValue(String.class).replace("host:", ""), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}