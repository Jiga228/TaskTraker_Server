package com.Samsung.TaskTraker_Server.Repository;

import com.Samsung.TaskTraker_Server.API_KEYS;
import com.Samsung.TaskTraker_Server.Models.Task;
import com.Samsung.TaskTraker_Server.Models.User;

import java.sql.*;
import java.util.*;

public class DataBase {
    private static DataBase instance;
    private Connection connection;
    private final String login, password;

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        instance = null;
    }

    private void OpenConnection() {
        try {
            connection = DriverManager.getConnection(API_KEYS.DB_URL, login, password);
        } catch (SQLException e) {
            System.out.println("[!] DB: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private DataBase(String login, String password) {
        this.login = login;
        this.password = password;
        OpenConnection();
    }

    public static DataBase getInstance() {
        if(instance == null) {
            instance = new DataBase(API_KEYS.DB_LOGIN, API_KEYS.DB_PASSWORD);
        }
        return instance;
    }

    public User SaveUser(String login, String password) throws SQLException {
        if(!connection.isValid(100))
            OpenConnection();

        PreparedStatement saveUser_stmt = connection.prepareStatement("INSERT INTO users(login, password) VALUE (?, ?)");
        saveUser_stmt.setString(1, login);
        saveUser_stmt.setString(2, password);
        saveUser_stmt.executeUpdate();

        PreparedStatement findID = connection.prepareStatement("SELECT id FROM users WHERE login = ?");
        findID.setString(1, login);
        ResultSet newID = findID.executeQuery();

        newID.next();
        return new User(newID.getInt(1), login, password);
    }

    public void AddTask(Task task, User user) throws SQLException {
        if(!connection.isValid(100))
            OpenConnection();

        PreparedStatement addTask = connection.prepareStatement("INSERT INTO tasks(ID," +
                                                             " TaskName," +
                                                             " TaskDescription," +
                                                             " TaskDate," +
                                                             " TaskTime," +
                                                             " UserOwner)" +
                                                             " VALUE (?, ?, ?, ?, ?, ?)");

        addTask.setLong(1, task.getID());
        addTask.setString(2, task.getTaskName());
        addTask.setString(3, task.getTaskDescription());
        addTask.setString(4, task.getTaskDate());
        addTask.setString(5, task.getTaskTime());
        addTask.setLong(6, user.getID());
        addTask.executeUpdate();
    }

    public void UpdateTask(Task task, User user) throws SQLException {
        if(!connection.isValid(100))
            OpenConnection();

        PreparedStatement addTask = connection.prepareStatement("UPDATE tasks SET TaskName = ?," +
                                                                     " TaskDescription = ?," +
                                                                     " TaskDate = ?," +
                                                                     " TaskTime = ?" +
                                                                     " WHERE UserOwner = ? and ID = ?");

        addTask.setString(1, task.getTaskName());
        addTask.setString(2, task.getTaskDescription());
        addTask.setString(3, task.getTaskDate());
        addTask.setString(4, task.getTaskTime());
        addTask.setLong(5, user.getID());
        addTask.setLong(6, task.getID());
        addTask.executeUpdate(); // <---------
    }

    public void RemoveTask(long taskID, User user) throws SQLException {
        if(!connection.isValid(100))
            OpenConnection();

        PreparedStatement remove = connection.prepareStatement("DELETE FROM tasks WHERE ID = ? AND UserOwner = ?");
        remove.setLong(1, taskID);
        remove.setLong(2, user.getID());
        remove.executeUpdate();
    }

    public void UpdatePassword(String password, int userID) throws SQLException {
        if(!connection.isValid(100))
            OpenConnection();

        PreparedStatement update = connection.prepareStatement("UPDATE users SET password = ? WHERE id = ?");
        update.setString(1, password);
        update.setInt(2, userID);
        update.executeUpdate();
    }

    public List<Task> getTaskList(User user) throws SQLException {
        if(!connection.isValid(100))
            OpenConnection();

        PreparedStatement findTasks = connection.prepareStatement("SELECT * FROM tasks WHERE UserOwner = ?");
        findTasks.setInt(1, user.getID());
        ResultSet result = findTasks.executeQuery();

        List<Task> taskList = new ArrayList<>();
        while(result.next()) {
            Task task = new Task(result.getLong(1),     // dbID
                                 result.getLong(2),     // ID
                                 result.getString(3),   // TaskName
                                 result.getString(4),   // TaskDescription
                                 result.getString(5),   // TaskDate
                                 result.getString(6));  // TaskTime
            taskList.add(task);
        }
        return taskList;
    }

    public User FindUser(String login) throws SQLException {
        if(!connection.isValid(100))
            OpenConnection();

        PreparedStatement getUser_stmt = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
        getUser_stmt.setString(1, login);
        ResultSet resultSet = getUser_stmt.executeQuery();
        if(!resultSet.next())
            return null;

        int id = resultSet.getInt(1);
        String pass = resultSet.getNString(3);

        return new User(id, login, pass);
    }
}
