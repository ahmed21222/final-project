package com.example.tasksyncprojectmanagementapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private List<Task> tasks;

    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public int getCount() { return tasks.size(); }

    @Override
    public Object getItem(int position) { return tasks.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_task, parent, false);
        }

        Task task = tasks.get(position);

        TextView tvProjectTitle = convertView.findViewById(R.id.tvProjectTitle);
        TextView tvTaskTitle = convertView.findViewById(R.id.tvTaskTitle);
        TextView tvPriority = convertView.findViewById(R.id.tvPriority);
        TextView tvSquad = convertView.findViewById(R.id.tvSquad);
        TextView tvStatus = convertView.findViewById(R.id.tvStatus);
        TextView tvDueDate = convertView.findViewById(R.id.tvDueDate);
        TextView tvAssignee = convertView.findViewById(R.id.tvAssignee);
        View priorityIndicator = convertView.findViewById(R.id.priorityIndicator);

        tvProjectTitle.setText(task.getProjectTitle());
        tvTaskTitle.setText(task.getTitle());
        tvPriority.setText(task.getPriority());
        tvSquad.setText(task.getSquad());
        tvStatus.setText(task.getStatus());
        tvDueDate.setText(task.getDueDate());
        tvAssignee.setText(task.getAssignee());

        String priority = task.getPriority();
        if ("High".equalsIgnoreCase(priority)) {
            tvPriority.setBackgroundResource(R.drawable.bg_priority_high);
            priorityIndicator.setBackgroundColor(Color.parseColor("#E11D48")); // أحمر زاهي
        } else if ("Medium".equalsIgnoreCase(priority)) {
            tvPriority.setBackgroundResource(R.drawable.bg_priority_medium);
            priorityIndicator.setBackgroundColor(Color.parseColor("#F59E0B")); // برتقالي
        } else {
            tvPriority.setBackgroundResource(R.drawable.bg_priority_low);
            priorityIndicator.setBackgroundColor(Color.parseColor("#10B981")); // أخضر
        }

        return convertView;
    }
}
