package com.Samsung.TaskTraker_Server.Repository;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Objects;

public class Task implements Serializable {
    @JsonIgnore
    private final long dbID;
    private final long ID;
    private final String TaskName;
    private final String TaskDescription;
    private final String TaskDate;
    private final String TaskTime;

    public Task(long dbID, long id, String taskName, String taskDescription, String taskDate, String taskTime) {
        this.TaskName = taskName;
        this.TaskDescription = taskDescription;
        this.TaskDate = taskDate;
        this.TaskTime = taskTime;
        this.ID = id;
        this.dbID = dbID;
    }

    public long getID() {
        return ID;
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

    public String getTaskTime() {
        return TaskTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return dbID == task.dbID && ID == task.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbID, ID, TaskName, TaskDescription, TaskDate, TaskTime);
    }
}
