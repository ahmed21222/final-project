package com.example.tasksyncprojectmanagementapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TaskSync.db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_USER_NAME = "full_name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_PHONE = "phone";
    private static final String COL_USER_USERNAME = "username";

    private static final String TABLE_PROJECTS = "projects";
    private static final String COL_PROJECT_ID = "id";
    private static final String COL_PROJECT_TITLE = "title";
    private static final String COL_PROJECT_MEMBERS = "members";
    private static final String COL_PROJECT_STATUS = "status";
    private static final String COL_PROJECT_PROGRESS = "progress";
    private static final String COL_PROJECT_USER_ID = "user_id";

    private static final String TABLE_TASKS = "tasks";
    private static final String COL_TASK_ID = "task_id";
    private static final String COL_TASK_TITLE = "task_title";
    private static final String COL_TASK_PROJECT_TITLE = "project_title";
    private static final String COL_TASK_SQUAD = "squad";
    private static final String COL_TASK_STATUS = "status";
    private static final String COL_TASK_DUE_DATE = "due_date";
    private static final String COL_TASK_PRIORITY = "priority";
    private static final String COL_TASK_ASSIGNEE = "assignee";
    private static final String COL_TASK_USER_ID = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_EMAIL + " TEXT UNIQUE, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_PHONE + " TEXT, " +
                COL_USER_USERNAME + " TEXT)";
        
        String createProjectsTable = "CREATE TABLE " + TABLE_PROJECTS + " (" +
                COL_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PROJECT_TITLE + " TEXT, " +
                COL_PROJECT_MEMBERS + " INTEGER, " +
                COL_PROJECT_STATUS + " TEXT, " +
                COL_PROJECT_PROGRESS + " INTEGER, " +
                COL_PROJECT_USER_ID + " INTEGER)";

        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TASK_TITLE + " TEXT, " +
                COL_TASK_PROJECT_TITLE + " TEXT, " +
                COL_TASK_SQUAD + " TEXT, " +
                COL_TASK_STATUS + " TEXT, " +
                COL_TASK_DUE_DATE + " TEXT, " +
                COL_TASK_PRIORITY + " TEXT, " +
                COL_TASK_ASSIGNEE + " TEXT, " +
                COL_TASK_USER_ID + " INTEGER)";
        
        db.execSQL(createUsersTable);
        db.execSQL(createProjectsTable);
        db.execSQL(createTasksTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public boolean registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        values.put(COL_USER_PHONE, "");
        values.put(COL_USER_USERNAME, email.split("@")[0]);
        return db.insert(TABLE_USERS, null, values) != -1;
    }

    public int getUserId(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_USER_ID + " FROM " + TABLE_USERS + 
                " WHERE " + COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?", new String[]{email, password});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }

    public User getUserDetails(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
            );
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    public boolean updateUserProfile(int userId, String name, String email, String phone, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PHONE, phone);
        values.put(COL_USER_USERNAME, username);
        return db.update(TABLE_USERS, values, COL_USER_ID + "=?", new String[]{String.valueOf(userId)}) > 0;
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, newPassword);
        return db.update(TABLE_USERS, values, COL_USER_ID + "=?", new String[]{String.valueOf(userId)}) > 0;
    }

    public boolean checkPassword(int userId, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_ID + "=? AND " + COL_USER_PASSWORD + "=?", 
                new String[]{String.valueOf(userId), password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public boolean addProject(String title, int members, String status, int progress, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROJECT_TITLE, title);
        values.put(COL_PROJECT_MEMBERS, members);
        values.put(COL_PROJECT_STATUS, status);
        values.put(COL_PROJECT_PROGRESS, progress);
        values.put(COL_PROJECT_USER_ID, userId);
        return db.insert(TABLE_PROJECTS, null, values) != -1;
    }

    public List<Project> getUserProjects(int userId) {
        List<Project> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROJECTS + " WHERE " + COL_PROJECT_USER_ID + "=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new Project(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getInt(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean updateProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROJECT_TITLE, project.getTitle());
        values.put(COL_PROJECT_MEMBERS, project.getMembers());
        values.put(COL_PROJECT_STATUS, project.getStatus());
        values.put(COL_PROJECT_PROGRESS, project.getProgress());
        return db.update(TABLE_PROJECTS, values, COL_PROJECT_ID + "=?", new String[]{String.valueOf(project.getId())}) > 0;
    }

    public boolean deleteProject(int projectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PROJECTS, COL_PROJECT_ID + "=?", new String[]{String.valueOf(projectId)}) > 0;
    }

    public boolean addTask(Task task, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_TITLE, task.getTitle());
        values.put(COL_TASK_PROJECT_TITLE, task.getProjectTitle());
        values.put(COL_TASK_SQUAD, task.getSquad());
        values.put(COL_TASK_STATUS, task.getStatus());
        values.put(COL_TASK_DUE_DATE, task.getDueDate());
        values.put(COL_TASK_PRIORITY, task.getPriority());
        values.put(COL_TASK_ASSIGNEE, task.getAssignee());
        values.put(COL_TASK_USER_ID, userId);
        return db.insert(TABLE_TASKS, null, values) != -1;
    }

    public List<Task> getUserTasks(int userId) {
        List<Task> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + COL_TASK_USER_ID + "=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new Task(
                        cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5), cursor.getString(6),
                        cursor.getString(7)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_TITLE, task.getTitle());
        values.put(COL_TASK_PROJECT_TITLE, task.getProjectTitle());
        values.put(COL_TASK_SQUAD, task.getSquad());
        values.put(COL_TASK_STATUS, task.getStatus());
        values.put(COL_TASK_DUE_DATE, task.getDueDate());
        values.put(COL_TASK_PRIORITY, task.getPriority());
        values.put(COL_TASK_ASSIGNEE, task.getAssignee());
        return db.update(TABLE_TASKS, values, COL_TASK_ID + "=?", new String[]{String.valueOf(task.getId())}) > 0;
    }

    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TASKS, COL_TASK_ID + "=?", new String[]{String.valueOf(taskId)}) > 0;
    }
}
