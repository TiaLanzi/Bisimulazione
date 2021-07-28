package com.example.bisimulazione.directedgraph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

import interfaces.CallbackNodeColor;

@SuppressLint("ViewConstructor")
public abstract class DirectedGraph extends View {

    protected static final String TAG = "Bisimulazione";

    private DatabaseReference reference;

    private Edge[] edges;
    private Node[] nodes;

    protected final float stroke = 8f;
    protected final float radius = 40f;

    public DirectedGraph(Context context) {
        super(context);
    }

    public DirectedGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DirectedGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setReference(DatabaseReference reference) {
        this.reference = reference;
    }

    protected DatabaseReference getReference() {
        return this.reference;
    }

    public void setEdges(Edge[] edges) {
        this.edges = edges;
    }

    public Edge[] getEdges() {
        return this.edges;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    public Node[] getNodes() {
        return this.nodes;
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected abstract void drawGraph(Canvas canvas);

    protected abstract void drawNodes(Canvas canvas);

    protected abstract void drawEdges(Canvas canvas);

    protected Paint paintNode(int color) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(stroke);
        paint.setColor(color);
        return paint;
    }

    protected Paint paintLine(int color) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(stroke);
        paint.setColor(color);
        return paint;
    }

    protected Paint paintTriangle(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(stroke);
        paint.setAntiAlias(true);
        return paint;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    protected void getNodeColour(CallbackNodeColor callback) {
        DatabaseReference reference = this.getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (Objects.requireNonNull(dataSnapshot.getKey()).equalsIgnoreCase("Node one")) {
                        String value = Objects.requireNonNull(dataSnapshot.child("Colour").getValue()).toString();
                        callback.onCallbackNodeOneColour(value);
                    } else if (dataSnapshot.getKey().equalsIgnoreCase("Node two")) {
                        String value = Objects.requireNonNull(dataSnapshot.child("Colour").getValue()).toString();
                        callback.onCallbackNodeTwoColour(value);
                    } else if (dataSnapshot.getKey().equalsIgnoreCase("Node three")) {
                        String value = Objects.requireNonNull(dataSnapshot.child("Colour").getValue()).toString();
                        callback.onCallbackNodeThreeColour(value);
                    } else if (dataSnapshot.getKey().equalsIgnoreCase("Node four")) {
                        String value = Objects.requireNonNull(dataSnapshot.child("Colour").getValue()).toString();
                        callback.onCallbackNodeFourColour(value);
                    } else if (dataSnapshot.getKey().equalsIgnoreCase("Node five")) {
                        String value = Objects.requireNonNull(dataSnapshot.child("Colour").getValue()).toString();
                        callback.onCallbackNodeFiveColour(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}