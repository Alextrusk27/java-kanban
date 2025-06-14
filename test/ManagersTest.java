import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.*;

public class ManagersTest {

    @Test
    public void createTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        Assertions.assertNotNull(taskManager, "TaskManager не создан");
        Assertions.assertNotNull(taskManager.getTasksList(), "Список задач не создан");
        Assertions.assertNotNull(taskManager.getSubTasksList(), "Список подзадач не создан");
        Assertions.assertNotNull(taskManager.getEpicsList(), "Список эпиков не создан");
        Assertions.assertNotNull(taskManager.getHistory(), "История просмотров не создана");
    }

    @Test
    public void createHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Assertions.assertNotNull(historyManager.getHistory(), "Спсиок просмотров не создан");
    }
}
