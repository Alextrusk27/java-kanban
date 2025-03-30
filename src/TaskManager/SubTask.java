package TaskManager;

public class SubTask extends Task {
    private int mainEpicId = 0;

    public SubTask(String taskName, String taskDescription, TaskStatus taskStatus) {
        super(taskName, taskDescription, taskStatus);
    }

    protected int getMainEpicId() {
        return mainEpicId;
    }

    protected void setMainEpicId(int mainEpicId) {
        this.mainEpicId = mainEpicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + super.getId() +
                ", subTaskName='" + super.getTaskName() + '\'' +
                ", subTaskDescription='" + super.getTaskDescription() + '\'' +
                ", subTaskStatus=" + super.getTaskStatus() +
                ", mainEpicId=" + mainEpicId +
                "}";
    }
}
