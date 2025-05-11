package com.Samsung.TaskTracker;

import java.util.ArrayList;

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

            // Generate TOKEN
            User newUser = new User(Login, Password, "TEST");
            repository.add(newUser);
            return newUser.getToken();
        }
    }
}
