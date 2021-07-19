package com.example.bisimulazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import info.androidhive.fontawesome.FontDrawable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.app_bar_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_host_fragment).setDrawerLayout(drawerLayout).build();

        NavController navController = Navigation.findNavController(this, R.id.navigation_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        initDrawerLayout();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.navigation_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.menu_home));
                navigateTo(new Home());
                break;
            case R.id.navigation_my_account:
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.my_account));
                Intent intentMyAccount = new Intent(this, MyAccount.class);
                startActivity(intentMyAccount);
                break;
            case R.id.navigation_game:
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.menu_play_game));
                Intent intentGame = new Intent(this, MatchmakingRoom.class);
                startActivity(intentGame);
                break;
            case R.id.navigation_faq:
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.menu_faq));
                Intent intentFaq = new Intent(this, FAQ.class);
                startActivity(intentFaq);
                break;
            case R.id.navigation_settings:
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.menu_settings));
                Intent intentSettings = new Intent(this, Settings.class);
                startActivity(intentSettings);
                break;
            case R.id.navigation_bug_report:
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.menu_bug_report));
                Intent intentBugReport = new Intent(this, BugReport.class);
                startActivity(intentBugReport);
                break;
            default:
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawers();
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void initDrawerLayout() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        int[] iconsNavigation = {R.string.fa_home_solid, R.string.fa_user_solid, R.string.fa_play_solid};
        renderMenuIcons(navigationView.getMenu().getItem(0).getSubMenu(), iconsNavigation, true, false);

        int[] iconsOther = {R.string.fa_question_circle_solid, R.string.fa_cog_solid, R.string.fa_bug_solid};
        renderMenuIcons(navigationView.getMenu().getItem(1).getSubMenu(), iconsOther, true, false);
    }

    private void renderMenuIcons(Menu menu, int[] icons, boolean isSolid, boolean isBrand) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (!menuItem.hasSubMenu()) {
                FontDrawable fontDrawable = new FontDrawable(this, icons[i], isSolid, isBrand);
                fontDrawable.setTextColor(ContextCompat.getColor(this, R.color.black));
                fontDrawable.setTextSize(20);
                menu.getItem(i).setIcon(fontDrawable);
            }
        }
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
}