package tasks;

import enums.TaskStatus;
import enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {

    private int id = 0; // 0 используется для блокировки многократного добавления одного объекта
    private final String taskName;
    private final String taskDescription;
    protected TaskType taskType;
    private TaskStatus taskStatus;
    private LocalDateTime taskStartTime = LocalDateTime.MIN;
    private Duration taskDuration = Duration.ofMinutes(0);

    public Task(String taskName, String taskDescription, TaskStatus taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskType = TaskType.TASK;
    }

    public Task(String taskName, String taskDescription, TaskStatus taskStatus, LocalDateTime dateTime, long duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskType = TaskType.TASK;
        this.taskStartTime = dateTime;
        this.taskDuration = Duration.ofMinutes(duration);
    }

    // для эпиков
    protected Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = TaskStatus.NEW;
    }

    public Task(Task task) {
        this.taskName = task.getTaskName();
        this.taskDescription = task.getTaskDescription();
        this.taskStatus = task.getTaskStatus();
        this.id = task.getId();
        this.taskType = TaskType.TASK;

        if (task.getTaskStartTime() != null) {
            this.taskStartTime = task.getTaskStartTime();
        }

        if (task.getTaskDuration() != null) {
            this.taskDuration = task.getTaskDuration();
        }
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

    public LocalDateTime getTaskStartTime() {
        return taskStartTime;
    }

    public Duration getTaskDuration() {
        return taskDuration;
    }

    public void setTaskStartTime(LocalDateTime taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public void setTaskDuration(long taskDuration) {
        this.taskDuration = Duration.ofMinutes(taskDuration);
    }

    public LocalDateTime getEndTime() {
        return taskStartTime.plus(taskDuration);
    }

    @Override
    public String toString() {
        return id + "," +
                taskType + "," +
                taskName + "," +
                taskStatus + "," +
                taskDescription + "," +
                taskStartTime + "," +
                taskDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(taskName, task.taskName) &&
                Objects.equals(taskDescription, task.taskDescription) &&
                taskType == task.taskType &&
                taskStatus == task.taskStatus &&
                Objects.equals(taskStartTime, task.taskStartTime) &&
                Objects.equals(taskDuration, task.taskDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, taskDescription, taskType, taskStatus, taskStartTime, taskDuration);
    }

    @Override
    public int compareTo(Task task) {
        return this.taskStartTime.compareTo(task.taskStartTime);
    }
}
