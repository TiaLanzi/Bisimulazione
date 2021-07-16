package com.example.bisimulazione;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Locale;
import java.util.Objects;

public class Settings extends AppCompatActivity {

    private boolean initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String[] LANGUAGES = {getString(R.string.language_select), getString(R.string.language_italian), getString(R.string.language_english)};
        Spinner spinner = findViewById(R.id.settings_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, LANGUAGES);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!initialized) {
                    initialized = true;
                    return;
                }
                String language = (String) parentView.getItemAtPosition(position);
                if (language.equalsIgnoreCase(getString(R.string.language_english))) {
                    changeLanguage("en");
                } else {
                    changeLanguage("it");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void changeLanguage(String language) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        Intent changeLanguage = new Intent(getApplicationContext(), MainActivity.class);
        changeLanguage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(changeLanguage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }
}