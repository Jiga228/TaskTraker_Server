package com.Samsung.TaskTraker_Server.Repository;
import java.util.Objects;

public class User {
    private final int ID;
    private final String login;
    private final String password;

    public User(int ID, String login, String password) {
        this.ID = ID;
        this.login = login;
        this.password = password;
    }

    public int getID() {
        return ID;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return ID == user.ID && Objects.equals(login, user.login) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, login, password);
    }
}
