package managers;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    // добавить новую задачу
    String addTask(Task task);

    // добавить новый эпик
    void addEpic(Epic epic);

    // добавить новую подзадачу в список и связка с эпиком
    void addSubTask(SubTask subTask, int epicId);

    // получение списка задач
    ArrayList<Task> getTasksList();

    // получение списка эпиков
    ArrayList<Epic> getEpicsList();

    // получение списка подзадач
    ArrayList<SubTask> getSubTasksList();

    // получение списка подзадач определенного эпика
    ArrayList<SubTask> getSubTasksListByEpic(int epicId);

    // поиск задачи по id
    Task getTask(int id);

    // поиск эпика по id
    Epic getEpic(int id);

    // поиск подзадачи по id
    SubTask getSubTask(int id);

    // обновление задачи по id
    void updateTask(Task task, int taskId);

    // обновление эпика по id
    void updateEpic(Epic epic, int epicId);

    // обновление подзадачи по id
    void updateSubTask(SubTask subTask, int subTaskId);

    // удаление задачи по id
    void removeTask(int taskId);

    // удаление подзадачи по id
    void removeSubTask(int subTaskId);

    // удаление эпика по id
    void removeEpic(int epicId);

    // удаление всех задач
    void removeAllTasks();

    // удаление всех подзадач
    void removeAllSubTasks();

    // удаление всех эпиков
    void removeAllEpics();

    // получение истории
    List<Task> getHistory();
}

