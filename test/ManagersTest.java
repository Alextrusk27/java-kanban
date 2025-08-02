import managers.FileBackedTaskManager;
import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    @Test
    public void createFileBackedHistoryManager(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("junit_file_backed_manager.csv").toFile();
        Files.createFile(tempFile.toPath());
        FileBackedTaskManager fileBackedTaskManager = Managers.getDefaultBacked(tempFile.getPath());
        Assertions.assertNotNull(fileBackedTaskManager, "FileBackedTaskManager не создан");
    }
}
