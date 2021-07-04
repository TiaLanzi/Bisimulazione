package com.example.bisimulazione;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bisimulazione.directedgraph.DirectedGraph;
import com.example.bisimulazione.directedgraph.Edge;
import com.example.bisimulazione.directedgraph.Node;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Table extends AppCompatActivity {

    private static final String TAG = "Bisimulazione";

    private String playerName;
    private String roomName;
    private String role;
    private String specialColour;

    private TextView coloreSpeciale;

    private FirebaseDatabase database;
    private DatabaseReference roomRef;
    private DatabaseReference roomRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        coloreSpeciale = findViewById(R.id.table_special_colour);

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
                sendData(player1);
            } else {
                role = getString(R.string.table_defender);
                sendData(player1);
            }
        }
        coloreSpeciale.setText(specialColour);
        //Log.i(TAG, "Room name: " + roomName + ", role: " + role);
        /*

        //Log.i(TAG, "Red: " + colours[0]);
        //Log.i(TAG, "Green: " + colours[1]);
        //Log.i(TAG, "Black: " + colours[2]);
        //Log.i(TAG, "Blue: " + colours[3]);

        // initialize layout
        LinearLayout tableLeftDirectedGraphLayout = findViewById(R.id.table_left_directed_graph_layout);

        // initialize root node
        int shiftStartVertical = 200;
        Node root = new Node(1, 0, shiftStartVertical, true, false, false, true);
        Node second = new Node(2, root.getX(), root.getY(), false, true, false, true);
        Node third = new Node(3, root.getX(), root.getY(), false, false, true, true);
        Node fourth = new Node(4, second.getX(), second.getY(), false, false, false, true);
        Node fifth = new Node(5, third.getX(), third.getY(), false, false, false, true);
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
        DirectedGraph directedGraphLeft = new DirectedGraph(this, edges);
        // add directed graph to linear layout
        tableLeftDirectedGraphLayout.addView(directedGraphLeft);

        LinearLayout tableRightDirectedGraphLayout = findViewById(R.id.table_right_directed_graph_layout);

        Node rootR = new Node(1, 0, shiftStartVertical, true, false, false, false);
        Node secondR = new Node(2, rootR.getX(), rootR.getY(), false, true, false, false);
        Node thirdR = new Node(3, rootR.getX(), rootR.getY(), false, false, true, false);
        Node fourthR = new Node(4, second.getX(), second.getY(), false, false, false, false);
        Node fifthR = new Node(5, thirdR.getX(), thirdR.getY(), false, false, false, false);

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

        DirectedGraph directedGraphRight = new DirectedGraph(this, edgesR);
        tableRightDirectedGraphLayout.addView(directedGraphRight);*/
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
}