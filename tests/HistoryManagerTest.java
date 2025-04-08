import org.junit.jupiter.api.*;
import managers.*;
import enums.TaskStatus;
import tasks.*;

import java.util.List;

import static managers.InMemoryHistoryManager.HISTORY_LIMIT;

public class HistoryManagerTest {

    private HistoryManager history;

    private Task task1;
    private Epic epic1;
    private SubTask subTask1Epic1;

    @BeforeEach
    public void setDefaultValues() {
        history = Managers.getDefaultHistory();

        task1 = new Task("task1name", "task1description", TaskStatus.NEW);
        epic1 = new Epic("epic1name", "epic1description");
        subTask1Epic1 = new SubTask("subTask1name", "subTask1description", TaskStatus.NEW);
    }

    @Test
    public void addAnyTaskToHistory() {
        history.addToHistory(task1);
        history.addToHistory(epic1);
        history.addToHistory(subTask1Epic1);

        List<Task> historyList = history.getHistory();

        Assertions.assertTrue(historyList.contains(task1), "Задача не найдена");
        Assertions.assertTrue(historyList.contains(epic1), "Эпик не найден");
        Assertions.assertTrue(historyList.contains(subTask1Epic1), "Подзадача не найдена");
    }

    @Test
    public void ifAddOverLimitValueToListFirstValueWillBeRemoved() {
        int count = 1;
        int overLimit = HISTORY_LIMIT + 2;

        while (count <= overLimit) {
            history.addToHistory(task1);
            count++;
        }

        Assertions.assertEquals(HISTORY_LIMIT, history.getHistory().size(), "Список истории выше лимита");
    }
}
