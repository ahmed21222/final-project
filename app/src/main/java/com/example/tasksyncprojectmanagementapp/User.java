package com.example.tasksyncprojectmanagementapp;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String username;

    public User(int id, String fullName, String email, String password, String phone, String username) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.username = username;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getUsername() { return username; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setUsername(String username) { this.username = username; }
}
