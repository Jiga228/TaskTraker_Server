package com.Samsung.TaskTraker_Server.Repository;

import java.io.*;
import java.sql.*;
import java.util.*;

public class DataBase {
    private final Object synhronizedDataBase = new Object();

    private class TaskDB {
        private PreparedStatement statement;

        public TaskDB(PreparedStatement statement) {
            this.statement = statement;
        }

        public void execute() {
            try {
                synchronized (synhronizedDataBase) {
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static DataBase instance;
    private final Connection connection;
    private final List<TaskDB> TaskDBList = new ArrayList<>();
    private final List<User> DataBaseCache = new ArrayList<>();
    private final Timer UpdateDBTimer = new Timer();

    public void close() {
        UpdateDBTimer.cancel();
        for (TaskDB i : TaskDBList)
            i.execute();
        DataBaseCache.clear();
        TaskDBList.clear();
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        instance = null;
    }

    private DataBase(String login, String pass) {
        try {
            connection = DriverManager.getConnection(API_KEYS.DB_URL, login, pass);
        } catch (SQLException e) {
            System.out.println("[!] DB: " + e.getMessage());
            throw new RuntimeException(e);
        }

        UpdateDBTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (TaskDB i : TaskDBList)
                    i.execute();
                DataBaseCache.clear();
                TaskDBList.clear();
            }
        }, 1_800_000,1_800_000);
    }

    public static DataBase getInstance() {
        if(instance == null) {
            instance = new DataBase(API_KEYS.DB_LOGIN, API_KEYS.DB_PASSWORD);
        }
        return instance;
    }

    private static String Serialize(List<Task> task) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream serialize = new ObjectOutputStream(baos);
            serialize.writeObject(task);
            serialize.flush();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            System.out.println("[!] DB: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static List<Task> Deserialize(String bytes) {
        byte[] data = Base64.getDecoder().decode(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (ArrayList<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[!] DB: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void SaveUser(User user) throws SQLException {
        PreparedStatement saveUser_stmt = connection.prepareStatement("INSERT INTO user (login, password, task_list) VALUE (?, ?, ?)");
        saveUser_stmt.setString(1, user.getLogin());
        saveUser_stmt.setString(2, user.getPassword());
        saveUser_stmt.setString(3, Serialize(user.getTaskList()));
        TaskDBList.add(new TaskDB(saveUser_stmt));
        DataBaseCache.add(user);
    }

    public void UpdateTaskList(User user) throws SQLException {
        PreparedStatement updateTask_stmt = connection.prepareStatement("UPDATE user SET task_list = ? WHERE token = ?");
        updateTask_stmt.setString(1, Serialize(user.getTaskList()));
        updateTask_stmt.setLong(2, user.getID());
        TaskDBList.add(new TaskDB(updateTask_stmt));
    }

    public User FindUser(String login) throws SQLException {
        for (User i : DataBaseCache) {
            if(i.getLogin().equals(login))
                return i;
        }

        PreparedStatement getUser_stmt = connection.prepareStatement("SELECT * FROM user WHERE login = ?");
        getUser_stmt.setString(1, login);
        ResultSet resultSet = getUser_stmt.executeQuery();
        if(!resultSet.next())
            return null;

        int id = resultSet.getInt(1);
        String pass = resultSet.getNString(3);
        List<Task> taskList = Deserialize(resultSet.getNString(4));

        return new User(id, login, pass, taskList);
    }
}
