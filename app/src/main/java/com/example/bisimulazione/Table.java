package com.example.bisimulazione;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.bisimulazione.directedgraph.DirectedGraph;
import com.example.bisimulazione.directedgraph.Edge;
import com.example.bisimulazione.directedgraph.Node;

public class Table extends AppCompatActivity {

    private final String TAG = "Bisimulazione";

    private DirectedGraph directedGraphRight;
    private LinearLayout tableRightDirectedGraphLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

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
    }
}