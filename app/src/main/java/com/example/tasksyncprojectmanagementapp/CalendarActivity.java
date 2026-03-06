package com.example.tasksyncprojectmanagementapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ListView lvCalendarTasks;
    private TextView tvCurrentMonth, tvUpcomingTitle;
    private DatabaseHelper dbHelper;
    private int userId;
    private List<Task> allUserTasks;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("USER_ID", -1);

        calendarView = findViewById(R.id.calendarView);
        lvCalendarTasks = findViewById(R.id.lvCalendarTasks);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        tvUpcomingTitle = findViewById(R.id.tvUpcomingTitle);

        Calendar cal = Calendar.getInstance();
        updateMonthText(cal);

        refreshTasks();

        showTasksForDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth);
            updateMonthText(selectedCal);
            showTasksForDate(year, month, dayOfMonth);
        });

        setupNavigation();
    }

    private void refreshTasks() {
        if (userId != -1) {
            allUserTasks = dbHelper.getUserTasks(userId);
        } else {
            allUserTasks = new ArrayList<>();
        }
    }

    private void updateMonthText(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvCurrentMonth.setText(sdf.format(cal.getTime()));
    }

    private void showTasksForDate(int year, int month, int day) {
        String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day);
        List<Task> filteredTasks = new ArrayList<>();
        
        if (allUserTasks != null) {
            for (Task task : allUserTasks) {
                String taskDate = task.getDueDate().trim();
                if (taskDate.equals(selectedDate)) {
                    filteredTasks.add(task);
                }
            }
        }

        if (filteredTasks.isEmpty()) {
            tvUpcomingTitle.setText("No tasks for " + selectedDate);
        } else {
            tvUpcomingTitle.setText("You have " + filteredTasks.size() + " tasks on " + selectedDate);
        }

        taskAdapter = new TaskAdapter(this, filteredTasks);
        lvCalendarTasks.setAdapter(taskAdapter);
    }

    private void setupNavigation() {
        findViewById(R.id.btnProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            startActivity(new Intent(this, ProjectListActivity.class));
            finish();
        });
        findViewById(R.id.navTasks).setOnClickListener(v -> {
            startActivity(new Intent(this, TaskManagementActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTasks();
    }
}
