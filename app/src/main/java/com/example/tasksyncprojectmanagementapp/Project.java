package com.example.tasksyncprojectmanagementapp;

import java.util.ArrayList;
import java.util.List;

public class Project {

    private int id;
    private String title;
    private int members;
    private String status;
    private int progress;
    private List<Task> tasks;

    public Project(int id, String title, int members, String status, int progress) {
        this.id = id;
        this.title = title;
        this.members = members;
        this.status = status;
        this.progress = progress;
        this.tasks = new ArrayList<>();
    }

    public Project(String title, int members, String status, int progress) {
        this.title = title;
        this.members = members;
        this.status = status;
        this.progress = progress;
        this.tasks = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getMembers() { return members; }
    public String getStatus() { return status; }
    public int getProgress() { return progress; }
    public List<Task> getTasks() { return tasks; }
    
    public void setTitle(String title) { this.title = title; }
    public void setMembers(int members) { this.members = members; }
    public void setStatus(String status) { this.status = status; }
    public void setProgress(int progress) { this.progress = progress; }

    public void addTask(Task task) {
        this.tasks.add(task);
    }
}
