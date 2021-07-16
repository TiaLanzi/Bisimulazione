package com.example.bisimulazione;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Objects;

import info.androidhive.fontawesome.FontTextView;

public class FAQ extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.faq));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final boolean[] VISIBILITIES = {false, false, false, false};

        int[] headers = {R.id.faq_header_one, R.id.faq_header_two, R.id.faq_header_three, R.id.faq_header_four};

        int[] answers = {R.id.faq_answer_one, R.id.faq_answer_two, R.id.faq_answer_three, R.id.faq_answer_four};

        int[] toggles = {R.id.faq_toggle_one, R.id.faq_toggle_two, R.id.faq_toggle_three, R.id.faq_toggle_four};

        for (int i = 0; i < answers.length; i++) {
            final TextView answer = findViewById(answers[i]);
            final RelativeLayout header = findViewById(headers[i]);
            final FontTextView toggle = findViewById(toggles[i]);

            final int index = i;

            header.setOnClickListener(v -> {
                if (VISIBILITIES[index]) {
                    answer.setVisibility(View.GONE);
                    toggle.setText(R.string.fa_caret_down_solid);
                } else {
                    answer.setVisibility(View.VISIBLE);
                    toggle.setText(R.string.fa_caret_up_solid);
                }
                VISIBILITIES[index] = !VISIBILITIES[index];
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }
}