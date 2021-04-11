package com.teamhustle.alertsos;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ReceiveNotificationActivity extends AppCompatActivity {
    Context c;
    TextView uname, ulocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_notification);
        getSupportActionBar().setTitle("Notification");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uname= findViewById(R.id.name);
        ulocation = findViewById(R.id.location);

        if (getIntent().hasExtra("time")){
            String name = getIntent().getStringExtra("time");
            String location = getIntent().getStringExtra("link");
            uname.setText(name);
            ulocation.setText(location);
            ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setText(location);
            Toast.makeText(getApplicationContext(), "Location copied to Clipboard", Toast.LENGTH_SHORT).show();
        }

        ulocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = getIntent().getStringExtra("link");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(location));
                startActivity(browserIntent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){

            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

}