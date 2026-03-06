package com.example.tasksyncprojectmanagementapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ProjectAdapter extends ArrayAdapter<Project> {

    public ProjectAdapter(Context context, List<Project> projects) {
        super(context, 0, projects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Project project = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.row_project, parent, false);
        }

        View viewStatus = convertView.findViewById(R.id.viewStatus);
        TextView title = convertView.findViewById(R.id.tvProjectTitle);
        TextView meta = convertView.findViewById(R.id.tvMeta);
        TextView progressText = convertView.findViewById(R.id.tvProgressPercent);
        ProgressBar progressBar = convertView.findViewById(R.id.progressBar);

        if (project != null) {
            title.setText(project.getTitle());
            meta.setText(project.getMembers() + " Members   •   " + project.getStatus());

            progressText.setText(project.getProgress() + "%");
            progressBar.setProgress(project.getProgress());

            int green = 0xFF2E7D32;
            int orange = 0xFFD84315;

            if ("at-risk".equalsIgnoreCase(project.getStatus())) {
                viewStatus.setBackgroundColor(orange);
                progressBar.setProgressTintList(ColorStateList.valueOf(orange));
            } else {
                viewStatus.setBackgroundColor(green);
                progressBar.setProgressTintList(ColorStateList.valueOf(green));
            }
        }

        return convertView;
    }
}
