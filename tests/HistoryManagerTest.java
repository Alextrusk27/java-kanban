import org.junit.jupiter.api.*;
import managers.*;
import enums.TaskStatus;
import tasks.*;

import java.util.List;

public class HistoryManagerTest {

    private HistoryManager historyManager;

    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    public void setDefaultValues() {
        historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault();

        task = new Task("task1name", "task1description", TaskStatus.NEW);
        epic = new Epic("epic1name", "epic1description");
        subTask = new SubTask("subTask1name", "subTask1description", TaskStatus.NEW);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask, epic.getId());
    }

    @Test
    public void addTaskToHistory() {
        historyManager.addToHistory(task);
        historyManager.addToHistory(epic);
        historyManager.addToHistory(subTask);

        Assertions.assertTrue(historyManager.getHistory().contains(task), "Задача не найдена");
        Assertions.assertTrue(historyManager.getHistory().contains(epic), "Эпик не найден");
        Assertions.assertTrue(historyManager.getHistory().contains(subTask), "Подзадача не найдена");
    }

    @Test
    public void historyHaveNoDuplicates() {
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);

        Assertions.assertEquals(1, historyManager.getHistory().size(), "История содержит дубль");
    }

    @Test
    public void getHistoryGetTasksInOrderOfAddition() {
        historyManager.addToHistory(subTask);
        historyManager.addToHistory(task);
        historyManager.addToHistory(epic);
        historyManager.addToHistory(subTask);
        historyManager.addToHistory(task);

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(epic, history.getFirst(), "Порядок нарушен");
        Assertions.assertEquals(task, history.getLast(), "Порядок нарушен");
    }

    @Test
    public void removeTasksRemoveFromHistory() {
        historyManager.addToHistory(task);
        historyManager.remove(task.getId());

        Assertions.assertFalse(historyManager.getHistory().contains(task), "Задача не удалена");
    }
}


