package com.example.tasksyncprojectmanagementapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddProjectActivity extends AppCompatActivity {

    private EditText etProjectName, etMembers, etProgress;
    private Button btnSaveProject;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        dbHelper = new DatabaseHelper(this);

        etProjectName = findViewById(R.id.etProjectName);
        etMembers = findViewById(R.id.etMembers);
        etProgress = findViewById(R.id.etProgress);
        btnSaveProject = findViewById(R.id.btnSaveProject);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnSaveProject.setOnClickListener(v -> {
            String name = etProjectName.getText().toString().trim();
            String membersStr = etMembers.getText().toString().trim();
            String progressStr = etProgress.getText().toString().trim();

            if (name.isEmpty() || membersStr.isEmpty() || progressStr.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int members = Integer.parseInt(membersStr);
            int progress = Integer.parseInt(progressStr);
            if (progress < 0) progress = 0;
            if (progress > 100) progress = 100;

            SharedPreferences sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            int userId = sharedPref.getInt("USER_ID", -1);

            if (userId != -1) {
                boolean success = dbHelper.addProject(name, members, "active", progress, userId);
                if (success) {
                    Toast.makeText(this, "Project created!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to create project", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User session error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
