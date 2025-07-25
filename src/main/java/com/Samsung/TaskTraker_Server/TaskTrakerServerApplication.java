package com.Samsung.TaskTraker_Server;

import com.Samsung.TaskTraker_Server.Models.Task;
import com.Samsung.TaskTraker_Server.Models.User;
import com.Samsung.TaskTraker_Server.Repository.*;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
@SpringBootApplication
public class TaskTrakerServerApplication {
    private static volatile boolean isRunning = true;
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TaskTrakerServerApplication.class, args);

        while (isRunning) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        int code = SpringApplication.exit(context, new ExitCodeGenerator() {
            @Override
            public int getExitCode() {
                return 0;
            }
        });
        System.exit(code);
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte arrayByte : arrayBytes) {
            stringBuffer.append(Integer.toString((arrayByte & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    private static String getMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            return convertByteArrayToHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/admin/shutdown")
    void shutdown(@RequestParam(name = "password", defaultValue = "null") String password) {
        if(!password.equals(API_KEYS.ADMIN_PASSWORD))
            return;
        DataBase.getInstance().close();
        isRunning = false;
    }

    @GetMapping("/singin")
    String SingIn(@RequestParam(name = "login", defaultValue = "null") String login, @RequestParam(name = "password", defaultValue = "null") String pass) {
        if (login.length() > 20 || pass.length() > 20)
            return "{\n\t\"status\":\"big\"\n}";
        if (login.equals("null") || pass.equals("null"))
            return "{\n\t\"status\":\"error\"\n}";
        else {
            try {
                String passHash = getMD5Hash(pass);
                User user = UserRepository.getRepository().getUserByLogin(login);
                if (user == null || !user.getPassword().equals(passHash))
                    return "{\n\t\"status\":\"error\"\n}";
                else
                    return "{\n\t\"status\":\"ok\"\n}";
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return "{\n\t\"status\":\"error\"\n}";
            }
        }
    }


    @PostMapping("/singup")
    String SingUp(@RequestParam(name = "login", defaultValue = "null") String login, @RequestParam(name = "password", defaultValue = "null") String pass) {
        if (login.length() > 20 || pass.length() > 20)
            return "{\n\t\"status\":\"big\",\n\t\"token\":\"null\"\n}";
        else if(login.isEmpty() || pass.isEmpty())
            return "{\n\t\"status\":\"small\",\n\t\"token\":\"null\"\n}";
        else if (login.equals("null") || pass.equals("null"))
            return "{\n\t\"status\":\"error\",\n\t\"token\":\"null\"\n}";
        try {
            String passHash = getMD5Hash(pass);
            User user = UserRepository.getRepository().AddUser(login, passHash);
            if(user == null)
                return "{\n\t\"status\":\"error\"\n}";

            return "{\n\t\"status\":\"ok\"\n}";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "{\n\t\"status\":\"error\"\n}";
        }
    }

    @PostMapping("/user/ChangePassword")
    public String editUserPassword(@RequestParam(name = "oldPassword", defaultValue = "") String oldPassword,
                                   @RequestParam(name = "login", defaultValue = "") String login,
                                   @RequestParam(name = "newPassword", defaultValue = "") String newPassword) {
        if(oldPassword.isEmpty() || login.isEmpty() || newPassword.isEmpty())
            return "{\n\t\"status\":\"error\"\n}";

        try {
            String passHash = getMD5Hash(oldPassword);
            User user = UserRepository.getRepository().getUserByLogin(login);
            if(user == null || !user.getPassword().equals(passHash))
                return "{\n\t\"status\":\"error\"\n}";

            passHash = getMD5Hash(newPassword);
            user.setPassword(passHash);
            return "{\n\t\"status\":\"ok\"\n}";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "{\n\t\"status\":\"error\"\n}";
        }
    }

    @GetMapping("/user/task")
    public Task getTask(@RequestParam(value = "login", defaultValue = "null") String login,
                        @RequestParam(value = "password", defaultValue = "null") String pass,
                        @RequestParam(name = "id") long taskID) {
        if (!login.equals("null") || !pass.equals("null")) {
            try {
                String passHash = getMD5Hash(pass);
                User user = UserRepository.getRepository().getUserByLogin(login);

                if (user == null || !user.getPassword().equals(passHash))
                    return new Task(0, 0, null, null, null, null);

                List<Task> taskList = DataBase.getInstance().getTaskList(user);

                for (Task i : taskList) {
                    if (i.getID() == taskID)
                        return i;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return new Task(0, 0, null, null, null, null);
            }
        }
        return new Task(0, 0, null, null, null, null);
    }

    @GetMapping("/user/task/list")
    public List<Task> getUserTasks(@RequestParam(value = "login", defaultValue = "null") String login,
                                   @RequestParam(value = "password", defaultValue = "null") String pass) {
        if (login.equals("null") || pass.equals("null"))
            return new ArrayList<>();
        else {
            try {
                String passHash = getMD5Hash(pass);
                User user = UserRepository.getRepository().getUserByLogin(login);
                if (user == null || !user.getPassword().equals(passHash))
                    return new ArrayList<>();
                else
                    return DataBase.getInstance().getTaskList(user);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return new ArrayList<>();
            }
        }
    }

    @PostMapping("/user/task/add")
    public void addUserTask(@RequestParam(value = "login", defaultValue = "null") String login,
                            @RequestParam(value = "password", defaultValue = "null") String pass,
                            @RequestBody Task task) {
        if (!login.equals("null") && !pass.equals("null") && task != null) {
            try {
                String passHash = getMD5Hash(pass);
                User user = UserRepository.getRepository().getUserByLogin(login);
                if (user == null || !user.getPassword().equals(passHash))
                    return;
                DataBase.getInstance().AddTask(task, user);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @PostMapping("/user/task/edit")
    public void editUserTask(@RequestParam(value = "login", defaultValue = "null") String login,
                            @RequestParam(value = "password", defaultValue = "null") String pass,
                            @RequestBody Task task) {
        if (!login.equals("null") && !pass.equals("null") && task != null) {
            try {
                String passHash = getMD5Hash(pass);
                User user = UserRepository.getRepository().getUserByLogin(login);
                if (user == null || !user.getPassword().equals(passHash))
                    return;
                DataBase.getInstance().UpdateTask(task, user);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @PostMapping("/user/task/remove")
    public void removeUserTask(@RequestParam(value = "login", defaultValue = "null") String login,
                               @RequestParam(value = "password", defaultValue = "null") String pass,
                               @RequestParam(name = "id", defaultValue = "-1") long id) {
        if (!login.equals("null") && !pass.equals("null") && id != -1) {
            try {
                String passHash = getMD5Hash(pass);
                User user = UserRepository.getRepository().getUserByLogin(login);
                if (user == null || !user.getPassword().equals(passHash))
                    return;
                DataBase.getInstance().RemoveTask(id, user);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
