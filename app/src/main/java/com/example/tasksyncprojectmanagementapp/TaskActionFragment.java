package com.example.tasksyncprojectmanagementapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskActionFragment extends BottomSheetDialogFragment {

    private EditText etTaskTitle, etDueDate, etAssignee;
    private Spinner spPriority, spProjectTitle;
    private TextView tvFragmentTitle;
    private Button btnAction;
    private ImageButton btnDelete;
    private DatabaseHelper dbHelper;
    private Task taskToEdit;
    private boolean isEdit = false;
    private int userId;

    public interface OnTaskActionListener {
        void onTaskActionSuccess();
    }

    private OnTaskActionListener listener;

    public void setOnTaskActionListener(OnTaskActionListener listener) {
        this.listener = listener;
    }

    public static TaskActionFragment newInstance(Task task) {
        TaskActionFragment fragment = new TaskActionFragment();
        fragment.taskToEdit = task;
        fragment.isEdit = (task != null);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        SharedPreferences sharedPref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("USER_ID", -1);

        tvFragmentTitle = view.findViewById(R.id.tvFragmentTitle);
        etTaskTitle = view.findViewById(R.id.etTaskTitle);
        spProjectTitle = view.findViewById(R.id.spProjectTitle);
        etDueDate = view.findViewById(R.id.etDueDate);
        etAssignee = view.findViewById(R.id.etAssignee);
        spPriority = view.findViewById(R.id.spPriority);
        btnAction = view.findViewById(R.id.btnAction);
        btnDelete = view.findViewById(R.id.btnDeleteTask);

        setupProjectSpinner();

        etDueDate.setOnClickListener(v -> showDatePicker());

        if (isEdit && taskToEdit != null) {
            tvFragmentTitle.setText("Edit Task Details");
            tvFragmentTitle.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            btnAction.setText("Save Changes");
            btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
            
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v -> showDeleteConfirmation());

            etTaskTitle.setText(taskToEdit.getTitle());
            etDueDate.setText(taskToEdit.getDueDate());
            etAssignee.setText(taskToEdit.getAssignee());
            
            for (int i = 0; i < spProjectTitle.getCount(); i++) {
                if (spProjectTitle.getItemAtPosition(i).toString().equalsIgnoreCase(taskToEdit.getProjectTitle())) {
                    spProjectTitle.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < spPriority.getCount(); i++) {
                if (spPriority.getItemAtPosition(i).toString().equalsIgnoreCase(taskToEdit.getPriority())) {
                    spPriority.setSelection(i);
                    break;
                }
            }
        }

        btnAction.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            if (spProjectTitle.getSelectedItem() == null || spProjectTitle.getSelectedItem().toString().equals("No projects available")) {
                Toast.makeText(getContext(), "Please create a project first", Toast.LENGTH_SHORT).show();
                return;
            }
            String pTitle = spProjectTitle.getSelectedItem().toString();
            String date = etDueDate.getText().toString().trim();
            String priority = spPriority.getSelectedItem().toString();
            String assignee = etAssignee.getText().toString().trim();

            if (title.isEmpty() || date.isEmpty() || assignee.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEdit) {
                taskToEdit.setTitle(title);
                taskToEdit.setProjectTitle(pTitle);
                taskToEdit.setDueDate(date);
                taskToEdit.setPriority(priority);
                taskToEdit.setAssignee(assignee);
                if (dbHelper.updateTask(taskToEdit)) {
                    Toast.makeText(getContext(), "Task Updated", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onTaskActionSuccess();
                    dismiss();
                }
            } else {
                Task newTask = new Task(title, pTitle, "Squad", "Pending", date, priority, assignee);
                if (dbHelper.addTask(newTask, userId)) {
                    Toast.makeText(getContext(), "Task Created", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onTaskActionSuccess();
                    dismiss();
                }
            }
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    etDueDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (dbHelper.deleteTask(taskToEdit.getId())) {
                        Toast.makeText(getContext(), "Task Deleted", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onTaskActionSuccess();
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupProjectSpinner() {
        List<Project> projects = dbHelper.getUserProjects(userId);
        List<String> projectNames = new ArrayList<>();
        for (Project p : projects) {
            projectNames.add(p.getTitle());
        }
        if (projectNames.isEmpty()) {
            projectNames.add("No projects available");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, projectNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProjectTitle.setAdapter(adapter);
    }
}
