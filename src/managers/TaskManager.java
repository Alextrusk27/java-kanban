package managers;

import enums.TaskStatus;
import tasks.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private static int defaultId = 0; // для метода генерации id объектов при добавлении в менеджер

    private final HashMap<Integer, Task> tasksList = new HashMap<>();
    private final HashMap<Integer, Epic> epicsList = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasksList = new HashMap<>();

    // добавить новую задачу
    public void addTask(Task task) {
            task.setId(createNewId());
            tasksList.put(task.getId(), task);
    }

    // добавить новый эпик
    public void addEpic(Epic epic) {
            epic.setId(createNewId());
            epicsList.put(epic.getId(), epic);
            checkEpicStatus(epic.getId()); // статус эпика
    }

    // добавить новую подзадачу в список и связка с эпиком
    public void addSubTask(SubTask subTask, int epicId) {
        // эпик с epicId должен существовать
        if (epicsList.containsKey(epicId)) {
            subTask.setId(createNewId());
            subTasksList.put(subTask.getId(), subTask);
            subTask.setEpicId(epicId); // передаем epicId в подзадачу
            Epic epic = epicsList.get(epicId);
            epic.getSubTasksIds().add(subTask.getId()); // добавляем id подзадачи в эпик
            checkEpicStatus(epic.getId()); // статус эпика
        }
    }

    // получение списка задач
    public Collection<Task> getTasksList() {
        return new ArrayList<>(tasksList.values());
    }

    // получение списка эпиков
    public Collection<Epic> getEpicsList() {
        return new ArrayList<>(epicsList.values());
    }

    // получение списка подзадач
    public Collection<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasksList.values());
    }

    // получение списка подзадач определенного эпика
    public ArrayList<SubTask> getSubTasksListByEpic(int epicId) {
        ArrayList<SubTask> result = new ArrayList<>();
        ArrayList<Integer> subTasksIds = epicsList.get(epicId).getSubTasksIds();
        for (SubTask subTask : subTasksList.values()) {
            if (subTasksIds.contains(subTask.getId())) {
                result.add(subTask);
            }
        }
        return result;
    }

    // поиск задачи по id
    public Task getTask(int id) {
        return tasksList.get(id);
    }

    // поиск эпика по id
    public Epic getEpic(int id) {
        return epicsList.get(id);
    }

    // поиск подзадачи по id
    public SubTask getSubTask(int id) {
        return subTasksList.get(id);
    }

    // обновление задачи по id
    public void updateTask(Task task, int taskId) {
        if (tasksList.containsKey(taskId)) {
            tasksList.put(taskId, task);
            task.setId(taskId);
        }
    }

    // обновление эпика по id
    public void updateEpic(Epic epic, int epicId) {
        if (epicsList.containsKey(epicId)) {
            ArrayList<Integer> subTasksIds = new ArrayList<>(epicsList.get(epicId).getSubTasksIds());
            epicsList.get(epicId).getSubTasksIds().clear(); // очистка subTasksIds
            epic.setSubTasksIds(subTasksIds); // передача списка subTasksIds
            epicsList.put(epicId, epic);
            epic.setId(epicId);
            checkEpicStatus(epicId); // статус эпика
        }
    }

    // обновление подзадачи по id
    public void updateSubTask(SubTask subTask, int subTaskId) {
        if (subTasksList.containsKey(subTaskId)) {
            int epicId = subTasksList.get(subTaskId).getEpicId();
            subTasksList.get(subTaskId).setEpicId(0); // обнуление epicId
            subTask.setEpicId(epicId); // передача новой подзадаче epicId
            subTasksList.put(subTaskId, subTask);
            subTask.setId(subTaskId);
            checkEpicStatus(epicId);
        }
    }

    // удаление задачи по id
    public void removeTask(int taskId) {
        tasksList.remove(taskId);
    }

    // удаление подзадачи по id
    public void removeSubTask(int subTaskId) {
        if (subTasksList.containsKey(subTaskId)) {
            SubTask subTask = subTasksList.get(subTaskId); // удаляемая подзадача
            Epic epic = epicsList.get(subTask.getEpicId()); // эпик удаляемой подзадачи
            epic.getSubTasksIds().remove((Integer) subTask.getId()); // удаление id подзадачи в эпике
            checkEpicStatus(subTask.getEpicId());
            subTask.setEpicId(0); // обнуление поля epicId в подзадаче
            subTasksList.remove(subTaskId); // удаление
        }
    }

    // удаление эпика по id
    public void removeEpic(int epicId) {
        if (epicsList.containsKey(epicId)) {
            ArrayList<Integer> subTasksIds = new ArrayList<>(epicsList.get(epicId).getSubTasksIds());
            for (int subTaskId : subTasksIds) { // удаление подзадач эпика
                removeSubTask(subTaskId);
            }
            epicsList.remove(epicId); // удаление эпика
        }
    }

    // удаление всех задач
    public void removeAllTasks() {
        tasksList.clear();
    }

    // удаление всех подзадач
    public void removeAllSubTasks() {
        for (Epic epic : epicsList.values()) {
            epic.getSubTasksIds().clear();
            checkEpicStatus(epic.getId());
        }
        subTasksList.clear();
    }

    // удаление всех эпиков
    public void removeAllEpics() {
        epicsList.clear();
        subTasksList.clear();
    }

    // статус эпика
    private void checkEpicStatus(int epicId) {
        ArrayList<TaskStatus> subTasksStatus = new ArrayList<>(); // список статусов подзадач эпика

        for (int id : getEpic(epicId).getSubTasksIds()) {
            subTasksStatus.add(getSubTask(id).getTaskStatus());
        }

        boolean statusInProgress = subTasksStatus.contains(TaskStatus.IN_PROGRESS);
        boolean statusNew = subTasksStatus.contains(TaskStatus.NEW);
        boolean statusDone = subTasksStatus.contains(TaskStatus.DONE);

        if (statusInProgress || statusNew && statusDone) {
            getEpic(epicId).setTaskStatus(TaskStatus.IN_PROGRESS);
        } else if (statusDone && !statusNew) {
            getEpic(epicId).setTaskStatus(TaskStatus.DONE);
        } else {
            getEpic(epicId).setTaskStatus(TaskStatus.NEW);
        }
    }

    //  генерация ID для новой задачи
    private int createNewId() {
        defaultId++;
        return defaultId;
    }
}

