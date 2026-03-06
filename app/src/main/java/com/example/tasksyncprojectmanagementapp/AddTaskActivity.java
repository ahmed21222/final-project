package com.example.tasksyncprojectmanagementapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTaskTitle, etDueDate, etAssignee;
    private Spinner spPriority, spProjectTitle;
    private Button btnSaveTask;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        dbHelper = new DatabaseHelper(this);
        
        SharedPreferences sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("USER_ID", -1);

        etTaskTitle = findViewById(R.id.etTaskTitle);
        spProjectTitle = findViewById(R.id.spProjectTitle);
        etDueDate = findViewById(R.id.etDueDate);
        etAssignee = findViewById(R.id.etAssignee);
        spPriority = findViewById(R.id.spPriority);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        btnBack = findViewById(R.id.btnBack);

        setupProjectSpinner();

        btnBack.setOnClickListener(v -> finish());
        etDueDate.setOnClickListener(v -> showDatePicker());

        btnSaveTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            if (spProjectTitle.getSelectedItem() == null || spProjectTitle.getSelectedItem().toString().equals("No projects available")) {
                Toast.makeText(this, "Please create a project first", Toast.LENGTH_SHORT).show();
                return;
            }
            String pTitle = spProjectTitle.getSelectedItem().toString();
            String date = etDueDate.getText().toString().trim();
            String priority = spPriority.getSelectedItem().toString();
            String assignee = etAssignee.getText().toString().trim();

            if (title.isEmpty() || date.isEmpty() || assignee.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userId != -1) {
                Task newTask = new Task(title, pTitle, "Squad", "Pending", date, priority, assignee);
                if (dbHelper.addTask(newTask, userId)) {
                    Toast.makeText(this, "Task created!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    etDueDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void setupProjectSpinner() {
        List<Project> projects = dbHelper.getUserProjects(userId);
        List<String> projectNames = new ArrayList<>();
        for (Project p : projects) {
            projectNames.add(p.getTitle());
        }
        if (projectNames.isEmpty()) projectNames.add("No projects available");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projectNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProjectTitle.setAdapter(adapter);
    }
}
