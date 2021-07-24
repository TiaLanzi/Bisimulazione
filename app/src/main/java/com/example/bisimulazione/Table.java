package com.example.bisimulazione;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bisimulazione.directedgraph.DirectedGraph;
import com.example.bisimulazione.directedgraph.DirectedGraphLeft;
import com.example.bisimulazione.directedgraph.DirectedGraphRight;
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
import java.util.Objects;

import interfaces.CallbackGameInProgress;
import interfaces.CallbackLastMoveColour;
import interfaces.CallbackNoMove;
import interfaces.CallbackPlayerOne;
import interfaces.CallbackPlayerTwo;
import interfaces.CallbackSelectedNode;
import interfaces.CallbackTurnOf;

public class Table extends AppCompatActivity implements CallbackTurnOf, CallbackPlayerOne, CallbackPlayerTwo, CallbackLastMoveColour, CallbackSelectedNode, CallbackNoMove, CallbackGameInProgress {

    private static final String TAG = "Bisimulazione";

    private String playerName;
    private String roomName;
    private String role;
    private String specialColour;

    private boolean touchable;

    private TextView coloreSpeciale;
    private TextView turnoDi;
    private TextView attacker;
    private TextView defender;
    private TextView lastMoveColour;
    private TextView selectedNodeLeft;
    private TextView selectedNodeRight;

    private Button noMove;

    private DatabaseReference roomsRef;
    private DatabaseReference roomNameRef;
    private DatabaseReference leftGraphRef;
    private DatabaseReference rightGraphRef;

    private com.example.bisimulazione.directedgraph.DirectedGraphLeft directedGraphLeft;
    private com.example.bisimulazione.directedgraph.DirectedGraphRight directedGraphRight;

    private Node[] nodes;
    private Edge[] edges;

    private Node[] nodesL;
    private Edge[] edgesL;

    private Node[] nodesR;
    private Edge[] edgesR;

    private int[][] controlMatrix;

    private final float radius = 40f;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        // initialize text views for special colour, turn of, attacker, defender, last move colour, no move and selected node (invisible)
        coloreSpeciale = findViewById(R.id.table_special_colour);
        turnoDi = findViewById(R.id.table_turn_of);
        attacker = findViewById(R.id.table_attacker_is);
        defender = findViewById(R.id.table_defender_is);
        lastMoveColour = findViewById(R.id.table_last_move_colour);
        noMove = findViewById(R.id.table_no_move);
        selectedNodeLeft = findViewById(R.id.table_left_selected_node);
        selectedNodeRight = findViewById(R.id.table_right_selected_node);
        // initialize firebase user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        // get user name if not null
        if (user != null) {
            playerName = user.getDisplayName();
        }
        // initialize database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // initialize reference to rooms
        roomsRef = database.getReference("rooms");
        // get data from matchmaking
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean player1 = extras.getBoolean("player 1");
            roomName = extras.getString("roomName");
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
        // set left graph reference
        leftGraphRef = roomNameRef.child("leftGraph");
        // set right graph reference
        rightGraphRef = roomNameRef.child("rightGraph");
        // initialize control matrix
        controlMatrix = new int[5][5];

        Node sNodeLeft;
        Node nodeTouchedLeft;

