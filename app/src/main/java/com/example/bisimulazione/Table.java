package com.example.bisimulazione;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bisimulazione.algorithmvisualizer.Digraph;
import com.example.bisimulazione.algorithmvisualizer.DirectedGraphVisualizer;

public class Table extends AppCompatActivity {

    private Digraph digraph;
    private DirectedGraphVisualizer directedGraphVisualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        digraph = new Digraph();
        digraph.add(1);

        directedGraphVisualizer = new DirectedGraphVisualizer(this.getApplicationContext());
        directedGraphVisualizer.setData(digraph);
        directedGraphVisualizer.drawGraph();
    }
}