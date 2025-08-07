import enums.TaskStatus;
import managers.FileBackedTaskManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static managers.FileBackedTaskManager.HEAD_LINE_IN_AUTOSAVE_FILE;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private FileBackedTaskManager taskManager;

    @BeforeEach
    public void setDefaultValues(@TempDir Path tempDir) throws IOException {
        super.setDefaultValues();
        File tempFile = tempDir.resolve("junit_file_backed_manager.csv").toFile();
        Files.createFile(tempFile.toPath());
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
    }

    @Test
    public void convertTaskFromString() {
        String taskToString = task1.toString();
        Task taskToCompare = FileBackedTaskManager.taskFromString(taskToString);

        Assertions.assertEquals(task1, taskToCompare, "Некорректное преобразование задачи из строки");
    }

    @Test
    public void convertEpicAndSubTaskFromString() {
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1Epic1, epic1.getId());

        Optional<Epic> epicFromManager = taskManager.getEpic(epic1.getId());
        Optional<SubTask> subTaskFromManager = taskManager.getSubTask(subTask1Epic1.getId());

        Assertions.assertTrue(epicFromManager.isPresent());
        Assertions.assertTrue(subTaskFromManager.isPresent());

        String epicFromManagerToString = epicFromManager.get().toString();
        String subTaskFromManagerToString = subTaskFromManager.get().toString();

        Task epicToCompare = FileBackedTaskManager.taskFromString(epicFromManagerToString);
        Task subTaskToCompare = FileBackedTaskManager.taskFromString(subTaskFromManagerToString);

        Assertions.assertEquals(epicFromManager.get(), epicToCompare, "Некорректное преобразование эпика из строки");
        Assertions.assertEquals(subTaskFromManager.get(), subTaskToCompare, "Некорректное преобразование подзадачи " +
                "из строки");
    }

    @Test
    public void loadManagerFromFile() throws IOException {
        File customAutosaveFile = Files.createTempFile("junit_test_file", ".csv").toFile();

        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1Epic1, epic1.getId());

        int taskId = task1.getId();
        int epicId = epic1.getId();
        int subtaskId = subTask1Epic1.getId();

        Optional<Task> taskFromManager = taskManager.getTask(taskId);
        Optional<Epic> epicFromManager = taskManager.getEpic(epicId);
        Optional<SubTask> subTaskFromManager = taskManager.getSubTask(subtaskId);

        Assertions.assertTrue(taskFromManager.isPresent());
        Assertions.assertTrue(epicFromManager.isPresent());
        Assertions.assertTrue(subTaskFromManager.isPresent());

        try {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(customAutosaveFile, StandardCharsets.UTF_8))) {
                bw.write(HEAD_LINE_IN_AUTOSAVE_FILE);
                bw.newLine();
                bw.write(taskFromManager.get().toString());
                bw.newLine();
                bw.write(epicFromManager.get().toString());
                bw.newLine();
                bw.write(subTaskFromManager.get().toString());
                bw.newLine();
            }

            Assertions.assertEquals(Files.readString(customAutosaveFile.toPath()),
                    Files.readString(taskManager.getAutoSave().toPath()), "Содержимое файлов не совпадает");
        } finally {
            Files.deleteIfExists(customAutosaveFile.toPath());
        }
    }

    @Test
    public void testSaveForAddAndRemoveTasks() throws IOException {
        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1Epic1, epic1.getId());

        int taskId = task1.getId();
        int epicId = epic1.getId();
        int subTaskId = subTask1Epic1.getId();

        Optional<Task> task = taskManager.getTask(taskId);
        Optional<Epic> epic = taskManager.getEpic(epicId);
        Optional<SubTask> subTask = taskManager.getSubTask(subTaskId);

        Assertions.assertTrue(task.isPresent());
        Assertions.assertTrue(epic.isPresent());
        Assertions.assertTrue(subTask.isPresent());

        String taskToString = task.get().toString();
        String epicToString = epic.get().toString();
        String subTaskToString = subTask.get().toString();

        List<String> tasks = List.of(taskToString, epicToString, subTaskToString);
        File file = taskManager.getAutoSave();

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (br.ready()) {
                String line = br.readLine();
                if (line.equals(HEAD_LINE_IN_AUTOSAVE_FILE)) {
                    continue;
                }
                Assertions.assertTrue(tasks.contains(line), "Добавленная задача не найдена в файле");
            }
        }

        taskManager.removeTask(taskId);
        taskManager.removeEpic(epicId);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (br.ready()) {
                String line = br.readLine();
                if (line.equals(HEAD_LINE_IN_AUTOSAVE_FILE)) {
                    continue;
                }
                Assertions.assertFalse(tasks.contains(line), "Удаленная задача найдена в файле");
            }
        }
    }

    @Test
    public void testSaveForRemoveTasksLists() throws IOException {
        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1Epic1, epic1.getId());
        taskManager.addTask(task2);

        int taskId = task1.getId();
        int task2Id = task2.getId();
        int epicId = epic1.getId();
        int subTaskId = subTask1Epic1.getId();

        Optional<Task> optionalTask1 = taskManager.getTask(taskId);
        Optional<Task> optionalTask2 = taskManager.getTask(task2Id);
        Optional<Epic> optionalEpic = taskManager.getEpic(epicId);
        Optional<SubTask> optionalSubTask = taskManager.getSubTask(subTaskId);

        Assertions.assertTrue(optionalTask1.isPresent());
        Assertions.assertTrue(optionalTask2.isPresent());
        Assertions.assertTrue(optionalEpic.isPresent());
        Assertions.assertTrue(optionalSubTask.isPresent());

        String taskToString = optionalTask1.get().toString();
        String task2ToString = optionalTask2.get().toString();
        String epicToString = optionalEpic.get().toString();
        String subTaskToString = optionalSubTask.get().toString();

        List<String> tasks = List.of(taskToString, epicToString, subTaskToString, task2ToString);
        File file = taskManager.getAutoSave();

        taskManager.removeAllTasks();
        taskManager.removeAllEpics();

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (br.ready()) {
                String line = br.readLine();
                if (line.equals(HEAD_LINE_IN_AUTOSAVE_FILE)) {
                    continue;
                }
                Assertions.assertFalse(tasks.contains(line), "Удаленная задача найдена в файле");
            }
        }
    }

    @Test
    public void testSaveForUpdateTasks() throws IOException {
        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1Epic1, epic1.getId());

        int taskId = task1.getId();
        int epicId = epic1.getId();
        int subTaskId = subTask1Epic1.getId();

        Task taskToUpdate = new Task("Обновление задачи", "Описание обновления задачи",
                TaskStatus.DONE);
        Epic epicToUpdate = new Epic("Обновление эпика", "Описание обновления эпика");
        SubTask subTaskToUpdate = new SubTask("Обновление задачи", "Описание обновления задачи",
                TaskStatus.DONE);

        taskManager.updateTask(taskToUpdate, taskId);
        taskManager.updateEpic(epicToUpdate, epicId);
        taskManager.updateSubTask(subTaskToUpdate, subTaskId);

        Optional<Task> task = taskManager.getTask(taskId);
        Optional<Epic> epic = taskManager.getEpic(epicId);
        Optional<SubTask> subTask = taskManager.getSubTask(subTaskId);

        Assertions.assertTrue(task.isPresent());
        Assertions.assertTrue(epic.isPresent());
        Assertions.assertTrue(subTask.isPresent());

        String taskToString = task.get().toString();
        String epicToString = epic.get().toString();
        String subTaskToString = subTask.get().toString();

        List<String> tasks = List.of(taskToString, epicToString, subTaskToString);
        File file = taskManager.getAutoSave();

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (br.ready()) {
                String line = br.readLine();
                if (line.equals(HEAD_LINE_IN_AUTOSAVE_FILE)) {
                    continue;
                }
                Assertions.assertTrue(tasks.contains(line), "Обновленная задача не найдена в файле");
            }
        }
    }
}

