package com.example.tasksyncprojectmanagementapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword;
    private ImageButton btnToggleCurrent, btnToggleNew;
    private Button btnDone;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("USER_ID", -1);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnToggleCurrent = findViewById(R.id.btnToggleCurrent);
        btnToggleNew = findViewById(R.id.btnToggleNew);
        btnDone = findViewById(R.id.btnDone);

        setupPasswordToggle(etCurrentPassword, btnToggleCurrent);
        setupPasswordToggle(etNewPassword, btnToggleNew);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnDone.setOnClickListener(v -> {
            String currentPass = etCurrentPassword.getText().toString();
            String newPass = etNewPassword.getText().toString();

            if (currentPass.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dbHelper.checkPassword(userId, currentPass)) {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 8) {
                Toast.makeText(this, "New password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.updateUserPassword(userId, newPass)) {
                Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPasswordToggle(EditText editText, ImageButton toggleButton) {
        toggleButton.setOnClickListener(v -> {
            if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleButton.setImageResource(R.drawable.ic_eye);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleButton.setImageResource(R.drawable.ic_eye_off);
            }
            editText.setSelection(editText.length());
        });
    }
}
