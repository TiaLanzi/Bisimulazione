package com.example.bisimulazione;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Third extends AppCompatActivity {

    private Button button;

    private String playerName = "";
    private String roomName = "";
    private String role = "";
    private String message = "";

    private FirebaseAuth auth;
    private FirebaseUser user;

    private FirebaseDatabase database;
    private DatabaseReference messageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        button = findViewById(R.id.button_third);
        button.setEnabled(false);

        database = FirebaseDatabase.getInstance();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            playerName = user.getDisplayName();
            //Log.i(TAG, "Player name " + user.getDisplayName());
        }


         /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            roomName = extras.getString(playerName);
            if (roomName.equals(playerName)) {
                role = "host";
            } else {
                role = "guest";
            }
        } */

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                message = role + ":Poked!";
                messageRef.setValue(message);
            }
        });

        messageRef = database.getReference("rooms/" + roomName + "/message");
        message = role + ":Poked!";
        messageRef.setValue(message);
        addRoomEventListener();
    }

    private void addRoomEventListener() {
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (role.equals("host")) {
                    if (snapshot.getValue(String.class).contains("guest:")) {
                        button.setEnabled(true);
                        Toast.makeText(Third.this, "" + snapshot.getValue(String.class).replace("guest:", ""), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (snapshot.getValue(String.class).contains("host:")) {
                        button.setEnabled(true);
                        Toast.makeText(Third.this, "" + snapshot.getValue(String.class).replace("host:", ""), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                messageRef.setValue(message);
            }
        });*/
    }
}