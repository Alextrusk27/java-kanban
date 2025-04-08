package managers;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask, int epicId);

    ArrayList<Task> getTasksList();

    ArrayList<Epic> getEpicsList();

    ArrayList<SubTask> getSubTasksList();

    ArrayList<SubTask> getSubTasksListByEpic(int epicId);

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    void updateTask(Task task, int taskId);

    void updateEpic(Epic epic, int epicId);

    void updateSubTask(SubTask subTask, int subTaskId);

    void removeTask(int taskId);

    void removeSubTask(int subTaskId);

    void removeEpic(int epicId);

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    List<Task> getHistory();
}

