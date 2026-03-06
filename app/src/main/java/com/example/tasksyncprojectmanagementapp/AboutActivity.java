package com.example.tasksyncprojectmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnContactSupport = findViewById(R.id.btnContactSupport);

        btnBack.setOnClickListener(v -> finish());

        btnContactSupport.setOnClickListener(v -> {
            startActivity(new Intent(this, ContactSupportActivity.class));
        });
    }
}
