package com.example.bisimulazione;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.bisimulazione.directedgraph.DirectedGraph;
import com.example.bisimulazione.directedgraph.Edge;
import com.example.bisimulazione.directedgraph.Node;

public class Table extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

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
        tableRightDirectedGraphLayout.addView(directedGraphRight);
    }
}