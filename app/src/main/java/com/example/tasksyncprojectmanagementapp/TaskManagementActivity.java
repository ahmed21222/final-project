package com.example.tasksyncprojectmanagementapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskManagementActivity extends AppCompatActivity implements TaskActionFragment.OnTaskActionListener {

    private ListView lvTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseHelper dbHelper;
    private int userId;
    private LinearLayout navDashboard, navCalendar;
    private EditText etSearchTasks;
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        dbHelper = new DatabaseHelper(this);
        
        SharedPreferences sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("USER_ID", -1);

        lvTasks = findViewById(R.id.lvTasks);
        navDashboard = findViewById(R.id.navDashboard);
        navCalendar = findViewById(R.id.navCalendar);
        etSearchTasks = findViewById(R.id.etSearchTasks);
        ivProfile = findViewById(R.id.ivProfile); // تم الإصلاح هنا
        
        refreshTaskList();

        ivProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        etSearchTasks.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        lvTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            Task selectedTask = (Task) parent.getItemAtPosition(position);
            if (selectedTask != null) showDeleteConfirmDialog(selectedTask);
            return true;
        });

        lvTasks.setOnItemClickListener((parent, view, position, id) -> {
            Task selectedTask = (Task) parent.getItemAtPosition(position);
            if (selectedTask != null) {
                TaskActionFragment fragment = TaskActionFragment.newInstance(selectedTask);
                fragment.setOnTaskActionListener(this);
                fragment.show(getSupportFragmentManager(), "edit_task");
            }
        });

        navDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProjectListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        navCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnAddTasks).setOnClickListener(v -> {
            startActivity(new Intent(this, AddTaskActivity.class));
        });
    }

    private void filterTasks(String query) {
        if (taskList == null) return;
        
        String q = query.toLowerCase().trim();
        List<Task> filtered = new ArrayList<>();
        for (Task t : taskList) {
            if (t.getTitle().toLowerCase().contains(q) || t.getProjectTitle().toLowerCase().contains(q)) {
                filtered.add(t);
            }
        }
        taskAdapter = new TaskAdapter(this, filtered);
        lvTasks.setAdapter(taskAdapter);
    }

    private void showDeleteConfirmDialog(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> {
                    if (dbHelper.deleteTask(task.getId())) {
                        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
                        refreshTaskList();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void refreshTaskList() {
        if (userId != -1) {
            taskList = dbHelper.getUserTasks(userId);
            taskAdapter = new TaskAdapter(this, taskList);
            lvTasks.setAdapter(taskAdapter);
        } else {
            finish();
        }
    }

    @Override
    public void onTaskActionSuccess() {
        refreshTaskList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTaskList();
    }
}
