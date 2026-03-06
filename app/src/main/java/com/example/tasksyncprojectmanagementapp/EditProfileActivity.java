package com.example.tasksyncprojectmanagementapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etUsername, etEmail;
    private TextView tvProfileName, tvProfileUsernameTop;
    private Button btnSave;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DatabaseHelper(this);
        
        SharedPreferences sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("USER_ID", -1);

        // Bind Views
        btnBack = findViewById(R.id.btnBack);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileUsernameTop = findViewById(R.id.tvProfileUsernameTop);
        etFullName = findViewById(R.id.etProfileFullName);
        etPhone = findViewById(R.id.etProfilePhone);
        etEmail = findViewById(R.id.etProfileEmail);
        etUsername = findViewById(R.id.etProfileUsername);
        btnSave = findViewById(R.id.btnProfileSave);

        loadUserData();

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String username = etUsername.getText().toString().trim();

            if (name.isEmpty() || username.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Name, Email and Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.updateUserProfile(userId, name, email, phone, username)) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                tvProfileName.setText(name);
                tvProfileUsernameTop.setText("@" + username);
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        User user = dbHelper.getUserDetails(userId);
        if (user != null) {
            tvProfileName.setText(user.getFullName());
            tvProfileUsernameTop.setText("@" + (user.getUsername() != null ? user.getUsername() : "user"));
            etFullName.setText(user.getFullName());
            etPhone.setText(user.getPhone());
            etEmail.setText(user.getEmail());
            etUsername.setText(user.getUsername());
        }
    }
}
