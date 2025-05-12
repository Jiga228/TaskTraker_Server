package com.Samsung.TaskTracker;

import java.util.ArrayList;
import java.util.Random;

public class UserRepository {
    private static UserRepository Self = null;

    private final ArrayList<User> repository = new ArrayList<>();

    public static synchronized UserRepository getRepository()
    {
        if(Self == null)
            Self = new UserRepository();
        return Self;
    }

    public User FindUser(String Login, String Password)
    {
        synchronized (repository) {
            for(User i : repository)
            {
                if(Login.equals(i.getLogin()) && Password.equals(i.getPassword()))
                    return i;
            }
        }
        return null;
    }

    public User FindUserByToken(String token)
    {
        synchronized (repository) {
            for(User i : repository)
            {
                if(token.equals(i.getToken()))
                    return i;
            }
        }
        return null;
    }

    public String AddUser(String Login, String Password)
    {
        synchronized (repository) {
            for(User i : repository)
            {
                if(Login.equals(i.getLogin()) && Password.equals(i.getPassword()))
                    return "null";
            }

            String token = GetNewToken(Login, Password);
            User newUser = new User(Login, Password, token);
            repository.add(newUser);
            return newUser.getToken();
        }
    }

    private String GetNewToken(String login, String password) {
        Random rand = new Random();
        int key1 = login.hashCode();
        int key2 = password.hashCode();
        int key3 = rand.nextInt();
        int token = key1 ^ key2 ^ key3;
        return Integer.toString(token);
    }
}
