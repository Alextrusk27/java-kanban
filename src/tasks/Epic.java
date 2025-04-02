package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasksIds;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
        subTasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(ArrayList<Integer> subTasksIds) {
        this.subTasksIds = subTasksIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + super.getId() +
                ", epicName='" + super.getTaskName() + '\'' +
                ", epicDescription='" + super.getTaskDescription() + '\'' +
                ", epicStatus=" + super.getTaskStatus() +
                ", subTasksIds=" + subTasksIds +
                "}";
    }
}