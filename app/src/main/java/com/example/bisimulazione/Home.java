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
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import interfaces.CallbackGameCount;

public class Home extends Fragment implements CallbackGameCount {

    private String playerName;

    private FirebaseUser user;

    private DatabaseReference playersRef;

    private TextView win;
    private TextView lose;

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

        MaterialCardView firstCard = view.findViewById(R.id.first_card);

        Button playGame = view.findViewById(R.id.home_play_button);
        Button activePlayers = view.findViewById(R.id.home_active_players_button);
        Button moreAbout = firstCard.findViewById(R.id.home_first_card_more_about);

        win = firstCard.findViewById(R.id.home_first_card_win_count);
        lose = firstCard.findViewById(R.id.home_first_card_lose_count);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            playerName = user.getDisplayName();
        }
        if (playerName != null) {
            if (!playerName.equals("")) {
                playersRef = database.getReference().child("players");
            }
        }

        setGameCount();

        playGame.setOnClickListener(v -> {
            sendData(playersRef, playerName);
            Intent intent = new Intent(getActivity(), MatchmakingRoom.class);
            startActivity(intent);
        });

        activePlayers.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ActivePlayers.class);
            startActivity(intent);
        });

        moreAbout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyAccount.class);
            startActivity(intent);
        });
    }

    private void sendData(DatabaseReference playersRef, String playerName) {
        DatabaseReference playerRef = playersRef.child(playerName);
        playerRef.setValue(playerName);
    }

    private void getGameCount(CallbackGameCount callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String fullName = Objects.requireNonNull(user.getDisplayName()).replace(",", "");
                    if (Objects.requireNonNull(dataSnapshot.getKey()).equalsIgnoreCase(fullName)) {
                        String win = Objects.requireNonNull(dataSnapshot.child("winCount").getValue()).toString();
                        callback.onCallbackWinCount(win);
                        String lose = Objects.requireNonNull(dataSnapshot.child("loseCount").getValue()).toString();
                        callback.onCallbackLoseCount(lose);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setGameCount() {
        getGameCount(new CallbackGameCount() {
            @Override
            public void onCallbackWinCount(String value) {
                if (value != null) {
                    win.setText(value);
                }
            }

            @Override
            public void onCallbackLoseCount(String value) {
                if (value != null) {
                    lose.setText(value);
                }
            }
        });
    }

    @Override
    public void onCallbackWinCount(String value) {

    }

    @Override
    public void onCallbackLoseCount(String value) {

    }
}