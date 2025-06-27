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

import static managers.FileBackedTaskManager.HEAD_LINE_IN_AUTOSAVE_FILE;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager taskManager;
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    public void setDefaultValues(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("junit_file_backed_manager.csv").toFile();
        Files.createFile(tempFile.toPath());
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        epic = new Epic("Эпик", "Описание эпика");
        subTask = new SubTask("Подзадача", "Описание подзадачи", TaskStatus.IN_PROGRESS);
    }

    @Test
    public void convertTaskFromString() {
        String taskToString = task.toString();
        Task taskToCompare = FileBackedTaskManager.taskFromString(taskToString);

        Assertions.assertEquals(task, taskToCompare, "Некорректное преобразование задачи из строки");
    }

    @Test
    public void convertEpicAndSubTaskFromString() {
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask, epic.getId());

        Epic epicFromManager = taskManager.getEpic(epic.getId());
        SubTask subTaskFromManager = taskManager.getSubTask(subTask.getId());

        String epicFromManagerToString = epicFromManager.toString();
        String subTaskFromManagerToString = subTaskFromManager.toString();

        Task epicToCompare = FileBackedTaskManager.taskFromString(epicFromManagerToString);
        Task subTaskToCompare = FileBackedTaskManager.taskFromString(subTaskFromManagerToString);

        Assertions.assertEquals(epicFromManager, epicToCompare, "Некорректное преобразование эпика из строки");
        Assertions.assertEquals(subTaskFromManager, subTaskToCompare, "Некорректное преобразование подзадачи " +
                "из строки");
    }

    @Test
    public void loadManagerFromFile() throws IOException {
        File customAutosaveFile = Files.createTempFile("junit_test_file", ".csv").toFile();

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask, epic.getId());

        int taskId = task.getId();
        int epicId = epic.getId();
        int subtaskId = subTask.getId();

        Task taskFromManager = taskManager.getTask(taskId);
        Epic epicFromManager = taskManager.getEpic(epicId);
        SubTask subTaskFromManager = taskManager.getSubTask(subtaskId);

        try {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(customAutosaveFile, StandardCharsets.UTF_8))) {
                bw.write(HEAD_LINE_IN_AUTOSAVE_FILE);
                bw.newLine();
                bw.write(taskFromManager.toString());
                bw.newLine();
                bw.write(epicFromManager.toString());
                bw.newLine();
                bw.write(subTaskFromManager.toString());
                bw.newLine();
            }
            FileBackedTaskManager taskManagerToCompare = FileBackedTaskManager.loadFromFile(customAutosaveFile);

            Assertions.assertEquals(taskManager.getTasksList(), taskManagerToCompare.getTasksList(),
                    "Задачи не совпадают");
            Assertions.assertEquals(taskManager.getEpicsList(), taskManagerToCompare.getEpicsList(),
                    "Эпики не совпадают");
            Assertions.assertEquals(taskManager.getSubTasksList(), taskManagerToCompare.getSubTasksList(),
                    "Подзадачи не совпадают");
            Assertions.assertEquals(Files.readString(customAutosaveFile.toPath()),
                    Files.readString(taskManager.getAutoSave().toPath()), "Содержимое файлов не совпадает");
        } finally {
            Files.deleteIfExists(customAutosaveFile.toPath());
        }
    }

    @Test
    public void testSaveForAddAndRemoveTasks() throws IOException {
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask, epic.getId());

        int taskId = task.getId();
        int epicId = epic.getId();
        int subTaskId = subTask.getId();

        String taskToString = taskManager.getTask(taskId).toString();
        String epicToString = taskManager.getEpic(epicId).toString();
        String subTaskToString = taskManager.getSubTask(subTaskId).toString();

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
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask, epic.getId());

        Task task2 = new Task("Задача2", "Описание задачи2", TaskStatus.DONE);
        taskManager.addTask(task2);

        int taskId = task.getId();
        int epicId = epic.getId();
        int subTaskId = subTask.getId();
        int task2Id = task2.getId();

        String taskToString = taskManager.getTask(taskId).toString();
        String epicToString = taskManager.getEpic(epicId).toString();
        String subTaskToString = taskManager.getSubTask(subTaskId).toString();
        String task2ToString = taskManager.getTask(task2Id).toString();

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
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask, epic.getId());

        int taskId = task.getId();
        int epicId = epic.getId();
        int subTaskId = subTask.getId();

        Task taskToUpdate = new Task("Обновление задачи", "Описание обновления задачи", TaskStatus.DONE);
        Epic epicToUpdate = new Epic("Обновление эпика", "Описание обновления эпика");
        SubTask subTaskToUpdate = new SubTask("Обновление задачи", "Описание обновления задачи", TaskStatus.DONE);

        taskManager.updateTask(taskToUpdate, taskId);
        taskManager.updateEpic(epicToUpdate, epicId);
        taskManager.updateSubTask(subTaskToUpdate, subTaskId);

        String taskToString = taskManager.getTask(taskId).toString();
        String epicToString = taskManager.getEpic(epicId).toString();
        String subTaskToString = taskManager.getSubTask(subTaskId).toString();

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

