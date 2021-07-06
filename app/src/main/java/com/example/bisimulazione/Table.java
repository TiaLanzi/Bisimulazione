package com.example.bisimulazione;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bisimulazione.directedgraph.DirectedGraph;
import com.example.bisimulazione.directedgraph.Edge;
import com.example.bisimulazione.directedgraph.Node;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import interfaces.Callback;

public class Table extends AppCompatActivity implements Callback {

    private static final String TAG = "Bisimulazione";

    private String playerName;
    private String roomName;
    private String role;
    private String specialColour;

    private TextView coloreSpeciale;
    private TextView turnoDi;

    private FirebaseDatabase database;
    private DatabaseReference roomRef;
    private DatabaseReference roomRole;
    private DatabaseReference leftTableRef;
    private DatabaseReference rightTableRef;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        coloreSpeciale = findViewById(R.id.table_special_colour);
        turnoDi = findViewById(R.id.table_turn_of);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            playerName = user.getDisplayName();
            //Log.i(TAG, "Player name " + user.getDisplayName());
        }

        database = FirebaseDatabase.getInstance();

        roomRef = database.getReference("rooms");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean player1 = extras.getBoolean("player 1");
            //Log.i(TAG, "Player 1? " + player1);
            roomName = extras.getString("roomName");
            //Log.i(TAG, "Room name: " + roomName);
            specialColour = extras.getString("specialColour");
            if (player1) {
                role = getString(R.string.table_attacker);
            } else {
                role = getString(R.string.table_defender);
            }
            sendData(player1);
        }
        // set special colour text
        setTextColour(specialColour);
        // set turn of text
        setTurnOf();


        //Log.i(TAG, "Room name: " + roomName + ", role: " + role);


        //Log.i(TAG, "Red: " + colours[0]);
        //Log.i(TAG, "Green: " + colours[1]);
        //Log.i(TAG, "Black: " + colours[2]);
        //Log.i(TAG, "Blue: " + colours[3]);

        // initialize layout
        LinearLayout tableLeftDirectedGraphLayout = findViewById(R.id.table_left_directed_graph_layout);

        // initialize root node
        int shiftStartVertical = 200;
        Node[] nodes = new Node[5];
        Node root = new Node(1, 0, shiftStartVertical, true, false, false, true, getColor(R.color.primaryColor));
        nodes[0] = root;
        Node second = new Node(2, root.getX(), root.getY(), false, true, false, true, getColor(R.color.black));
        nodes[1] = second;
        Node third = new Node(3, root.getX(), root.getY(), false, false, true, true, getColor(R.color.black));
        nodes[2] = third;
        Node fourth = new Node(4, second.getX(), second.getY(), false, false, false, true, getColor(R.color.black));
        nodes[3] = fourth;
        Node fifth = new Node(5, third.getX(), third.getY(), false, false, false, true, getColor(R.color.black));
        nodes[4] = fifth;
        // initialize array of edges
        Edge[] edges = new Edge[7];
        // initialize first edge
        Edge uno = new Edge(1, root, second, getResources().getColor(R.color.red), true, true, false);
        // assign first edge to index 0 of edges array
        edges[0] = uno;
        Edge due = new Edge(2, root, third, getResources().getColor(R.color.red), true, true, false);
        edges[1] = due;
        Edge tre = new Edge(3, second, root, getResources().getColor(R.color.green), true, false, true);
        edges[2] = tre;
        Edge quattro = new Edge(4, third, root, getResources().getColor(R.color.green), true, false, true);
        edges[3] = quattro;
        Edge cinque = new Edge(5, third, fifth, getResources().getColor(R.color.black), true, true, true);
        edges[4] = cinque;
        Edge sei = new Edge(6, second, fourth, getResources().getColor(R.color.primaryColor), true, true, true);
        edges[5] = sei;
        Edge sette = new Edge(7, fifth, fourth, getResources().getColor(R.color.primaryColor), true, false, true);
        edges[6] = sette;
        // initialize DirectedGraph
        DirectedGraph directedGraphLeft = new DirectedGraph(this, edges, nodes, roomName);
        // add directed graph to linear layout
        tableLeftDirectedGraphLayout.addView(directedGraphLeft);

        LinearLayout tableRightDirectedGraphLayout = findViewById(R.id.table_right_directed_graph_layout);

        Node[] nodesR = new Node[5];
        Node rootR = new Node(1, 0, shiftStartVertical, true, false, false, false, getColor(R.color.primaryColor));
        nodesR[0] = rootR;
        Node secondR = new Node(2, rootR.getX(), rootR.getY(), false, true, false, false, getColor(R.color.black));
        nodesR[1] = secondR;
        Node thirdR = new Node(3, rootR.getX(), rootR.getY(), false, false, true, false, getColor(R.color.black));
        nodesR[2] = thirdR;
        Node fourthR = new Node(4, second.getX(), second.getY(), false, false, false, false, getColor(R.color.black));
        nodesR[3] = fourthR;
        Node fifthR = new Node(5, thirdR.getX(), thirdR.getY(), false, false, false, false, getColor(R.color.black));
        nodesR[4] = fifthR;

        Edge[] edgesR = new Edge[7];
        Edge unoR = new Edge(1, rootR, secondR, getResources().getColor(R.color.red), false, true, false);
        edgesR[0] = unoR;
        Edge dueR = new Edge(2, rootR, thirdR, getResources().getColor(R.color.red), false, true, false);
        edgesR[1] = dueR;
        Edge treR = new Edge(3, thirdR, rootR, getResources().getColor(R.color.green), false, false, true);
        edgesR[2] = treR;
        Edge quattroR = new Edge(4, fifthR, rootR, getResources().getColor(R.color.green), false, false, false);
        edgesR[3] = quattroR;
        Edge cinqueR = new Edge(5, secondR, fourthR, getResources().getColor(R.color.primaryColor), false, true, true);
        edgesR[4] = cinqueR;
        Edge seiR = new Edge(6, fifthR, fourthR, getResources().getColor(R.color.primaryColor), false, false, true);
        edgesR[5] = seiR;
        Edge setteR = new Edge(7, thirdR, fifthR, getResources().getColor(R.color.black), false, true, true);
        edgesR[6] = setteR;

        DirectedGraph directedGraphRight = new DirectedGraph(this, edgesR, nodesR, roomName);
        tableRightDirectedGraphLayout.addView(directedGraphRight);
    }

    private void sendData(boolean player1) {
        HashMap<String, String> map = new HashMap<>();
        map.put("playerName", playerName);
        map.put("role", role);
        // Log.i(TAG, "Siamo qui arrivati");
        if (player1) {
            roomRef.child(roomName + "/" + "Player 1/").setValue(map);
        } else {
            roomRef.child(roomName + "/" + "Player 2/").setValue(map);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTextColour(String sColour) {
        switch (sColour) {
            case "red":
                coloreSpeciale.setTextColor(getColor(R.color.red));
                break;
            case "green":
                coloreSpeciale.setTextColor(getColor(R.color.green));
                break;
            case "black":
                coloreSpeciale.setTextColor(getColor(R.color.black));
                break;
            case "blue":
                coloreSpeciale.setTextColor(getColor(R.color.primaryColor));
                break;
            default:
                break;
        }
        coloreSpeciale.setText(specialColour);
    }

    private void setTurnOf() {
        getTurnOf(roomRef.child(roomName), new Callback() {
            @Override
            public void onCallbackTurnOf(String turnOf) {
                boolean retrievedTurnOf = false;
                while (!retrievedTurnOf) {
                    if (turnOf != null) {
                        turnoDi.setText(turnOf);
                        retrievedTurnOf = true;
                        Log.i(TAG, "Turn of " + turnOf);

                    }
                }
            }
        });
    }

    private void getTurnOf(DatabaseReference roomNameRef, Callback callback) {
        roomNameRef.child("turnOf").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String value = snapshot.getValue().toString();
                callback.onCallbackTurnOf(value);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onCallbackTurnOf(String turnOf) {

    }
}