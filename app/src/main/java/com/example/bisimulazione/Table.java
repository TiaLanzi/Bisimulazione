package com.example.bisimulazione;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.bisimulazione.directedgraph.DirectedGraph;
import com.example.bisimulazione.directedgraph.DirectedGraphLeft;
import com.example.bisimulazione.directedgraph.DirectedGraphRight;
import com.example.bisimulazione.directedgraph.Edge;
import com.example.bisimulazione.directedgraph.Node;
import com.example.bisimulazione.models.Pair;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import interfaces.CallbackNoMove;
import interfaces.CallbackEnabledLeftGraph;
import interfaces.CallbackEnabledRightGraph;
import interfaces.CallbackGameInProgress;
import interfaces.CallbackLastMoveColour;
import interfaces.CallbackPlayerOne;
import interfaces.CallbackPlayerTwo;
import interfaces.CallbackSelectedNode;
import interfaces.CallbackTurnOf;

public class Table extends AppCompatActivity implements CallbackTurnOf, CallbackPlayerOne, CallbackPlayerTwo, CallbackLastMoveColour, CallbackSelectedNode, CallbackGameInProgress, CallbackEnabledLeftGraph, CallbackEnabledRightGraph, CallbackNoMove {

    private static final String TAG = "Bisimulazione";

    private String playerName;
    private String roomName;
    private String role;
    private String specialColour;

    private TextView coloreSpeciale;
    private TextView turnoDi;
    private TextView attacker;
    private TextView defender;
    private TextView lastMoveColour;
    private TextView selectedNodeLeft;
    private TextView selectedNodeRight;

    private Button noMoveBtn;

    private FirebaseDatabase database;

    private DatabaseReference roomsRef;
    private DatabaseReference roomNameRef;
    private DatabaseReference leftGraphRef;
    private DatabaseReference rightGraphRef;

    private com.example.bisimulazione.directedgraph.DirectedGraphLeft directedGraphLeft;
    private com.example.bisimulazione.directedgraph.DirectedGraphRight directedGraphRight;

    private Node[] nodesL;
    private Edge[] edgesL;

    private Node[] nodesR;
    private Edge[] edgesR;

    private Node sNode;
    private Node nTouched;

    private Map<Node, Boolean> visitedNodes;

    private HashSet<Pair> pairHashSet;

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
        noMoveBtn = findViewById(R.id.table_no_move_button);
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
        database = FirebaseDatabase.getInstance();
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

        // set info box
        setInfoBox();
        // enable / disable graphs
        enableGraphs();
        // end game
        setEndGame();

        // set nodes and edges
        setNodesAndEdges();

        // initialize directed graph left
        directedGraphLeft = new DirectedGraphLeft(this);
        // set nodes for left graph
        directedGraphLeft.setNodes(nodesL);
        // set edges for left graph
        directedGraphLeft.setEdges(edgesL);

