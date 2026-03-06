package com.example.tasksyncprojectmanagementapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ProjectListActivity extends AppCompatActivity implements ProjectActionFragment.OnProjectActionListener {

    private ListView lvProjects;
    private Button btnCreateProject, btnFilters;
    private EditText etSearch;
    private ImageButton btnProfile;
    private LinearLayout navTasks, navCalendar;

    private ProjectAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Project> currentProjects;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        dbHelper = new DatabaseHelper(this);
        
        SharedPreferences sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("USER_ID", -1);

        lvProjects = findViewById(R.id.lvProjects);
        btnCreateProject = findViewById(R.id.btnCreateProject);
        btnFilters = findViewById(R.id.btnFilters);
        etSearch = findViewById(R.id.etSearch);
        btnProfile = findViewById(R.id.btnProfile);
        
        navTasks = findViewById(R.id.navTasks);
        navCalendar = findViewById(R.id.navCalendar);

        refreshProjectList();

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectListActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnCreateProject.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectListActivity.this, AddProjectActivity.class);
            startActivity(intent);
        });

        lvProjects.setOnItemClickListener((parent, view, position, id) -> {
            Project selectedProject = currentProjects.get(position);
            ProjectActionFragment fragment = ProjectActionFragment.newInstance(selectedProject);
            fragment.setOnProjectActionListener(this);
            fragment.show(getSupportFragmentManager(), "edit_project");
        });

        lvProjects.setOnItemLongClickListener((parent, view, position, id) -> {
            Project selectedProject = currentProjects.get(position);
            showDeleteConfirmDialog(selectedProject);
            return true;
        });

        navTasks.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectListActivity.this, TaskManagementActivity.class);
            startActivity(intent);
        });

        navCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectListActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        btnFilters.setOnClickListener(v -> Toast.makeText(this, "Filters later", Toast.LENGTH_SHORT).show());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void showDeleteConfirmDialog(Project project) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Project")
                .setMessage("Are you sure you want to delete '" + project.getTitle() + "' and all its tasks?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> {
                    if (dbHelper.deleteProject(project.getId())) {
                        Toast.makeText(this, "Project deleted", Toast.LENGTH_SHORT).show();
                        refreshProjectList();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void refreshProjectList() {
        if (userId != -1) {
            currentProjects = dbHelper.getUserProjects(userId);
            adapter = new ProjectAdapter(this, currentProjects);
            lvProjects.setAdapter(adapter);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void filter(String q) {
        if (currentProjects == null) return;
        q = q.toLowerCase().trim();
        List<Project> filtered = new ArrayList<>();
        for (Project p : currentProjects) {
            if (p.getTitle().toLowerCase().contains(q)) filtered.add(p);
        }
        adapter = new ProjectAdapter(this, filtered);
        lvProjects.setAdapter(adapter);
    }

    @Override
    public void onProjectActionSuccess() {
        refreshProjectList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshProjectList();
    }
}
