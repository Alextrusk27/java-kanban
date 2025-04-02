package tasks;

import enums.TaskStatus;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String taskName, String taskDescription, TaskStatus taskStatus) {
        super(taskName, taskDescription, taskStatus);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + super.getId() +
                ", subTaskName='" + super.getTaskName() + '\'' +
                ", subTaskDescription='" + super.getTaskDescription() + '\'' +
                ", subTaskStatus=" + super.getTaskStatus() +
                ", epicId=" + epicId +
                "}";
    }
}
