package tasks;

import enums.TaskStatus;
import enums.TaskType;

import java.util.Objects;

public class Task {

    private int id = 0; // 0 используется для блокировки многократного добавления одного объекта
    private final String taskName;
    private final String taskDescription;
    protected TaskType taskType;
    private TaskStatus taskStatus;

    public Task(String taskName, String taskDescription, TaskStatus taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskType = TaskType.TASK;
    }

    // для эпиков
    protected Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = TaskStatus.NEW;
    }

    public Task(Task task) {
        this.taskName = task.getTaskName();
        this.taskDescription = task.taskDescription;
        this.taskStatus = task.taskStatus;
        this.id = task.getId();
        this.taskType = TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    @Override
    public String toString() {
        return id + "," +
                taskType + "," +
                taskName + "," +
                taskStatus + "," +
                taskDescription + ",";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(taskName, task.taskName) &&
                Objects.equals(taskDescription, task.taskDescription) &&
                taskStatus == task.taskStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, taskDescription, taskStatus);
    }
}
