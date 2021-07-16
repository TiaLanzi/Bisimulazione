package com.example.bisimulazione;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

public class BugReport extends AppCompatActivity {

    private EditText inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);

        // set header with title
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.bug_report));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputText = findViewById(R.id.bug_report_body_mail);

        Button send = findViewById(R.id.bug_report_send_button);
        send.setOnClickListener(v -> {
            Editable body = inputText.getText();
            if (body.length() != 0) {
                sendEmail(body);
                inputText.setText("");
            }
        });
    }

    private void sendEmail(Editable editable) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String recipient = "prova@example.it";
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bug_report));
        intent.putExtra(Intent.EXTRA_TEXT, editable);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
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