package tasks;

import enums.TaskStatus;
import enums.TaskType;
import exceptions.TaskCreateException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(2000, 1, 1, 1,1);

    private int id = 0; // 0 используется для блокировки многократного добавления одного объекта
    private final String taskName;
    private final String taskDescription;
    protected TaskType taskType;
    private TaskStatus taskStatus;
    protected LocalDateTime taskStartTime = DEFAULT_TIME;
    protected long taskDuration = 0;

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
        this.taskDuration = duration;
    }

    // для эпиков
    protected Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = TaskStatus.NEW;
    }

    public Task(Task task) {
        if (task.getTaskName() != null && task.getTaskDescription() != null && task.getTaskStatus() != null) {

            this.taskName = task.getTaskName();
            this.taskDescription = task.getTaskDescription();
            this.taskStatus = task.getTaskStatus();
            this.id = task.getId();
            this.taskType = TaskType.TASK;

            this.taskStartTime = Objects.requireNonNullElse(task.taskStartTime, DEFAULT_TIME);
            if (task.getTaskDuration() == null) {
                this.taskDuration = 0;
            } else {
                this.taskDuration = task.taskDuration;
            }
        } else {
            throw new TaskCreateException("Некорректные параметры задачи");
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
        return Duration.ofMinutes(taskDuration);
    }

    public void setTaskStartTime(LocalDateTime taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public void setTaskDuration(long taskDuration) {
        this.taskDuration = taskDuration;
    }

    public LocalDateTime getEndTime() {
        return taskStartTime.plus(Duration.ofMinutes(taskDuration));
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
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
