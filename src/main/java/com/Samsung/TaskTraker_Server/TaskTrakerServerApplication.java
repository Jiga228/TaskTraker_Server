package com.Samsung.TaskTraker_Server;

import com.Samsung.TaskTraker_Server.Repository.*;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.*;

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
                User user = UserRepository.getRepository().getUserByLogin(login);
                if (user == null || !user.getPassword().equals(pass))
                    return "{\n\t\"status\":\"error\"\n}";
                else
                    return "{\n\t\"status\":\"ok\"\n}";
            } catch (SQLException e) {
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
            User user = UserRepository.getRepository().AddUser(login, pass);
            if(user == null)
                return "{\n\t\"status\":\"error\"\n}";

            return "{\n\t\"status\":\"ok\"\n}";
        } catch (SQLException e) {
            return "{\n\t\"status\":\"error\"\n}";
        }
    }

    @GetMapping("/user/task")
    public Task getTask(@RequestParam(value = "login", defaultValue = "null") String login,
                        @RequestParam(value = "password", defaultValue = "null") String pass,
                        @RequestParam(name = "id") long taskID) {
        if (!login.equals("null") || !pass.equals("null")) {
            try {
                User user = UserRepository.getRepository().getUserByLogin(login);

                if (user == null || !user.getPassword().equals(pass))
                    return new Task(0, null, null, null, null);

                List<Task> taskList = user.getTaskList();

                for (Task i : taskList) {
                    if (i.getID() == taskID)
                        return i;
                }
            } catch (SQLException e) {
                return new Task(0, null, null, null, null);
            }
        }
        return new Task(0, null, null, null, null);
    }

    @GetMapping("/user/task/list")
    public List<Task> getUserTasks(@RequestParam(value = "login", defaultValue = "null") String login,
                                   @RequestParam(value = "password", defaultValue = "null") String pass) {
        if (login.equals("null") || pass.equals("null"))
            return new ArrayList<>();
        else {
            try {
                User user = UserRepository.getRepository().getUserByLogin(login);
                if (user == null || !user.getPassword().equals(pass))
                    return new ArrayList<>();
                else
                    return user.getTaskList();
            } catch (SQLException e) {
                return new ArrayList<>();
            }
        }
    }

    @PostMapping("/user/task/edit")
    public void addUserTask(@RequestParam(value = "login", defaultValue = "null") String login,
                            @RequestParam(value = "password", defaultValue = "null") String pass,
                            @RequestBody Task task) {
        if (!login.equals("null") && !pass.equals("null") && task != null) {
            try {
                User user = UserRepository.getRepository().getUserByLogin(login);
                if (user == null || !user.getPassword().equals(pass))
                    return;
                user.addTask(task);
            } catch (SQLException ignored) {
            }
        }
    }

    @PostMapping("/user/task/remove")
    public void removeUserTask(@RequestParam(value = "login", defaultValue = "null") String login,
                               @RequestParam(value = "password", defaultValue = "null") String pass,
                               @RequestParam(name = "id", defaultValue = "-1") long id) {
        if (!login.equals("null") && !pass.equals("null") && id != -1) {
            try {
                User user = UserRepository.getRepository().getUserByLogin(login);
                if (user == null || !user.getPassword().equals(pass))
                    return;
                user.removeTask(id);
            } catch (SQLException ignore) {
            }
        }
    }
}