        selectedNodeLeft.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(TAG, "Left Before " + s.toString());
                refreshDirectedGraph(directedGraphLeft, s.toString().trim(), true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "Left After " + s.toString());
                refreshDirectedGraph(directedGraphLeft, s.toString().trim(), false);
            }
        });
        // set info box
        setInfoBox();
        // end game
        setEndGame();

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
        // get edges of left graph
        edgesL = divideEdges(edges, left);
        // initialize directed graph left
        //directedGraphLeft = findViewById(R.id.table_left_table_directed_graph);
        directedGraphLeft = new DirectedGraphLeft(this);
        // set nodes for left graph
        directedGraphLeft.setNodes(nodesL);
        // set edges for left graph
        directedGraphLeft.setEdges(edgesL);

        setOutgoingEdgesLeft();

        touchable = true;

        leftGraphRef = roomNameRef.child("leftGraph");
        directedGraphLeft.setReference(leftGraphRef);

        LinearLayout layoutDirectedGraphLeft = findViewById(R.id.table_left_directed_graph_layout);
        layoutDirectedGraphLeft.addView(directedGraphLeft);

        directedGraphLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setOnTouchGraph(event, (DirectedGraphLeft) v);
                }
                return touchable;
            }
        });

        left = false;
        nodesR = divideNodes(nodes, left);
        edgesR = divideEdges(edges, left);

        //directedGraphRight = findViewById(R.id.table_right_table_directed_graph);
        directedGraphRight = new DirectedGraphRight(this);

        directedGraphRight.setNodes(nodesR);
        directedGraphRight.setEdges(edgesR);

        setOutgoingEdgesRight();

        rightGraphRef = roomNameRef.child("rightGraph");
        directedGraphRight.setReference(rightGraphRef);

        LinearLayout layoutDirectedGraphRight = findViewById(R.id.table_right_directed_graph_layout);
        layoutDirectedGraphRight.addView(directedGraphRight);

        selectedNodeRight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(TAG, "Right Before " + s.toString());
                refreshDirectedGraph(directedGraphRight, s.toString(), true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "Right after " + s.toString());
                refreshDirectedGraph(directedGraphRight, s.toString(), false);
            }
        });

        directedGraphRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setOnTouchGraph(event, (DirectedGraphRight) v);
                }
                return touchable;
            }
        });

        noMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnTouchGraph(null, null);
            }
        });
    }

    private void refreshDirectedGraph(DirectedGraph directedGraph, String selectedNode, boolean bool) {
        if (directedGraph.getNodes() != null) {
            for (Node node : directedGraph.getNodes()) {
                Log.i(TAG, "Selected node id" + stringToID(selectedNode));
                if (node.getId() == stringToID(selectedNode)) {
                    if (bool) {
                        node.setColor(getResources().getColor(R.color.black));
                    } else {
                        node.setColor(getResources().getColor(R.color.primaryColor));
                    }
                }
            }
        }
        directedGraph.invalidate();
    }

    private void setInfoBox() {
        // set attacker
        setAttacker();
        // set defender
        setDefender();
        // set special colour text
        setTextColour(specialColour);
        // set turn of text
        setTurnOf();
        // set last move colour
        setLastMoveColour();
        // set selected node left table
        setSelectedNode(true);
        // set selected node right table
        setSelectedNode(false);
        // enable / disable no move button
        setNoMove();
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

    private void setOutgoingEdgesLeft() {
        // set outgoing edges
        Edge[] edgesRootLeftO = {edgesL[0], edgesL[1]};
        nodesL[0].setOutgoingEdges(edgesRootLeftO);

        Edge[] edgesSecondLeftO = {edgesL[2], edgesL[5]};
        nodesL[1].setOutgoingEdges(edgesSecondLeftO);

        Edge[] edgesThirdLeftO = {edgesL[3], edgesL[4]};
        nodesL[2].setOutgoingEdges(edgesThirdLeftO);

        Edge[] edgesSFourthLeftO = {};
        nodesL[3].setOutgoingEdges(edgesSFourthLeftO);

        Edge[] edgesFifthLeftO = {edgesL[6]};
        nodesL[4].setOutgoingEdges(edgesFifthLeftO);
    }

    private void setOutgoingEdgesRight() {
        Edge[] edgesRootRightO = {edgesR[0], edgesR[1]};
        nodesR[0].setOutgoingEdges(edgesRootRightO);

        Edge[] edgesSecondRightO = {edgesR[4]};
        nodesR[1].setOutgoingEdges(edgesSecondRightO);

        Edge[] edgesThirdRightO = {edgesR[2], edgesR[6]};
        nodesR[2].setOutgoingEdges(edgesThirdRightO);

        Edge[] edgesSFourthRightO = {};
        nodesR[3].setOutgoingEdges(edgesSFourthRightO);

        Edge[] edgesFifthRightO = {edgesR[3], edgesR[5]};
        nodesR[4].setOutgoingEdges(edgesFifthRightO);
    }

    private void setOnTouchGraph(MotionEvent event, DirectedGraph directedGraph) {
        DatabaseReference graphRef;
        boolean left;
        Node nodeTouched;
        Node sNode;
        if (false) {
            // no move button
        } else {
            if (directedGraph != null) {
                if (directedGraph.getNodes() != null) {
                    for (Node node : directedGraph.getNodes()) {
                        if (node != null) {
                            // control whether the touch is in the circle or not
                            boolean touchInCircle = touchIsInCircle(event.getX(), event.getY(), node.getX(), node.getY(), radius);
                            if (touchInCircle) {
                                nodeTouched = node;
                                left = node.isLeftTable();
                                // get id of node touched
                                int idNodeTouched = node.getId();
                                // get id of previous selected node (left or right)
                                String selectedNode;
                                if (node.isLeftTable()) {
                                    selectedNode = selectedNodeLeft.getText().toString().trim();
                                } else {
                                    selectedNode = selectedNodeRight.getText().toString().trim();
                                }
                                sNode = stringToNode(selectedNode, node.isLeftTable());
                                // set reference to proper graph
                                if (left) {
                                    graphRef = leftGraphRef;
                                } else {
                                    graphRef = rightGraphRef;
                                }
                                refreshNodes(graphRef, sNode, nodeTouched);
                                refreshTurnOf();
                            }
                        }
                    }
                }
            }
        }
    }

    private void refreshNodes(DatabaseReference graphRef, Node selectedNode, Node nodeTouched) {
        // set selected node to colour --> black and selected --> false
        switch (selectedNode.getId()) {
            case 1:
                graphRef.child("Node one").child("Colour").setValue("black");
                graphRef.child("Node one").child("Selected").setValue("false");
                break;
            case 2:
                graphRef.child("Node two").child("Colour").setValue("black");
                graphRef.child("Node two").child("Selected").setValue("false");
                break;
            case 3:
                graphRef.child("Node three").child("Colour").setValue("black");
                graphRef.child("Node three").child("Selected").setValue("false");
                break;
            case 4:
                graphRef.child("Node four").child("Colour").setValue("black");
                graphRef.child("Node four").child("Selected").setValue("false");
                break;
            case 5:
                graphRef.child("Node five").child("Colour").setValue("black");
                graphRef.child("Node five").child("Selected").setValue("false");
                break;
            default:
                break;
        }
        // set selected node to colour --> blue and selected --> true
        switch (nodeTouched.getId()) {
            case 1:
                graphRef.child("Node one").child("Colour").setValue("blue");
                graphRef.child("Node one").child("Selected").setValue("true");
                break;
            case 2:
                graphRef.child("Node two").child("Colour").setValue("blue");
                graphRef.child("Node two").child("Selected").setValue("true");
                break;
            case 3:
                graphRef.child("Node three").child("Colour").setValue("blue");
                graphRef.child("Node three").child("Selected").setValue("true");
                break;
            case 4:
                graphRef.child("Node four").child("Colour").setValue("blue");
                graphRef.child("Node four").child("Selected").setValue("true");
                break;
            case 5:
                graphRef.child("Node five").child("Colour").setValue("blue");
                graphRef.child("Node five").child("Selected").setValue("true");
                break;
            default:
                break;
        }
    }
        /*
        if (event == null && directedGraph == null) {
            if (isValidMove(null, null)) {
                refreshTurnOf();
            }
        } else {
            if (directedGraph.getNodes() != null) {
                for (Node node : directedGraph.getNodes()) {
                    if (node != null) {
                        boolean isTouched = touchIsInCircle(event.getX(), event.getY(), node.getX(), node.getY(), radius);
                        if (isTouched) {
                            // assegna all'id
                            int id;
                            if (node.isLeftTable()) {
                                id = stringToID(selectedNodeLeft.getText().toString().trim());
                            } else {
                                id = stringToID(selectedNodeRight.getText().toString().trim());
                            }
                            Node startNode = null;
                            for (Node n : directedGraph.getNodes()) {
                                if (n.getId() == id) {
                                    startNode = n;
                                }
                            }
                            if (isValidMove(startNode, node)) {
                                Node nodeLeftSelected = stringToNode(selectedNodeLeft.getText().toString().trim(), true);
                                Node nodeRightSelected = stringToNode(selectedNodeRight.getText().toString().trim(), false);
                                controlMatrix(nodeLeftSelected, nodeRightSelected, node);
                                DatabaseReference graphRef;
                                if (node.isLeftTable()) {
                                    graphRef = leftGraphRef;
                                } else {
                                    graphRef = rightGraphRef;
                                }
                                switch (node.getId()) {
                                    case 1:
                                        setTouchedCircle(graphRef.child("Node one"), false);
                                        directedGraph.getNodes()[0].setColor(getResources().getColor(R.color.primaryColor));
                                        break;
                                    case 2:
                                        setTouchedCircle(graphRef.child("Node two"), false);
                                        directedGraph.getNodes()[1].setColor(getResources().getColor(R.color.primaryColor));
                                        break;
                                    case 3:
                                        setTouchedCircle(graphRef.child("Node three"), false);
                                        directedGraph.getNodes()[2].setColor(getResources().getColor(R.color.primaryColor));
                                        break;
                                    case 4:
                                        setTouchedCircle(graphRef.child("Node four"), false);
                                        directedGraph.getNodes()[3].setColor(getResources().getColor(R.color.primaryColor));
                                        break;
                                    case 5:
                                        setTouchedCircle(graphRef.child("Node five"), false);
                                        directedGraph.getNodes()[4].setColor(getResources().getColor(R.color.primaryColor));
                                        break;
                                    default:
                                        break;
                                }
                                refreshNodes(directedGraph, node.isLeftTable(), node.getId());
                                refreshTurnOf();
                                setSelectedNode(node.isLeftTable());
                                directedGraph.invalidate();
                            } else {
                                if (possibleMoves()) {
                                    // there are possible move yet
                                    Toast.makeText(this, getString(R.string.table_possible_moves), Toast.LENGTH_LONG).show();
                                } else {
                                    // there are no possible moves --> end game
                                    roomNameRef.child("gameInProgress").setValue("false");
                                }
                            }
                        }
                    }
                }
            }
        }*/

    private void controlMatrix(Node nodeLeftSelected, Node nodeRightSelected, Node nodeTouched) {
        System.out.println("Before...");
        for (int i = 0; i < controlMatrix.length; i++) {
            for (int j = 0; j < controlMatrix[i].length; j++) {
                System.out.print(controlMatrix[i][j] + " ");
            }
            System.out.println();
        }
        if (nodeTouched.isLeftTable()) {
            controlMatrix[(nodeTouched.getId() - 1)][(nodeRightSelected.getId() - 1)] = 1;
        } else {
            controlMatrix[(nodeLeftSelected.getId() - 1)][(nodeTouched.getId() - 1)] = 1;
        }
        System.out.println("After...");
        for (int i = 0; i < controlMatrix.length; i++) {
            for (int j = 0; j < controlMatrix[i].length; j++) {
                System.out.print(controlMatrix[i][j] + " ");
            }
            System.out.println();
        }
        if (controlMatrix[(nodeLeftSelected.getId() - 1)][(nodeRightSelected.getId() - 1)] == 1) {
            // configuration already visited after defender move --> end game
            if (turnoDi.getText().toString().equalsIgnoreCase(getString(R.string.table_attacker))) {
                roomNameRef.child("gameInProgress").setValue("false");
            }
        }
    }

    private Node stringToNode(String string, boolean left) {
        Node returnNode = null;
        int id = stringToID(string);
        if (left) {
            for (Node node : directedGraphLeft.getNodes()) {
                if (node.getId() == id) {
                    returnNode = node;
                }
            }
        } else {
            for (Node node : directedGraphRight.getNodes()) {
                if (node.getId() == id) {
                    returnNode = node;
                }
            }
        }
        return returnNode;
    }

    private boolean possibleMoves() {
        String sNodeLeft = selectedNodeLeft.getText().toString();
        String sNodeRight = selectedNodeRight.getText().toString();
        boolean leftAvailable = false;
        boolean rightAvailable = false;
        Node startNode;
        int counter = 0;
        if (turnoDi.getText().toString().equalsIgnoreCase(getString(R.string.table_attacker))) {
            startNode = stringToNode(sNodeLeft, true);
            if (startNode != null) {
                for (Edge edge : startNode.getOutgoingEdges()) {
                    // se trova almeno un arco vuol dire che ci sono ancora mosse possibili
                    if (edge != null) {
                        counter++;
                    }
                }
                if (counter != 0) {
                    leftAvailable = true;
                }
            }
        } else {
            startNode = stringToNode(sNodeRight, false);
            if (startNode != null) {
                for (Edge edge : startNode.getOutgoingEdges()) {
                    // se trova almeno un arco uscente dallo start node del colore dell'ultima mossa --> ci sono ancora mosse possibili
                    if (colourToString(edge.getColor()).equalsIgnoreCase(lastMoveColour.getText().toString())) {
                        counter++;
                    }
                }
                if (counter != 0) {
                    rightAvailable = true;
                }
            }
        }
        return leftAvailable && rightAvailable;
    }

    private boolean isValidMove(Node startNode, Node nodeTouched) {
        if (startNode == null && nodeTouched == null) {
            if (lastMoveColour.getText().toString().equalsIgnoreCase(coloreSpeciale.getText().toString())) {
                roomNameRef.child("noMoveButtonEnabled").setValue("false");
                return true;
            } else {
                return false;
            }
        }
        if (turnoDi.getText().toString().equalsIgnoreCase(getString(R.string.table_attacker))) {
            Log.i(TAG, "Strong move");
            return isStrongMove(startNode, nodeTouched);
        } else {
            Log.i(TAG, "Weak move");
            return isWeakMove(startNode, nodeTouched);
        }
    }

    private boolean isStrongMove(Node startNode, Node nodeTouched) {
        if (turnoDi.getText().toString().equalsIgnoreCase(getString(R.string.table_attacker))) {
            if (startNode.getOutgoingEdges() != null) {
                for (Edge edge : startNode.getOutgoingEdges()) {
                    if (edge.getTwo().getId() == nodeTouched.getId()) {
                        String colore = colourToString(edge.getColor());
                        roomNameRef.child("lastMoveColour").setValue(colore);
                        roomNameRef.child("noMoveButtonEnabled").setValue("true");
                        return true;
                    }
                }
                Toast.makeText(this, "MOSSA NON CONSENTITA PER L'ATTACCANTE", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return false;
    }

    private boolean isWeakMove(Node startNode, Node nodeTouched) {
        Node firstNode;
        String specialC = coloreSpeciale.getText().toString();
        // se colore speciale è uguale al colore dell'ultima mossa
        if (coloreSpeciale.getText().toString().equalsIgnoreCase(lastMoveColour.getText().toString())) {
            // cicla sugli archi uscenti dell'arco di partenza
            for (Edge edge : startNode.getOutgoingEdges()) {
                // se il colore dell'arco in questione è diverso dal colore speciale ritorna false
                if (!colourToString(edge.getColor()).equalsIgnoreCase(specialC)) {
                    return false;
                } else {
                    // se il colore dell'arco è del colore speciale salva il nodo raggiunto
                    firstNode = edge.getTwo();
                    // cicla sugli archi uscenti del nodo salvato
                    for (Edge e : firstNode.getOutgoingEdges()) {
                        // se il colore dell'arco è diverso dal colore speciale ritorna false
                        if (!colourToString(e.getColor()).equalsIgnoreCase(specialC)) {
                            return false;
                        } else {
                            // se gli id combaciano ritorna true
                            if (e.getTwo().getId() == nodeTouched.getId()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        String lastMoveC = lastMoveColour.getText().toString();
        // cicla sugli archi uscenti del nodo di partenza
        for (Edge edge : startNode.getOutgoingEdges()) {
            // legge un arco del colore dell'ultima mossa --> non può più leggerne di questo colore
            if (colourToString(edge.getColor()).equalsIgnoreCase(lastMoveC)) {
                // salva il nodo raggiunto
                firstNode = edge.getTwo();
                // cicla sugli archi uscenti del nodo salvato
                for (Edge e : firstNode.getOutgoingEdges()) {
                    // se legge un arco che non è del colore speciale ritorna false
                    if (!colourToString(e.getColor()).equalsIgnoreCase(specialC)) {
                        return false;
                    } // legge un arco del colore speciale --> devo verificare che il nodo raggiunto sia uguale al nodo toccato
                    else {
                        // gli ids coincidono --> return true
                        if (e.getTwo().getId() == nodeTouched.getId()) {
                            return true;
                        }
                    }
                }
                // i nodi raggiunti dagli archi uscenti del primo nodo (che è diverso dallo startNode)
                return false;
            }
            // se invece legge subito un arco del colore speciale --> deve leggerne almeno uno del colore dell'ultima mossa
            else if (colourToString(edge.getColor()).equalsIgnoreCase(specialC)) {
                firstNode = edge.getTwo();
                for (Edge e : firstNode.getOutgoingEdges()) {
                    if (!colourToString(e.getColor()).equalsIgnoreCase(specialC)) {
                        // continua a ciclare --> come farlo?
                    }
                }
            } else {
                // il primo arco letto non è ne del colore speciale nè del colore dell'ultima mossa
                return false;
            }
        }
        return true;
    }

    private int stringToID(String sNode) {
        switch (sNode) {
            case "Node one":
                return 1;
            case "Node two":
                return 2;
            case "Node three":
                return 3;
            case "Node four":
                return 4;
            case "Node five":
                return 5;
            default:
                break;
        }
        return -1;
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

    private void refreshTurnOf() {
        if (turnoDi.getText().toString().equalsIgnoreCase(getString(R.string.table_attacker))) {
            roomsRef.child(roomName).child("turnOf").setValue(getString(R.string.table_defender));
        } else {
            roomsRef.child(roomName).child("turnOf").setValue(getString(R.string.table_attacker));
        }
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

    private boolean touchIsInCircle(float x, float y, float centreX, float centreY,
                                    float radius) {
        double dx = Math.pow(x - centreX, 2);
        double dy = Math.pow(y - centreY, 2);

        return (dx + dy) < Math.pow(radius, 2);
    }

    private void setAttacker() {
        getPlayerOneName(new CallbackPlayerOne() {
            @Override
            public void onCallbackPlayerOneName(String playerName) {
                if (playerName != null) {
                    attacker.setText(playerName);
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
                if (playerName != null) {
                    defender.setText(playerName);
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

    private void setTextColour(String sColour) {
        switch (sColour) {
            case "red":
                coloreSpeciale.setTextColor(getResources().getColor(R.color.red));
                break;
            case "green":
                coloreSpeciale.setTextColor(getResources().getColor(R.color.green));
                break;
            case "black":
                coloreSpeciale.setTextColor(getResources().getColor(R.color.black));
                break;
            case "blue":
                coloreSpeciale.setTextColor(getResources().getColor(R.color.primaryColor));
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
                if (turnOf != null) {
                    turnoDi.setText(turnOf);
                }
            }
        });
    }

    private void getTurnOf(DatabaseReference roomNameRef, CallbackTurnOf callback) {
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

    private void setLastMoveColour() {
        getLastMoveColour(roomsRef.child(roomName), new CallbackLastMoveColour() {
            @Override
            public void onCallbackLastMoveColour(String colour) {
                if (colour != null) {
                    lastMoveColour.setText(colour);
                }
            }
        });
    }

    private void getLastMoveColour(DatabaseReference roomNameRef, CallbackLastMoveColour
            callback) {
        roomNameRef.child("lastMoveColour").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.toString() != null) {
                    String value = snapshot.getValue().toString();
                    callback.onCallbackLastMoveColour(value);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setSelectedNode(boolean leftTable) {
        getSelectedNode(new CallbackSelectedNode() {
            @Override
            public void onCallbackSelectedNodeOne(String sNode) {
                if (sNode != null) {
                    if (sNode.equalsIgnoreCase("true")) {
                        if (leftTable) {
                            selectedNodeLeft.setText("Node one");
                        } else {
                            selectedNodeRight.setText("Node one");
                        }
                    }
                }
            }

            @Override
            public void onCallbackSelectedNodeTwo(String sNode) {
                if (sNode != null) {
                    if (sNode.equalsIgnoreCase("true")) {
                        if (leftTable) {
                            selectedNodeLeft.setText("Node two");
                        } else {
                            selectedNodeRight.setText("Node two");
                        }
                    }
                }
            }

            @Override
            public void onCallbackSelectedNodeThree(String sNode) {
                if (sNode != null) {
                    if (sNode.equalsIgnoreCase("true")) {
                        if (leftTable) {
                            selectedNodeLeft.setText("Node three");
                        } else {
                            selectedNodeRight.setText("Node three");
                        }
                    }
                }
            }

            @Override
            public void onCallbackSelectedNodeFour(String sNode) {
                if (sNode != null) {
                    if (sNode.equalsIgnoreCase("true")) {
                        if (leftTable) {
                            selectedNodeLeft.setText("Node four");
                        } else {
                            selectedNodeRight.setText("Node four");
                        }
                    }
                }
            }

            @Override
            public void onCallbackSelectedNodeFive(String sNode) {
                if (sNode != null) {
                    if (sNode.equalsIgnoreCase("true")) {
                        if (leftTable) {
                            selectedNodeLeft.setText("Node five");
                        } else {
                            selectedNodeRight.setText("Node five");
                        }
                    }
                }
            }
        }, leftTable);
    }

    private void getSelectedNode(CallbackSelectedNode callback, boolean leftTable) {
        DatabaseReference reference;
        if (leftTable) {
            reference = leftGraphRef;
        } else {
            reference = rightGraphRef;
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (Objects.requireNonNull(dataSnapshot.getKey()).equalsIgnoreCase("Node one")) {
                        String value = Objects.requireNonNull(dataSnapshot.child("Selected").getValue()).toString();
                        callback.onCallbackSelectedNodeOne(value);
                    } else if (dataSnapshot.getKey().equalsIgnoreCase("Node two")) {
                        String value = Objects.requireNonNull(dataSnapshot.child("Selected").getValue()).toString();
                        callback.onCallbackSelectedNodeTwo(value);
                    } else if (dataSnapshot.getKey().equalsIgnoreCase("Node three")) {
                        String value = Objects.requireNonNull(dataSnapshot.child("Selected").getValue()).toString();
                        callback.onCallbackSelectedNodeThree(value);
                    } else if (dataSnapshot.getKey().equalsIgnoreCase("Node four")) {
                        String value = Objects.requireNonNull(dataSnapshot.child("Selected").getValue()).toString();
                        callback.onCallbackSelectedNodeFour(value);
                    } else if (dataSnapshot.getKey().equalsIgnoreCase("Node five")) {
                        String value = Objects.requireNonNull(dataSnapshot.child("Selected").getValue()).toString();
                        callback.onCallbackSelectedNodeFive(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setNoMove() {
        getNoMove(new CallbackNoMove() {
            @Override
            public void onCallbackNoMove(String value) {
                if (value != null) {
                    if (value.equalsIgnoreCase("true")) {
                        if (lastMoveColour.getText().toString().equalsIgnoreCase(coloreSpeciale.getText().toString())) {
                            noMove.setEnabled(true);
                            noMove.setClickable(true);
                            noMove.setFocusable(true);
                            Log.i(TAG, "Enabled button");
                        }
                    } else {
                        noMove.setEnabled(false);
                        noMove.setClickable(false);
                        noMove.setFocusable(false);
                        Log.i(TAG, "Disabled button");
                    }
                }
            }
        });
    }

    private void getNoMove(CallbackNoMove callback) {
        roomNameRef.child("noMoveButtonEnabled").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString() != null) {
                    String value = snapshot.getValue().toString();
                    callback.onCallbackNoMove(value);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setEndGame() {
        getGameInProgress(new CallbackGameInProgress() {
            @Override
            public void onCallbackGameInProgress(String value) {
                if (value != null) {
                    if (value.equalsIgnoreCase("false")) {
                        new AlertDialog.Builder(getApplicationContext()).setTitle(getString(R.string.alert_end_game))
                                .setMessage(getString(R.string.alert_win_congrats) + " " + playerName)
                                .setPositiveButton(R.string.alert_continue, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                    }
                }
            }
        });
    }

    private void getGameInProgress(CallbackGameInProgress callback) {
        roomNameRef.child("gameInProgress").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = snapshot.getValue().toString();
                    callback.onCallbackGameInProgress(value);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
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

    @Override
    public void onCallbackLastMoveColour(String colour) {
    }

    @Override
    public void onCallbackSelectedNodeOne(String selectedNode) {

    }

    @Override
    public void onCallbackSelectedNodeTwo(String selectedNode) {

    }

    @Override
    public void onCallbackSelectedNodeThree(String selectedNode) {

    }

    @Override
    public void onCallbackSelectedNodeFour(String selectedNode) {

    }

    @Override
    public void onCallbackSelectedNodeFive(String selectedNode) {

    }

    @Override
    public void onCallbackNoMove(String value) {

    }

    @Override
    public void onCallbackGameInProgress(String value) {

    }
}