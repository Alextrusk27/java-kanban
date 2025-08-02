package tasks;

import enums.TaskStatus;
import enums.TaskType;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String taskName, String taskDescription, TaskStatus taskStatus) {
        super(taskName, taskDescription, taskStatus);
        super.taskType = TaskType.SUBTASK;
    }

    public SubTask(SubTask subTask) {
        super(subTask);
        this.epicId = subTask.getEpicId();
        super.taskType = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return super.getId() + "," +
                super.getTaskType() + "," +
                super.getTaskName() + "," +
                super.getTaskStatus() + "," +
                super.getTaskDescription() + "," +
                epicId;
    }
}
