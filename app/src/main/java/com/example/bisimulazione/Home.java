package com.example.bisimulazione;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class Home extends Fragment {

    private String playerName;

    private DatabaseReference playersRef;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button playGame = view.findViewById(R.id.main_play_button);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            playerName = user.getDisplayName();
        }
        if (playerName != null) {
            if (!playerName.equals("")) {
                playersRef = database.getReference().child("players");
            }
        }

        playGame.setOnClickListener(v -> {
            sendData(playersRef, playerName);
            Intent intent = new Intent(getActivity(), MatchmakingRoom.class);
            startActivity(intent);
        });
    }

    private void sendData(DatabaseReference playersRef, String playerName) {
        DatabaseReference playerRef = playersRef.child(playerName);
        playerRef.setValue(playerName);
    }
}