package com.example.bisimulazione;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.callback.Callback;

import interfaces.CallbackPlayerOne;
import interfaces.CallbackPlayerTwo;
import interfaces.CallbackTurnOf;

public class Table extends AppCompatActivity implements CallbackTurnOf, CallbackPlayerOne, CallbackPlayerTwo {

    private static final String TAG = "Bisimulazione";

    private String playerName;
    private String roomName;
    private String role;
    private String specialColour;

    private boolean player1;

    private TextView coloreSpeciale;
    private TextView turnoDi;
    private TextView attacker;
    private TextView defender;

    private FirebaseDatabase database;
    private DatabaseReference roomsRef;
    private DatabaseReference roomNameRef;

    private Node[] nodes;
    private Edge[] edges;

    private Node[] nodesL;
    private Edge[] edgesL;

    private Node[] nodesR;
    private Edge[] edgesR;

    private ArrayList<List<Edge>> incomingEdgesLeft;
    private ArrayList<List<Edge>> outgoingEdgesLeft;
    private ArrayList<List<Edge>> incomingEdgesRight;
    private ArrayList<List<Edge>> outgoingEdgesRight;

    private final float radius = 40f;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        // initialize text views for special colour, turn of, attacker and defender
        coloreSpeciale = findViewById(R.id.table_special_colour);
        turnoDi = findViewById(R.id.table_turn_of);
        attacker = findViewById(R.id.table_attacker_is);
        defender = findViewById(R.id.table_defender_is);
        // initialize firebase user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        // get user name if not null
        if (user != null) {
            playerName = user.getDisplayName();
            //Log.i(TAG, "Player name " + user.getDisplayName());
        }
        // initialize database
        database = FirebaseDatabase.getInstance();
        // initialize reference to rooms
        roomsRef = database.getReference("rooms");
        // get data from matchmaking
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            player1 = extras.getBoolean("player 1");
            //Log.i(TAG, "Player 1? " + player1);
            roomName = extras.getString("roomName");
            //Log.i(TAG, "Room name: " + roomName);
            specialColour = extras.getString("specialColour");
            if (player1) {
                role = getString(R.string.table_attacker);
            } else {
                role = getString(R.string.table_defender);
            }
            // send to DB player name and his role whether player 1 or 2
            sendData(player1);
        }
        // set reference to room name
        roomNameRef = roomsRef.child(roomName);
        // set attacker
        setAttacker();
        // set defender
        setDefender();
        // set special colour text
        setTextColour(specialColour);
        // set turn of text
        setTurnOf();

        // initialize nodes
        nodes = new Node[10];
        // set nodes
        setNodes();
        // initialize edges
        edges = new Edge[14];
        // set edges
        setEdges();

        boolean left = true;
        // get node of left graph
        nodesL = divideNodes(nodes, left);
        for (Node node : nodesL) {
            Log.i(TAG, "1 - Nodes " + node.getId());
        }
        // get edges of left graph
        edgesL = divideEdges(edges, left);
       /* for (Edge edge : edgesL) {
            Log.i(TAG, "1 - Edge " + edge.getId());
        } */
        //dGraphLeft.setNodes(nodesL);
        // initialize directed graphs
        com.example.bisimulazione.directedgraph.DirectedGraph dGraphLeft = (com.example.bisimulazione.directedgraph.DirectedGraph) findViewById(R.id.table_left_table_directed_graph);
        //com.example.bisimulazione.directedgraph.DirectedGraph dGraphRight = (com.example.bisimulazione.directedgraph.DirectedGraph) findViewById(R.id.table_right_table_directed_graph);
        dGraphLeft.setNodes(nodesL);
        dGraphLeft.setEdges(edgesL);
        // initialize layout
        //LinearLayout tableLeftDirectedGraphLayout = findViewById(R.id.table_left_directed_graph_layout);
        // initialize directed graph left

        // add directed graph to linear layout
        //tableLeftDirectedGraphLayout.removeView(dGraphLeft);
        //tableLeftDirectedGraphLayout.addView(dGraphLeft);

        //incomingEdgesLeft = getIncomingEdgesLeft(edgesL);
        //outgoingEdgesLeft = getOutgoingEdgesLeft(edgesL);

        left = false;
        /*nodesR = divideNodes(nodes, left);
        edgesR = divideEdges(edges, left);

        LinearLayout tableRightDirectedGraphLayout = findViewById(R.id.table_right_directed_graph_layout);

        DirectedGraph directedGraphRight = new DirectedGraph(this, edgesR, nodesR, roomName);
        tableRightDirectedGraphLayout.addView(directedGraphRight); */
    }

    private Edge[] divideEdges(Edge[] edges, boolean left) {
        Edge[] appoggio = new Edge[7];
        if (left) {
            System.arraycopy(edges, 0, appoggio, 0, 7);
        } else {
            for (int i = 7; i < 14; i++) {
                appoggio[i - 7] = edges[i];
            }
        }
        return appoggio;
    }

    private Node[] divideNodes(Node[] nodes, boolean left) {
        Node[] appoggio = new Node[5];
        if (left) {
            System.arraycopy(nodes, 0, appoggio, 0, 5);
        } else {
            for (int i = 5; i < 10; i++) {
                appoggio[i - 5] = nodes[i];
            }
        }
        return appoggio;
    }

    private void setNodes() {
        int shiftStartVertical = 200;
        // left graph
        // initialize root node
        Node root = new Node(1, 0, shiftStartVertical, true, false, false, true, getResources().getColor(R.color.primaryColor));
        // assign root node to index 0 of nodes array
        nodes[0] = root;
        Node second = new Node(2, root.getX(), root.getY(), false, true, false, true, getResources().getColor(R.color.black));
        nodes[1] = second;
        Node third = new Node(3, root.getX(), root.getY(), false, false, true, true, getResources().getColor(R.color.black));
        nodes[2] = third;
        Node fourth = new Node(4, second.getX(), second.getY(), false, false, false, true, getResources().getColor(R.color.black));
        nodes[3] = fourth;
        Node fifth = new Node(5, third.getX(), third.getY(), false, false, false, true, getResources().getColor(R.color.black));
        nodes[4] = fifth;

        //right graph
        Node rootR = new Node(1, 0, shiftStartVertical, true, false, false, false, getResources().getColor(R.color.primaryColor));
        nodes[5] = rootR;
        Node secondR = new Node(2, rootR.getX(), rootR.getY(), false, true, false, false, getResources().getColor(R.color.black));
        nodes[6] = secondR;
        Node thirdR = new Node(3, rootR.getX(), rootR.getY(), false, false, true, false, getResources().getColor(R.color.black));
        nodes[7] = thirdR;
        Node fourthR = new Node(4, second.getX(), second.getY(), false, false, false, false, getResources().getColor(R.color.black));
        nodes[8] = fourthR;
        Node fifthR = new Node(5, thirdR.getX(), thirdR.getY(), false, false, false, false, getResources().getColor(R.color.black));
        nodes[9] = fifthR;
    }

    private void setEdges() {
        // left graph
        // initialize first edge
        Edge uno = new Edge(1, nodes[0], nodes[1], getResources().getColor(R.color.red), true, true, false);
        // assign first edge to index 0 of edges array
        edges[0] = uno;
        Edge due = new Edge(2, nodes[0], nodes[2], getResources().getColor(R.color.red), true, true, false);
        edges[1] = due;
        Edge tre = new Edge(3, nodes[1], nodes[0], getResources().getColor(R.color.green), true, false, true);
        edges[2] = tre;
        Edge quattro = new Edge(4, nodes[2], nodes[0], getResources().getColor(R.color.green), true, false, true);
        edges[3] = quattro;
        Edge cinque = new Edge(5, nodes[2], nodes[4], getResources().getColor(R.color.black), true, true, true);
        edges[4] = cinque;
        Edge sei = new Edge(6, nodes[1], nodes[3], getResources().getColor(R.color.primaryColor), true, true, true);
        edges[5] = sei;
        Edge sette = new Edge(7, nodes[4], nodes[3], getResources().getColor(R.color.primaryColor), true, false, true);
        edges[6] = sette;

        // right graph
        Edge unoR = new Edge(1, nodes[5], nodes[6], getResources().getColor(R.color.red), false, true, false);
        edges[7] = unoR;
        Edge dueR = new Edge(2, nodes[5], nodes[7], getResources().getColor(R.color.red), false, true, false);
        edges[8] = dueR;
        Edge treR = new Edge(3, nodes[7], nodes[5], getResources().getColor(R.color.green), false, false, true);
        edges[9] = treR;
        Edge quattroR = new Edge(4, nodes[9], nodes[5], getResources().getColor(R.color.green), false, false, false);
        edges[10] = quattroR;
        Edge cinqueR = new Edge(5, nodes[6], nodes[8], getResources().getColor(R.color.primaryColor), false, true, true);
        edges[11] = cinqueR;
        Edge seiR = new Edge(6, nodes[9], nodes[8], getResources().getColor(R.color.primaryColor), false, false, true);
        edges[12] = seiR;
        Edge setteR = new Edge(7, nodes[7], nodes[9], getResources().getColor(R.color.black), false, true, true);
        edges[13] = setteR;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        // perform on touch only once
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            /*for (Node node : nodes) {
                if (node != null) {
                    boolean isTouched = touchIsInCircle(event.getX(), event.getY(), node.getX(), node.getY(), radius);
                    if (isTouched) {
                        // Log.i(TAG, "Circle touched: " + node.getId() + "table: " + node.isLeftTable());
                        if (node.isLeftTable()) {
                            roomNameRef = roomNameRef.child("leftGraph");
                            switch (node.getId()) {
                                case 1:
                                    roomNameRef.child("One selected").setValue(true);
                                    break;
                                case 2:
                                    roomNameRef.child("Two selected").setValue(true);
                                    break;
                                case 3:
                                    roomNameRef.child("Three selected").setValue(true);
                                    break;
                                case 4:
                                    roomNameRef.child("Four selected").setValue(true);
                                    break;
                                case 5:
                                    roomNameRef.child("Five selected").setValue(true);
                                    break;
                                default:
                                    break;
                            }
                        }
                        //refreshNodes(node.isLeftTable(), node.getId());
                        //refreshTurnOf();
                    } */
            refreshTurnOf();
        }
        //}
        //}
        return true;
    }

    private void refreshTurnOf() {
        getTurnOf(roomsRef.child(roomName), new CallbackTurnOf() {
            @Override
            public void onCallbackTurnOf(String turnOf) {
                boolean retrievedTurnOf = false;
                while (!retrievedTurnOf) {
                    if (turnOf != null) {
                        if (turnOf.equalsIgnoreCase(getString(R.string.table_attacker))) {
                            turnoDi.setText(getString(R.string.table_defender));
                        } else {
                            turnoDi.setText(getString(R.string.table_attacker));
                        }
                        retrievedTurnOf = true;
                    }
                }
            }
        }, true);
    }

    private boolean touchIsInCircle(float x, float y, float centreX, float centreY, float radius) {
        double dx = Math.pow(x - centreX, 2);
        double dy = Math.pow(y - centreY, 2);

        return (dx + dy) < Math.pow(radius, 2);
    }

    private void setAttacker() {
        getPlayerOneName(new CallbackPlayerOne() {
            @Override
            public void onCallbackPlayerOneName(String playerName) {
                boolean retrievedPlayerName = false;
                while (!retrievedPlayerName) {
                    if (playerName != null) {
                        attacker.setText(playerName);
                        retrievedPlayerName = true;
                    }
                }
            }
        });
    }

    private void getPlayerOneName(CallbackPlayerOne callback) {
        roomNameRef.child("Player 1").child("playerName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = snapshot.getValue().toString();
                    callback.onCallbackPlayerOneName(value);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setDefender() {
        getPlayerTwoName(new CallbackPlayerTwo() {
            @Override
            public void onCallbackPlayerTwoName(String playerName) {
                boolean retrievedPlayerName = false;
                while (!retrievedPlayerName) {
                    if (playerName != null) {
                        defender.setText(playerName);
                        retrievedPlayerName = true;
                    }
                }
            }
        });
    }

    private void getPlayerTwoName(CallbackPlayerTwo callback) {
        roomNameRef.child("Player 2").child("playerName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = snapshot.getValue().toString();
                    callback.onCallbackPlayerTwoName(value);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void sendData(boolean player1) {
        HashMap<String, String> map = new HashMap<>();
        map.put("playerName", playerName);
        map.put("role", role);
        if (player1) {
            roomsRef.child(roomName + "/" + "Player 1/").setValue(map);
        } else {
            roomsRef.child(roomName + "/" + "Player 2/").setValue(map);
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
        getTurnOf(roomsRef.child(roomName), new CallbackTurnOf() {
            @Override
            public void onCallbackTurnOf(String turnOf) {
                boolean retrievedTurnOf = false;
                while (!retrievedTurnOf) {
                    if (turnOf != null) {
                        turnoDi.setText(turnOf);
                        retrievedTurnOf = true;
                    }
                }
            }
        }, false);
    }

    private void getTurnOf(DatabaseReference roomNameRef, CallbackTurnOf callback, boolean persistent) {
        if (!persistent) {
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
        } else {
            roomNameRef.child("turnOf").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.getValue().toString() != null) {
                        String value = snapshot.getValue().toString();
                        callback.onCallbackTurnOf(value);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }


    private ArrayList<List<Edge>> getIncomingEdgesLeft(Edge[] edges) {
        //private void getIncomingEdges(Edge[] edges) {

        List<Edge> incomingEdgesNodeOne = new ArrayList<Edge>();
        List<Edge> incomingEdgesNodeTwo = new ArrayList<Edge>();
        List<Edge> incomingEdgesNodeThree = new ArrayList<Edge>();
        List<Edge> incomingEdgesNodeFour = new ArrayList<Edge>();
        List<Edge> incomingEdgesNodeFive = new ArrayList<Edge>();

        for (Edge edge : edges) {
            switch (edge.getId()) {
                case 1:
                    incomingEdgesNodeTwo.add(edge);
                    break;
                case 2:
                    incomingEdgesNodeThree.add(edge);
                    break;
                case 3:
                case 4:
                    incomingEdgesNodeOne.add(edge);
                    break;
                case 5:
                    incomingEdgesNodeFive.add(edge);
                    break;
                case 6:
                case 7:
                    incomingEdgesNodeFour.add(edge);
                    break;
                default:
                    break;
            }
        }
        ArrayList<List<Edge>> arrayList = new ArrayList<>(5);
        arrayList.add(incomingEdgesNodeOne);
        arrayList.add(incomingEdgesNodeTwo);
        arrayList.add(incomingEdgesNodeThree);
        arrayList.add(incomingEdgesNodeFour);
        arrayList.add(incomingEdgesNodeFive);

        /*Iterator<List<Edge>> listOfListsIterator = arrayList.iterator();
        while (listOfListsIterator.hasNext()) {
            List<Edge> list = new ArrayList<>();
            list = listOfListsIterator.next();

            Iterator<Edge> eachListIterator = list.iterator();
            while (eachListIterator.hasNext()) {
                eachListIterator.next().
                //int id = eachListIterator.next().getId();
            }
        } */
        return arrayList;
    }

    private ArrayList<List<Edge>> getOutgoingEdgesLeft(Edge[] edges) {
        //private void getIncomingEdges(Edge[] edges) {

        List<Edge> outgoingEdgesNodeOne = new ArrayList<Edge>();
        List<Edge> outgoingEdgesNodeTwo = new ArrayList<Edge>();
        List<Edge> outgoingEdgesNodeThree = new ArrayList<Edge>();
        List<Edge> outgoingEdgesNodeFour = new ArrayList<Edge>();
        List<Edge> outgoingEdgesNodeFive = new ArrayList<Edge>();

        for (Edge edge : edges) {
            switch (edge.getId()) {
                case 1:
                case 2:
                    outgoingEdgesNodeOne.add(edge);
                    break;
                case 3:
                case 6:
                    outgoingEdgesNodeTwo.add(edge);
                    break;
                case 4:
                case 5:
                    outgoingEdgesNodeThree.add(edge);
                    break;
                case 7:
                    outgoingEdgesNodeFive.add(edge);
                    break;
                default:
                    break;
            }
        }
        ArrayList<List<Edge>> arrayList = new ArrayList<>(5);
        arrayList.add(outgoingEdgesNodeOne);
        arrayList.add(outgoingEdgesNodeTwo);
        arrayList.add(outgoingEdgesNodeThree);
        arrayList.add(outgoingEdgesNodeFour);
        arrayList.add(outgoingEdgesNodeFive);

        /*Iterator<List<Edge>> listOfListsIterator = arrayList.iterator();
        while (listOfListsIterator.hasNext()) {
            List<Edge> list = new ArrayList<>();
            list = listOfListsIterator.next();

            Iterator<Edge> eachListIterator = list.iterator();
            while (eachListIterator.hasNext()) {
                eachListIterator.next().
                //int id = eachListIterator.next().getId();
            }
        } */
        return arrayList;
    }

    @Override
    public void onCallbackTurnOf(String turnOf) {

    }

    @Override
    public void onCallbackPlayerOneName(String playerName) {

    }

    @Override
    public void onCallbackPlayerTwoName(String playerName) {

    }
}