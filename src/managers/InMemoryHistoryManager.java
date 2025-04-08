package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> historyViews = new ArrayList<>();
    public static final int HISTORY_LIMIT = 10;


    @Override
    public <T extends Task> void addToHistory(T task) {
        if (task != null) {
            historyViews.add(task);
            while (historyViews.size() > HISTORY_LIMIT) {
                historyViews.removeFirst();
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyViews);
    }
}

