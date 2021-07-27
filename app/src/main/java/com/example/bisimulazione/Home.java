package com.example.bisimulazione;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import interfaces.CallbackActivePlayers;
import interfaces.CallbackGameCount;

public class Home extends Fragment implements CallbackGameCount, CallbackActivePlayers {

    private static final String TAG = "Bisimulazione";

    private String playerName;

    private FirebaseUser user;

    private DatabaseReference playersRef;

    private TextView win;
    private TextView lose;

    private TextView aPlayers;
    private TextView roomsCount;

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
        MaterialCardView secondCard = view.findViewById(R.id.second_card);

        Button playGame = view.findViewById(R.id.home_play_button);
        Button activePlayers = view.findViewById(R.id.home_active_players_button);
        Button moreAbout = firstCard.findViewById(R.id.home_first_card_more_about);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.home_floating_action_button);

        win = firstCard.findViewById(R.id.home_first_card_win_count);
        lose = firstCard.findViewById(R.id.home_first_card_lose_count);

        aPlayers = secondCard.findViewById(R.id.home_second_card_active_players_count);
        roomsCount = secondCard.findViewById(R.id.home_second_card_rooms_count);

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

        setActivePlayers();
        //setRoomsCount();

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

        floatingActionButton.setOnClickListener(v -> showDialog());
    }

    private void setActivePlayers() {
        getActivePlayers(new CallbackActivePlayers() {
            @Override
            public void onCallbackActivePlayers(int counter) {
                aPlayers.setText(String.valueOf(counter));
            }
        });
    }

    private void getActivePlayers(CallbackActivePlayers callback) {
        FirebaseDatabase.getInstance().getReference().child("players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        counter++;
                    }
                }
                Log.i(TAG, "Counter " + counter);
                callback.onCallbackActivePlayers(counter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        View v = getLayoutInflater().inflate(R.layout.share_dialog, null, false);
        builder.setView(v);
        builder.setNegativeButton(getString(R.string.home_share), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("text/plain");
                intentShare.putExtra(Intent.EXTRA_TEXT, getString(R.string.home_share_message));
                startActivity(Intent.createChooser(intentShare, getString(R.string.home_share_with_your_friends)));
            }
        });

        builder.setPositiveButton(getString(R.string.home_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    @Override
    public void onCallbackActivePlayers(int counter) {

    }
}