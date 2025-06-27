package tasks;

import enums.TaskType;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasksIds;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
        subTasksIds = new ArrayList<>();
        super.taskType = TaskType.EPIC;
    }

    public Epic(Epic epic) {
        super(epic);
        this.subTasksIds = new ArrayList<>();
        super.taskType = TaskType.EPIC;
    }

    public ArrayList<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(ArrayList<Integer> subTasksIds) {
        this.subTasksIds = subTasksIds;
    }

    @Override
    public String toString() {
        return super.getId() + "," +
                super.getTaskType() + "," +
                super.getTaskName() + "," +
                super.getTaskStatus() + "," +
                super.getTaskDescription() + ",";
    }
}