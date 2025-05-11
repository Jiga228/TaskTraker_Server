package com.Samsung.TaskTracker;

import java.util.Objects;

public class Task {
    private final long ID;
    private final String TaskName;
    private final String TaskDescription;
    private final String TaskDate;

    public Task(String taskName, String taskDescription, String taskDate, long id) {
        TaskName = taskName;
        TaskDescription = taskDescription;
        TaskDate = taskDate;
        ID = id;
    }

    public String getTaskName() {
        return TaskName;
    }

    public String getTaskDescription() {
        return TaskDescription;
    }

    public String getTaskDate() {
        return TaskDate;
    }

    public long getID() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return ID == task.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, TaskName, TaskDescription, TaskDate);
    }

    @Override
    public String toString() {
        return "{" +
                "ID='" + ID + '\'' +
                ", TaskName='" + TaskName + '\'' +
                ", TaskDescription='" + TaskDescription + '\'' +
                ", TaskData='" + TaskDate + '\'' +
                '}';
    }
}
