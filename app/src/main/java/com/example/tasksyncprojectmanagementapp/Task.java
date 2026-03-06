package com.example.tasksyncprojectmanagementapp;

public class Task {
    private int id;
    private String title;
    private String projectTitle;
    private String squad;
    private String status;
    private String dueDate;
    private String priority;
    private String assignee;

    public Task(int id, String title, String projectTitle, String squad, String status, String dueDate, String priority, String assignee) {
        this.id = id;
        this.title = title;
        this.projectTitle = projectTitle;
        this.squad = squad;
        this.status = status;
        this.dueDate = dueDate;
        this.priority = priority;
        this.assignee = assignee;
    }
    public Task(String title, String projectTitle, String squad, String status, String dueDate, String priority, String assignee) {
        this.title = title;
        this.projectTitle = projectTitle;
        this.squad = squad;
        this.status = status;
        this.dueDate = dueDate;
        this.priority = priority;
        this.assignee = assignee;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getProjectTitle() { return projectTitle; }
    public String getSquad() { return squad; }
    public String getStatus() { return status; }
    public String getDueDate() { return dueDate; }
    public String getPriority() { return priority; }
    public String getAssignee() { return assignee; }

    public void setTitle(String title) { this.title = title; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }
    public void setSquad(String squad) { this.squad = squad; }
    public void setStatus(String status) { this.status = status; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
}
