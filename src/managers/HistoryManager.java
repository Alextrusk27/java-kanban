package managers;

import tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {

    <T extends Task> void addToHistory(T task);

    ArrayList<Task> getHistory();
}
