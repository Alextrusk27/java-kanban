package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> historyViews = new ArrayList<>(); // история просмотров
    public static final int HISTORY_LIMIT = 10;


    @Override
    public <T extends Task> void addToHistory(T task) {
        while (historyViews.size() >= HISTORY_LIMIT) {
            historyViews.removeFirst();
        }
        if (task instanceof Epic) {
            Epic taskToAdd = new Epic((Epic) task);
            historyViews.add(taskToAdd);
        } else if (task instanceof SubTask) {
            SubTask subTaskToAdd = new SubTask((SubTask) task);
            historyViews.add(subTaskToAdd);
        } else {
            Task taskToAdd = new Task(task);
            historyViews.add(taskToAdd);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyViews;
    }
}

