package managers;

import enums.TaskStatus;
import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final String HEAD_LINE_IN_AUTOSAVE_FILE = "id,type,name,status,description,epic";
    private final File autoSave;

    public FileBackedTaskManager(String path) {
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                if (path.endsWith(".csv")) {
                    if (Files.readString(file.toPath()).isEmpty()) {
                        this.autoSave = file;
                    } else {
                        throw new ManagerSaveException("Файл должен быть пуст");
                    }
                } else {
                    throw new ManagerSaveException("Файл должен быть в формате .csv");
                }
            } else {
                throw new ManagerSaveException("Файл не найден");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Файл не подходит для FileBackedTaskManager");
        }
    }

    private FileBackedTaskManager(File file) {
        this.autoSave = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask, int epicId) {
        super.addSubTask(subTask, epicId);
        save();
    }

    @Override
    public void updateTask(Task task, int taskId) {
        super.updateTask(task, taskId);
        save();
    }

    @Override
    public void updateEpic(Epic epic, int epicId) {
        super.updateEpic(epic, epicId);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask, int subTaskId) {
        super.updateSubTask(subTask, subTaskId);
        save();
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeSubTask(int subTaskId) {
        super.removeSubTask(subTaskId);
        save();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    public File getAutoSave() {
        return autoSave;
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(autoSave, StandardCharsets.UTF_8))) {
            bw.write("");
            bw.write(HEAD_LINE_IN_AUTOSAVE_FILE);
            bw.newLine();

            for (Task task : getTasksList()) {
                bw.write(task.toString());
                bw.newLine();
            }

            for (Epic epic : getEpicsList()) {
                bw.write(epic.toString());
                bw.newLine();
            }

            for (SubTask subTask : getSubTasksList()) {
                bw.write(subTask.toString());
                bw.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка автосохранения: " + e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (!file.getName().endsWith(".csv")) {
            throw new ManagerSaveException("Файл автосохранения должен быть в формате .csv");
        }

        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            if (Files.readString(file.toPath()).isEmpty()) {
                return new FileBackedTaskManager(file.getPath());
            }
            if (br.readLine().equals(HEAD_LINE_IN_AUTOSAVE_FILE)) {
                while (br.ready()) {

                    Task task = taskFromString(br.readLine());

                    if (task.getId() > defaultId) {
                        defaultId = task.getId();
                    }

                    if (task instanceof SubTask) {
                        taskManager.subTasksList.put(task.getId(), (SubTask) task);
                    } else if (task instanceof Epic) {
                        taskManager.epicsList.put(task.getId(), (Epic) task);
                    } else {
                        taskManager.tasksList.put(task.getId(), task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла автосохранения: " + e.getMessage());
        }
        return taskManager;
    }

    public static Task taskFromString(String value) {

        String[] taskData = value.split(",");
        String taskType = taskData[1];

        int taskId = Integer.parseInt(taskData[0]);
        String taskName = taskData[2];
        String taskDescription = taskData[4];

        TaskStatus taskStatus;
        switch (taskData[3]) {
            case "NEW" -> taskStatus = TaskStatus.NEW;
            case "IN_PROGRESS" -> taskStatus = TaskStatus.IN_PROGRESS;
            case "DONE" -> taskStatus = TaskStatus.DONE;
            default -> throw new IllegalArgumentException("Ошибка преобразования строки в задачу: некорректный " +
                    "статус у задачи ID " + taskId);
        }

        switch (taskType) {
            case "TASK":
                Task task = new Task(taskName, taskDescription, taskStatus);
                task.setId(taskId);
                return task;
            case "EPIC":
                Epic epic = new Epic(taskName, taskDescription);
                epic.setTaskStatus(taskStatus);
                epic.setId(taskId);
                return epic;
            case "SUBTASK":
                SubTask subTask = new SubTask(taskName, taskDescription, taskStatus);
                subTask.setId(taskId);
                subTask.setEpicId(Integer.parseInt(taskData[5]));
                return subTask;
            default:
                throw new IllegalArgumentException("Ошибка преобразования строки в задачу: неизвестный " +
                        "тип задачи у задачи ID " + taskId);

        }
    }
}