        // must be implemented following config pattern
        setOutgoingEdgesLeft();

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
                return true;
            }
        });

        directedGraphRight = new DirectedGraphRight(this);

        directedGraphRight.setNodes(nodesR);
        directedGraphRight.setEdges(edgesR);

        // must be implemented following config pattern
        setOutgoingEdgesRight();

        rightGraphRef = roomNameRef.child("rightGraph");
        directedGraphRight.setReference(rightGraphRef);

        LinearLayout layoutDirectedGraphRight = findViewById(R.id.table_right_directed_graph_layout);
        layoutDirectedGraphRight.addView(directedGraphRight);

        selectedNodeLeft.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                refreshDirectedGraph(directedGraphLeft, s.toString().trim(), true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshDirectedGraph(directedGraphLeft, s.toString().trim(), false);
            }
        });

        selectedNodeRight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                refreshDirectedGraph(directedGraphRight, s.toString(), true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshDirectedGraph(directedGraphRight, s.toString(), false);
            }
        });

        directedGraphRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setOnTouchGraph(event, (DirectedGraphRight) v);
                }
                return true;
            }
        });

        // initialize pair hash set
        pairHashSet = new HashSet<>();
    }

    private void refreshDirectedGraph(DirectedGraph directedGraph, String selectedNode, boolean bool) {
        if (directedGraph.getNodes() != null) {
            for (Node node : directedGraph.getNodes()) {
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
        setTextColourSpecial(specialColour);
        // set turn of text
        setTurnOf();
        // set last move colour
        setLastMoveColour();
        // set selected node left table
        setSelectedNode(true);
        // set selected node right table
        setSelectedNode(false);
        // set no move
        setNoMove();
    }

    private void setNodesAndEdges() {
        readConfigFile();
    }

    private void readConfigFile() {
        boolean foundConfig = false;
        int configNumber = 1;
        BufferedReader bufferedReader;

        try {
            final InputStream file = getAssets().open("configs");
            bufferedReader = new BufferedReader(new InputStreamReader(file));
            String line = bufferedReader.readLine();
            while (line != null && !foundConfig) {
                char first = line.charAt(0);
                if (first != '#') {
                    if (first >= 48 && first <= 57) {
                        int number = (int) first - '0';
                        if (configNumber == number) {
                            String[] config = readConfig(bufferedReader);
                            generateNodes(config[0], config[1]);
                            generateEdges(config[2], config[3]);
                            foundConfig = true;
                        } else {
                            skipConfig(bufferedReader);
                        }
                    }
                } else {
                    // read \n character and go to next line
                    line = bufferedReader.readLine();
                }
            }
            bufferedReader.close();
        } catch (IOException ioException) {
            Log.e(TAG, "IOException.");
            ioException.printStackTrace();

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            Log.e(TAG, "Index out of bounds exception.");
        }
    }

    private void skipConfig(BufferedReader bufferedReader) {
        for (int i = 0; i < 8; i++) {
            try {
                bufferedReader.readLine();
            } catch (IOException ioException) {
                Log.e(TAG, "IOException");
                ioException.printStackTrace();
            }
        }
    }

    private String[] readConfig(BufferedReader bufferedReader) {
        int counter = 0;
        String line;
        String[] config = new String[4];
        for (int i = 0; i < 4; i++) {
            try {
                line = bufferedReader.readLine();
                config[counter] = line;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            counter++;
        }
        return config;
    }

    private void generateNodes(String nodesLineLeft, String nodesLineRight) {
        String[] nodesLeft = nodesLineLeft.split(";");
        nodesL = new Node[nodesLeft.length];
        for (int i = 0; i < nodesL.length; i++) {
            Node node = createNode(nodesLeft[i], true);
            nodesL[i] = node;
        }

        String[] nodesRight = nodesLineRight.split(";");
        nodesR = new Node[nodesRight.length];
        for (int j = 0; j < nodesR.length; j++) {
            Node n = createNode(nodesRight[j], false);
            nodesR[j] = n;
        }
    }

    private Node createNode(String node, boolean leftTable) {
        String[] attributes = node.split(",");
        int id = Integer.parseInt(attributes[0]);
        boolean root = false;
        int x = 0;
        int y = 200;
        if (Integer.parseInt(attributes[1]) == 0) {
            root = true;
        } else {
            Node parent = getNodeFromId(Integer.parseInt(attributes[1]), leftTable);
            x = parent.getX();
            y = parent.getY();
        }
        boolean toLeft = Boolean.parseBoolean(attributes[2]);
        boolean toRight = Boolean.parseBoolean(attributes[3]);
        int colour = stringToColour(attributes[4]);
        Node n = new Node(id, x, y, root, toLeft, toRight, leftTable, colour);
        return n;
    }

    private Node getNodeFromId(int id, boolean leftTable) {
        Node[] nodes;
        if (leftTable) {
            nodes = nodesL;
        } else {
            nodes = nodesR;
        }

        if (nodes != null) {
            for (Node node : nodes) {
                if (node != null) {
                    if (node.getId() == id) {
                        return node;
                    }
                }
            }
        }
        return null;
    }

    private int stringToColour(String colour) {
        int colore = -1;
        switch (colour) {
            case "red":
                colore = -237502;
                break;
            case "green":
                colore = -16711895;
                break;
            case "black":
                colore = -13421773;
                break;
            case "blue":
                colore = -15774591;
                break;
            default:
                break;
        }
        return colore;
    }

    private void generateEdges(String edgesLineLeft, String edgesLineRight) {
        String[] edgesLeft = edgesLineLeft.split(";");
        edgesL = new Edge[edgesLeft.length];
        for (int i = 0; i < edgesL.length; i++) {
            Edge edge = createEdge(edgesLeft[i], true);
            edgesL[i] = edge;
        }

        String[] edgesRight = edgesLineRight.split(";");
        edgesR = new Edge[edgesRight.length];
        for (int j = 0; j < edgesR.length; j++) {
            Edge e = createEdge(edgesRight[j], false);
            edgesR[j] = e;
        }
    }

    private Edge createEdge(String edge, boolean leftTable) {
        String[] attributes = edge.split(",");
        int id = Integer.parseInt(attributes[0]);
        Node source = getNodeFromId(Integer.parseInt(attributes[1]), leftTable);
        Node destination = getNodeFromId(Integer.parseInt(attributes[2]), leftTable);
        int colour = stringToColour(attributes[3]);
        boolean toBottom = Boolean.parseBoolean(attributes[4]);
        boolean line = Boolean.parseBoolean(attributes[5]);
        Edge e = new Edge(id, source, destination, colour, leftTable, toBottom, line);
        return e;
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
        if (directedGraph != null) {
            if (directedGraph.getNodes() != null) {
                for (Node node : directedGraph.getNodes()) {
                    if (node != null) {
                        // control whether the touch is in the circle or not
                        boolean touchInCircle = touchIsInCircle(event.getX(), event.getY(), node.getX(), node.getY(), radius);
                        if (touchInCircle) {
                            nTouched = node;
                            String selectedNode;
                            if (node.isLeftTable()) {
                                selectedNode = selectedNodeLeft.getText().toString().trim();
                            } else {
                                selectedNode = selectedNodeRight.getText().toString().trim();
                            }
                            // get selected node
                            sNode = stringToNode(selectedNode, node.isLeftTable());
                            // case no move
                            if (sNode.getId() == nTouched.getId()) {
                                Toast.makeText(this, getResources().getString(R.string.table_no_move_btn_text), Toast.LENGTH_LONG).show();
                            } else {
                                if (isValidMove(sNode, nTouched)) {
                                    // set reference to proper graph
                                    left = node.isLeftTable();
                                    if (left) {
                                        graphRef = leftGraphRef;
                                    } else {
                                        graphRef = rightGraphRef;
                                    }
                                    refreshNodes(graphRef, sNode, nTouched);
                                    controlConfiguration();
                                    refreshNoMove();
                                    refreshTurnOf();
                                } else {
                                    Toast.makeText(this, getResources().getString(R.string.table_invalid_move_defender), Toast.LENGTH_LONG).show();
                                    if (possibleMoves(sNode)) {
                                        Toast.makeText(this, getResources().getString(R.string.table_possible_moves), Toast.LENGTH_LONG).show();
                                    } else {
                                        roomNameRef.child("gameInProgress").setValue(String.valueOf(false));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        noMoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sNode != null && nTouched != null) {
                    DatabaseReference graphRef;
                    if (sNode.isLeftTable()) {
                        graphRef = leftGraphRef;
                    } else {
                        graphRef = rightGraphRef;
                    }
                    if (controlNoMove(sNode)) {
                        roomNameRef.child("noMove").setValue(String.valueOf(false));
                        updateValidWeakMove();
                        refreshNodes(graphRef, sNode, nTouched);
                        controlConfiguration();
                        refreshTurnOf();
                    } else {
                        roomNameRef.child("noMove").setValue(String.valueOf(true));
                    }
                } else {
                    Toast.makeText(Table.this, getResources().getString(R.string.table_invalid_move_defender), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean controlNoMove(Node selectedNode) {
        if (selectedNode != null) {
            if (turnoDi.getText().toString().trim().equalsIgnoreCase(getString(R.string.table_defender))) {
                if (coloreSpeciale.getText().toString().trim().equalsIgnoreCase(lastMoveColour.getText().toString().trim())) {
                    return true;
                } else {
                    // special cases
                    if (coloreSpeciale.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.table_green)) && lastMoveColour.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.table_red))) {
                        if (sNode.isLeftTable()) {
                            if ((sNode.getId() == 1) || (sNode.getId() == 2) || (sNode.getId() == 3)) {
                                Toast.makeText(this, getResources().getString(R.string.table_cycle), Toast.LENGTH_LONG).show();
                                return true;
                            } else {
                                Toast.makeText(this, getResources().getString(R.string.table_invalid_move_defender), Toast.LENGTH_LONG).show();
                                return false;
                            }
                        } else {
                            if ((sNode.getId() == 1) || (sNode.getId() == 2)) {
                                Toast.makeText(this, getResources().getString(R.string.table_cycle), Toast.LENGTH_LONG).show();
                                return true;
                            } else {
                                Toast.makeText(this, getResources().getString(R.string.table_invalid_move_defender), Toast.LENGTH_LONG).show();
                                return false;
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.table_invalid_move_attacker), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return false;
    }

    private void refreshNoMove() {
        String turnOf = turnoDi.getText().toString().trim();
        if (turnOf.equalsIgnoreCase(getResources().getString(R.string.table_attacker))) {
            roomNameRef.child("noMove").setValue(String.valueOf(true));
        } else {
            roomNameRef.child("noMove").setValue(String.valueOf(false));
        }
    }

    private void controlConfiguration() {
        if (turnoDi.getText().toString().equalsIgnoreCase(getResources().getString(R.string.table_defender))) {
            Node sNodeLeft = stringToNode(selectedNodeLeft.getText().toString().trim(), true);
            Node sNodeRight = stringToNode(selectedNodeRight.getText().toString().trim(), false);
            if (sNodeLeft != null && sNodeRight != null) {
                Pair pair = new Pair(sNodeLeft, sNodeRight);
                if (pairHashSet.contains(pair)) {
                    Toast.makeText(this, getResources().getString(R.string.table_configuration_already_visited), Toast.LENGTH_LONG).show();
                    roomNameRef.child("gameInProgress").setValue(String.valueOf(false));
                }
                pairHashSet.add(pair);
            }
        }
    }

    private void removeReferences() {
        // remove reference of the room
        if (roomNameRef != null) {
            roomNameRef.removeValue();
        }
        // remove players name
        if (attacker.getText() != null) {
            FirebaseDatabase.getInstance().getReference().child("players").child(attacker.getText().toString().trim()).removeValue();
        }
        if (defender.getText() != null) {
            FirebaseDatabase.getInstance().getReference().child("players").child(defender.getText().toString().trim()).removeValue();
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

    private boolean possibleMoves(Node startNode) {
        if (turnoDi.getText().toString().trim().equalsIgnoreCase(getString(R.string.table_attacker))) {
            return possibleMovesAttacker(startNode);
        } else {
            return possibleMovesDefender(startNode);
        }
    }

    private boolean possibleMovesAttacker(Node startNode) {
        int counterLeft = 0;
        int counterRight = 0;
        String selectedNode;
        Node startNodeLeft;
        Node startNodeRight;
        if (startNode.isLeftTable()) {
            startNodeLeft = startNode;
            selectedNode = selectedNodeRight.getText().toString().trim();
            startNodeRight = stringToNode(selectedNode, false);
        } else {
            startNodeRight = startNode;
            selectedNode = selectedNodeLeft.getText().toString().trim();
            startNodeLeft = stringToNode(selectedNode, true);
        }
        for (Edge edge : startNodeLeft.getOutgoingEdges()) {
            if (edge != null) {
                counterLeft++;
            }
        }
        for (Edge edge : startNodeRight.getOutgoingEdges()) {
            if (edge != null) {
                counterRight++;
            }
        }
        return counterLeft != 0 || counterRight != 0;
    }

    private boolean possibleMovesDefender(Node startNode) {
        visitedNodes = new HashMap<>(5);
        DirectedGraph directedGraph;
        if (startNode.isLeftTable()) {
            directedGraph = directedGraphLeft;
        } else {
            directedGraph = directedGraphRight;
        }
        for (Node node : directedGraph.getNodes()) {
            for (Edge edge : node.getOutgoingEdges()) {
                if (isWeakMove(node, edge.getDestination(), false, visitedNodes)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isValidMove(Node startNode, Node nodeTouched) {
        // if turn of attacker --> strong move else --> weak move
        if (turnoDi.getText().toString().equalsIgnoreCase(getString(R.string.table_attacker))) {
            return isStrongMove(startNode, nodeTouched);
        } else {
            visitedNodes = new HashMap<>(5);
            return isWeakMove(startNode, nodeTouched, false, visitedNodes);
        }
    }

    private boolean isStrongMove(Node startNode, Node nodeTouched) {
        // check if start node and node touched are not null
        if (startNode != null && nodeTouched != null) {
            // check if nodes are not null
            if (startNode.getOutgoingEdges() != null) {
                // loop on outgoing edges of start node
                for (Edge edge : startNode.getOutgoingEdges()) {
                    // check if edge is not null
                    if (edge != null) {
                        // check if second node of edge has the same id of the node touched
                        if (edge.getDestination().getId() == nodeTouched.getId()) {
                            String colore = colourToString(edge.getColor());
                            // set colour of the move
                            roomNameRef.child("lastMoveColour").setValue(colore);
                            if (nodeTouched.isLeftTable()) {
                                roomNameRef.child("leftGraph").child("enabled").setValue(String.valueOf(false));
                            } else {
                                roomNameRef.child("rightGraph").child("enabled").setValue(String.valueOf(false));
                            }
                            return true;
                        }
                    }
                }
                // no second node edges has the same id of the node touched --> invalid move --> return false
                Toast.makeText(this, getResources().getString(R.string.table_invalid_move_attacker), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return false;
    }

    private boolean isWeakMove(Node startNode, Node nodeTouched, boolean atLeastOneArcOfLastMoveColour, Map<Node, Boolean> visitedNodes) {
        String lmc = lastMoveColour.getText().toString().trim();
        String sc = coloreSpeciale.getText().toString().trim();
        boolean found = false;
        if (lmc.equalsIgnoreCase(sc)) {
            for (Edge edge : startNode.getOutgoingEdges()) {
                if (!colourToString(edge.getColor()).equalsIgnoreCase(sc)) {
                    continue;
                } else {
                    found = true;
                }
            }
            return found;
        }

        if (visitedNodes.containsKey(startNode)) {
            if (visitedNodes.get(startNode)) {
                updateValidWeakMove();
                return true;
            } else {
                return false;
            }
        }
        if (startNode.getId() == nodeTouched.getId()) {
            return atLeastOneArcOfLastMoveColour;
        } else {
            for (Edge edge : startNode.getOutgoingEdges()) {
                boolean lastColorJustUsed = atLeastOneArcOfLastMoveColour;
                if (colourToString(edge.getColor()).equalsIgnoreCase(lmc)) {
                    lastColorJustUsed = true;
                } else if (!colourToString(edge.getColor()).equalsIgnoreCase(sc)) {
                    continue;
                }

                if (isWeakMove(edge.getDestination(), nodeTouched, lastColorJustUsed, visitedNodes)) {
                    visitedNodes.put(edge.getDestination(), true);
                    updateValidWeakMove();
                    return true;
                }
            }
            visitedNodes.put(startNode, false);
            return false;
        }
    }

    private void updateValidWeakMove() {
        roomNameRef.child("leftGraph").child("enabled").setValue(String.valueOf(true));
        roomNameRef.child("rightGraph").child("enabled").setValue(String.valueOf(true));
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
            case -13421773:
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

    private void setTextColourSpecial(String sColour) {
        switch (sColour) {
            case "red":
                coloreSpeciale.setTextColor(getResources().getColor(R.color.red));
                break;
            case "green":
                coloreSpeciale.setTextColor(getResources().getColor(R.color.green));
                break;
            case "blue":
                coloreSpeciale.setTextColor(getResources().getColor(R.color.primaryColor));
                break;
            default:
                coloreSpeciale.setTextColor(getResources().getColor(R.color.black));
                break;
        }
        coloreSpeciale.setText(specialColour);
    }

    private void setTextColourLastMove(String lmColour) {
        switch (lmColour) {
            case "red":
                lastMoveColour.setTextColor(getResources().getColor(R.color.red));
                break;
            case "green":
                lastMoveColour.setTextColor(getResources().getColor(R.color.green));
                break;
            case "black":
                lastMoveColour.setTextColor(getResources().getColor(R.color.black));
                break;
            case "blue":
                lastMoveColour.setTextColor(getResources().getColor(R.color.primaryColor));
                break;
            default:
                break;
        }
        lastMoveColour.setText(lmColour);
    }

    private void showDialogTurnOf() {
        String turn = turnoDi.getText().toString().trim();
        if (turn.equalsIgnoreCase(getResources().getString(R.string.table_attacker))) {
            if (playerName.equalsIgnoreCase(attacker.getText().toString().trim())) {
                Toast.makeText(this, getResources().getString(R.string.table_your_turn), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (playerName.equalsIgnoreCase(defender.getText().toString().trim())) {
                Toast.makeText(this, getResources().getString(R.string.table_your_turn), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setTurnOf() {
        getTurnOf(roomsRef.child(roomName), new CallbackTurnOf() {
            @Override
            public void onCallbackTurnOf(String turnOf) {
                if (turnOf != null) {
                    turnoDi.setText(turnOf);
                    showDialogTurnOf();
                }
            }
        });
    }

    private void getTurnOf(DatabaseReference roomNameRef, CallbackTurnOf callback) {
        roomNameRef.child("turnOf").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
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
                    setTextColourLastMove(colour);
                }
            }
        });
    }

    private void getLastMoveColour(DatabaseReference roomNameRef, CallbackLastMoveColour
            callback) {
        roomNameRef.child("lastMoveColour").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = Objects.requireNonNull(snapshot.getValue()).toString();
                    callback.onCallbackLastMoveColour(value);
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
                    if (value.equalsIgnoreCase(String.valueOf(false))) {
                        noMoveBtn.setEnabled(false);
                        noMoveBtn.setClickable(false);
                        noMoveBtn.setFocusable(false);
                        noMoveBtn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    } else {
                        noMoveBtn.setEnabled(true);
                        noMoveBtn.setClickable(true);
                        noMoveBtn.setFocusable(true);
                        noMoveBtn.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
                    }
                }
            }
        });
    }

    private void getNoMove(CallbackNoMove callback) {
        roomNameRef.child("noMove").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    callback.onCallbackNoMove(snapshot.getValue().toString().trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                            selectedNodeLeft.setText(getResources().getString(R.string.node_one));
                        } else {
                            selectedNodeRight.setText(getResources().getString(R.string.node_one));
                        }
                    }
                }
            }

            @Override
            public void onCallbackSelectedNodeTwo(String sNode) {
                if (sNode != null) {
                    if (sNode.equalsIgnoreCase("true")) {
                        if (leftTable) {
                            selectedNodeLeft.setText(getResources().getString(R.string.node_two));
                        } else {
                            selectedNodeRight.setText(getResources().getString(R.string.node_two));
                        }
                    }
                }
            }

            @Override
            public void onCallbackSelectedNodeThree(String sNode) {
                if (sNode != null) {
                    if (sNode.equalsIgnoreCase("true")) {
                        if (leftTable) {
                            selectedNodeLeft.setText(getResources().getString(R.string.node_three));
                        } else {
                            selectedNodeRight.setText(getResources().getString(R.string.node_three));
                        }
                    }
                }
            }

            @Override
            public void onCallbackSelectedNodeFour(String sNode) {
                if (sNode != null) {
                    if (sNode.equalsIgnoreCase("true")) {
                        if (leftTable) {
                            selectedNodeLeft.setText(getResources().getString(R.string.node_four));
                        } else {
                            selectedNodeRight.setText(getResources().getString(R.string.node_four));
                        }
                    }
                }
            }

            @Override
            public void onCallbackSelectedNodeFive(String sNode) {
                if (sNode != null) {
                    if (sNode.equalsIgnoreCase("true")) {
                        if (leftTable) {
                            selectedNodeLeft.setText(getResources().getString(R.string.node_five));
                        } else {
                            selectedNodeRight.setText(getResources().getString(R.string.node_five));
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

    private void endGame() {
        removeReferences();
        updateScoreCount();
    }

    private void updateScoreCount() {
        DatabaseReference usersRef = database.getReference("users");
        String winner;
        if (turnoDi.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.table_attacker))) {
            winner = defender.getText().toString().trim();
        } else {
            winner = attacker.getText().toString().trim();
        }
        if (playerName.equalsIgnoreCase(winner)) {
            usersRef.child(playerName).child("winCount").setValue(ServerValue.increment(1));
        } else {
            usersRef.child(playerName).child("loseCount").setValue(ServerValue.increment(1));
        }
    }

    private void displayEndGameDialog() {
        String winner;
        if (turnoDi.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.table_attacker))) {
            winner = defender.getText().toString().trim();
        } else {
            winner = attacker.getText().toString().trim();
        }
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View v;
        if (playerName.equalsIgnoreCase(winner)) {
            v = getLayoutInflater().inflate(R.layout.end_game_dialog_winner, null, false);
        } else {
            v = getLayoutInflater().inflate(R.layout.end_game_dialog_loser, null, false);
        }
        builder.setView(v);
        builder.setPositiveButton(getResources().getString(R.string.alert_continue), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endGame();
                finish();
            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    private void setEndGame() {
        getGameInProgress(new CallbackGameInProgress() {
            @Override
            public void onCallbackGameInProgress(String value) {
                if (value != null) {
                    if (value.equalsIgnoreCase(String.valueOf(false))) {
                        displayEndGameDialog();
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

    private void enableGraphs() {
        enableLeftGraph();
        enableRightGraph();
    }

    private void enableLeftGraph() {
        getEnabledLeftGraph(new CallbackEnabledLeftGraph() {
            @Override
            public void onCallbackEnabledLeftGraph(String value) {
                if (directedGraphLeft != null) {
                    if (value != null) {
                        if (value.equalsIgnoreCase(String.valueOf(true))) {
                            // enable touch of left graph
                            directedGraphLeft.setEnabled(true);
                            directedGraphLeft.setClickable(true);
                            directedGraphLeft.setFocusable(true);
                        } else {
                            // disable touch of left graph
                            directedGraphLeft.setEnabled(false);
                            directedGraphLeft.setClickable(false);
                            directedGraphLeft.setFocusable(false);
                        }
                    }
                }
            }
        });
    }

    private void getEnabledLeftGraph(CallbackEnabledLeftGraph callback) {
        DatabaseReference reference = roomNameRef.child("leftGraph").child("enabled");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = snapshot.getValue().toString();
                    callback.onCallbackEnabledLeftGraph(value);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void enableRightGraph() {
        getEnabledRightGraph(new CallbackEnabledRightGraph() {
            @Override
            public void onCallbackEnabledRightGraph(String value) {
                if (directedGraphRight != null) {
                    if (value != null) {
                        if (value.equalsIgnoreCase(String.valueOf(true))) {
                            // enable touch of left graph
                            directedGraphRight.setEnabled(true);
                            directedGraphRight.setClickable(true);
                            directedGraphRight.setFocusable(true);
                        } else {
                            // disable touch of left graph
                            directedGraphRight.setEnabled(false);
                            directedGraphRight.setClickable(false);
                            directedGraphRight.setFocusable(false);
                        }
                    }
                }
            }
        });
    }

    private void getEnabledRightGraph(CallbackEnabledRightGraph callback) {
        DatabaseReference reference = roomNameRef.child("rightGraph").child("enabled");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = snapshot.getValue().toString();
                    callback.onCallbackEnabledRightGraph(value);
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
    public void onCallbackGameInProgress(String value) {

    }

    @Override
    public void onCallbackEnabledLeftGraph(String value) {

    }

    @Override
    public void onCallbackEnabledRightGraph(String value) {

    }

    @Override
    public void onCallbackNoMove(String value) {

    }
}