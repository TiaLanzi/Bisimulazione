package com.example.bisimulazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Bisimulazione";

    private AppBarConfiguration appBarConfiguration;

    private Toolbar toolbar;

    private String playerName;
    private DatabaseReference playersRef;
    private DatabaseReference playerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize views about navigation drawer and header
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_view).setDrawerLayout(drawerLayout).build();*/

        Button playGame = findViewById(R.id.main_play_button);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            playerName = user.getDisplayName();
            // Log.i(TAG, "Player name " + user.getDisplayName());
        }
        if (playerName != null) {
            if (!playerName.equals("")) {
                playersRef = database.getReference().child("players");
            }
        }

        playGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData(playersRef, playerName);
                Intent intent = new Intent(MainActivity.this, MatchmakingRoom.class);
                startActivity(intent);
            }
        });
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        //NavigationUI.setupActionBarWithNavController(navigationView, navController);

    }

    private void setSupportActionBar(Toolbar toolbar) {
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.navigation_bug_report:
                Intent intentBugReport = new Intent(this, BugReport.class);
                startActivity(intentBugReport);
                break;
            case R.id.navigation_faq:
                Intent intentFaq = new Intent(this, FAQ.class);
                startActivity(intentFaq);
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawers();
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void navigateTo(Fragment fragment) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStackImmediate();
        }
        fragmentTransaction.replace(R.id.nav_host_fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void sendData(DatabaseReference playersRef, String playerName) {
        //Log.i(TAG, "Siamo qui arrivati");
        playerRef = playersRef.child(playerName);
        playerRef.setValue(playerName);
    }
}