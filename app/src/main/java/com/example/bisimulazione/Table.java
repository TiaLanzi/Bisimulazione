package com.example.bisimulazione;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.bisimulazione.directedgraph.DirectedGraph;
import com.example.bisimulazione.directedgraph.Node;

public class Table extends AppCompatActivity {

    private final String TAG = "Bisimulazione";

    private DirectedGraph directedGraphLeft;
    private LinearLayout tableLeftDirectedGraphLayout;
    private DirectedGraph directedGraphRight;
    private LinearLayout tableRightDirectedGraphLayout;
    private int x;
    private int y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        // initialize layout
        tableLeftDirectedGraphLayout = findViewById(R.id.table_left_directed_graph_layout);

        // initialize root node
        Node root = new Node(320, 96, true, true);
        // initialize second node
        Node second = new Node(root.getX(), root.getY(), true, false, true);
        // initialize third node
        Node third = new Node(root.getX(), root.getY(), false, true, false);

        //initialize DirectedGraph
        directedGraphLeft = new DirectedGraph(this, root, second);
        //directedGraphLeft.addPath(root, third);
        directedGraphLeft.setBackgroundColor(getResources().getColor(R.color.red));

        /*directedGraphRight = new DirectedGraph(this);
        directedGraphRight.setBackgroundColor(getResources().getColor(R.color.background_color));*/

        tableLeftDirectedGraphLayout.addView(directedGraphLeft);
        //tableRightDirectedGraphLayout.addView(directedGraphRight);
    }
}