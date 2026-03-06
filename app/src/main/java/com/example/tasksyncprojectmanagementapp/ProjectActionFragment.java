package com.example.tasksyncprojectmanagementapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProjectActionFragment extends BottomSheetDialogFragment {

    private EditText etProjectName, etMembers, etProgress;
    private TextView tvTitle;
    private Button btnAction;
    private ImageButton btnDelete;
    private LinearLayout layoutStatus;
    private Spinner spStatus;
    private DatabaseHelper dbHelper;
    private Project projectToEdit;
    private boolean isEdit = false;

    public interface OnProjectActionListener {
        void onProjectActionSuccess();
    }

    private OnProjectActionListener listener;

    public void setOnProjectActionListener(OnProjectActionListener listener) {
        this.listener = listener;
    }

    public static ProjectActionFragment newInstance(Project project) {
        ProjectActionFragment fragment = new ProjectActionFragment();
        fragment.projectToEdit = project;
        fragment.isEdit = (project != null);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        tvTitle = view.findViewById(R.id.tvProjectFragmentTitle);
        etProjectName = view.findViewById(R.id.etProjectName);
        etMembers = view.findViewById(R.id.etMembers);
        etProgress = view.findViewById(R.id.etProgress);
        btnAction = view.findViewById(R.id.btnProjectAction);
        btnDelete = view.findViewById(R.id.btnDeleteProject);
        layoutStatus = view.findViewById(R.id.layoutStatus);
        spStatus = view.findViewById(R.id.spStatus);

        if (isEdit && projectToEdit != null) {
            tvTitle.setText("Edit Project Details");
            tvTitle.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            btnAction.setText("Save Changes");

            btnDelete.setVisibility(View.VISIBLE);
            layoutStatus.setVisibility(View.VISIBLE);

            etProjectName.setText(projectToEdit.getTitle());
            etMembers.setText(String.valueOf(projectToEdit.getMembers()));
            etProgress.setText(String.valueOf(projectToEdit.getProgress()));

            for (int i = 0; i < spStatus.getCount(); i++) {
                if (spStatus.getItemAtPosition(i).toString().equalsIgnoreCase(projectToEdit.getStatus())) {
                    spStatus.setSelection(i);
                    break;
                }
            }

            btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        }

        btnAction.setOnClickListener(v -> {
            String name = etProjectName.getText().toString().trim();
            String membersStr = etMembers.getText().toString().trim();
            String progressStr = etProgress.getText().toString().trim();

            if (name.isEmpty() || membersStr.isEmpty() || progressStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int members = Integer.parseInt(membersStr);
            int progress = Integer.parseInt(progressStr);
            
            SharedPreferences sharedPref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            int userId = sharedPref.getInt("USER_ID", -1);

            if (isEdit) {
                projectToEdit.setTitle(name);
                projectToEdit.setMembers(members);
                projectToEdit.setProgress(progress);
                projectToEdit.setStatus(spStatus.getSelectedItem().toString());
                if (dbHelper.updateProject(projectToEdit)) {
                    Toast.makeText(getContext(), "Project Updated", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onProjectActionSuccess();
                    dismiss();
                }
            } else {
                if (dbHelper.addProject(name, members, "active", progress, userId)) {
                    Toast.makeText(getContext(), "Project Created", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onProjectActionSuccess();
                    dismiss();
                }
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Project")
                .setMessage("Are you sure you want to delete this project?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (dbHelper.deleteProject(projectToEdit.getId())) {
                        if (listener != null) listener.onProjectActionSuccess();
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
