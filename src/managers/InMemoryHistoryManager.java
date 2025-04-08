package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final ArrayList<Task> historyViews = new ArrayList<>(); // история просмотров


    @Override
    public void addToHistory(Task task) {
        Task taskToHistory = new Task(task.getTaskName(), task.getTaskDescription(), task.getTaskStatus());
        if (historyViews.size() == 10) {
            historyViews.removeFirst();
        }
        historyViews.add(taskToHistory);
    }

    @Override
    public List<Task> getHistory() {
        return historyViews;
    }
}

