package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    // добавление задачи в историю
    void addToHistory(Task task);

    // получение истории
    List<Task> getHistory();
}
