package com.Samsung.TaskTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private final String Login, Password;
    private final List<Task> UserTasks = new ArrayList<>();
    private String Token;

    User(String Login, String Password, String Token) {
        this.Login = Login;
        this.Password = Password;
        this.Token = Token;
    }

    public String getLogin() {
        return Login;
    }

    public String getPassword() {
        return Password;
    }

    public void addTask(Task task)
    {
        UserTasks.add(task);
    }

    public String getToken() {
        return Token;
    }

    public void removeTaskByID(long ID) {
        UserTasks.remove(new Task(null, null, null, ID));
    }

    public List<Task> getUserTasks() {
        return UserTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(Login, user.Login) && Objects.equals(Password, user.Password) && Objects.equals(UserTasks, user.UserTasks) && Objects.equals(Token, user.Token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Login, Password, UserTasks, Token);
    }

    @Override
    public String toString() {
        return "{" +
                "Login='" + Login + '\'' +
                ", Password='" + Password + '\'' +
                ", UserTasks=" + UserTasks +
                ", Token='" + Token + '\'' +
                '}';
    }
}