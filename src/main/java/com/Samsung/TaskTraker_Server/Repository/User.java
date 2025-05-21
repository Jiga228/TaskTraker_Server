package com.Samsung.TaskTraker_Server.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private final long ID;
    private final String login;
    private final String password;
    private List<Task> TaskList = new ArrayList<>();

    public User(long ID, String login, String password, List<Task> taskList) {
        this.ID = ID;
        this.login = login;
        this.password = password;
        TaskList = taskList;
    }

    public User(long ID, String login, String password) {
        this.ID = ID;
        this.login = login;
        this.password = password;
    }

    public long getID() {
        return ID;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public List<Task> getTaskList() {
        return TaskList;
    }

    public void addTask(Task task) {
        if(TaskList.contains(task)) {
            TaskList.remove(task); // Просто удаляем потаму, что cравнение идёт только по ID
            TaskList.add(task);
        }
        else {
            TaskList.add(task);
        }
        try {
            DataBase.getInstance().UpdateTaskList(this);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeTask(long taskID) {
        TaskList.remove(new Task(taskID, null, null, null, null));
        try {
            DataBase.getInstance().UpdateTaskList(this);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return ID == user.ID && Objects.equals(login, user.login) && Objects.equals(password, user.password) && Objects.equals(TaskList, user.TaskList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, login, password, TaskList);
    }
}
