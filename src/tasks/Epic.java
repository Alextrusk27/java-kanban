package tasks;

import enums.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTasksIds;
    private LocalDateTime endTime;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
        subTasksIds = new ArrayList<>();
        super.taskType = TaskType.EPIC;
    }

    public Epic(Epic epic) {
        super(epic);
        super.taskType = TaskType.EPIC;
        this.subTasksIds = epic.subTasksIds;
        this.endTime = epic.endTime;
    }

    public ArrayList<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
