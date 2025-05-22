package com.Samsung.TaskTraker_Server.Repository;

import java.sql.SQLException;
import java.util.*;

public class UserRepository {
    private static final int MAX_COUNT_USERS_IN_CACHE = 1000;
    private static UserRepository instance = null;

    private final List<User> UserCache = new ArrayList<>();

    private UserRepository() {}

    public static synchronized UserRepository getRepository() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public User AddUser(String login, String password) throws SQLException {
        for (User i : UserCache) {
            if(i.getLogin().equals(login)) {
                return null;
            }
        }

        User find = DataBase.getInstance().FindUser(login);
        if(find != null)
            return null;

        User user = new User(0, login, password);

        DataBase.getInstance().SaveUser(user);
        if(UserCache.size() >= MAX_COUNT_USERS_IN_CACHE)
            UserCache.remove(0);
        UserCache.add(user);

        return user;
    }

    public User getUserByLogin(String login) throws SQLException {
        for (User i : UserCache) {
            if(i.getLogin().equals(login)) {
                return i;
            }
        }

        User loadUser = DataBase.getInstance().FindUser(login);
        if(UserCache.size() >= MAX_COUNT_USERS_IN_CACHE)
            UserCache.remove(0);
        UserCache.add(loadUser);
        return loadUser;
    }

    private String GenerateNewToken(User user) {
        long key1 = user.getID();
        long key2 = user.getLogin().hashCode();
        long key3 = user.getPassword().hashCode();
        long key4 = new Random().nextInt();
        return Base64.getMimeEncoder().encodeToString(Long.toString(key1 ^ key2 ^ key3 ^ key4).getBytes());
    }
}
