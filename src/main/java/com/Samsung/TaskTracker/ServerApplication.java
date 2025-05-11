package com.Samsung.TaskTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		String token = UserRepository.getRepository().AddUser("test", "test");
		UserRepository.getRepository().FindUserByToken(token).addTask(new Task("task", "task", "11.05.2025 21:48", 76134));
		SpringApplication.run(ServerApplication.class, args);
	}

	@GetMapping("/singin")
	String Login(@RequestParam(name="login", defaultValue = "null") String login, @RequestParam(name="password", defaultValue = "null") String pass)
	{
		if(login.equals("null") || pass.equals("null"))
			return "{\n\t\"status\":\"error\",\n\t\"token\":\"null\"\n}";
		else
		{
			User user = UserRepository.getRepository().FindUser(login, pass);
			if(user == null)
				return "{\n\t\"status\":\"error\",\n\t\"token\":\"null\"\n}";
			else
			{
				return "{\n\t\"status\":\"ok\",\n\t\"token\":\"" + user.getToken() + "\"\n}";
			}
		}
	}

	@PostMapping("/singup")
	String SingIn(@RequestParam(name="login", defaultValue = "null") String login, @RequestParam(name="password", defaultValue = "null") String pass)
	{
		if(login.equals("null") || pass.equals("null"))
			return "{\n\t\"status\":\"error\",\n\t\"token\":\"null\"\n}";
		String token = UserRepository.getRepository().AddUser(login, pass);
		if(token.equals("null"))
			return "{\n\t\"status\":\"error\",\n\t\"token\":\"null\"\n}";
		return "{\n\t\"status\":\"ok\",\n\t\"token\":\"" + token + "\"\n}";
	}

	@GetMapping("/user/task")
	public List<Task> getUserTasks(@RequestParam(name="token", defaultValue = "null") String token)
	{
		if(token.equals("null"))
			return new ArrayList<>();
		else
		{
			User user = UserRepository.getRepository().FindUserByToken(token);
			if(user == null)
				return new ArrayList<>();
			else
				return user.getUserTasks();
		}
	}

	@PostMapping("/user/task/add")
	public void addUserTask(@RequestBody Task task, @RequestParam(name="token", defaultValue="null") String token)
	{
		if(!token.equals("null") && task != null)
		{
			User user = UserRepository.getRepository().FindUserByToken(token);
			if(user == null)
				return;
			user.addTask(task);
		}
	}

	@PostMapping("/user/task/remove")
	public void removeUserTask(@RequestParam(name="token", defaultValue="null") String token, @RequestParam(name="id", defaultValue="-1") long id)
	{
		if(!token.equals("null") && id != -1)
		{
			User user = UserRepository.getRepository().FindUserByToken(token);
			if(user == null)
				return;

			user.removeTaskByID(id);
		}
	}
}