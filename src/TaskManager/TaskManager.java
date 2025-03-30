package TaskManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private static int defaultId = 0; // для метода генерации id объектов при добавлении в менеджер

    private final HashMap<Integer, Task> tasksList;
    private final HashMap<Integer, Epic> epicsList;
    private final HashMap<Integer, SubTask> subTasksList;

    public TaskManager() {
        tasksList = new HashMap<>();
        epicsList = new HashMap<>();
        subTasksList = new HashMap<>();
    }

    // добавить новую задачу
    public void setTask(Task task) {
        checkTaskType(task);
        if (task.getId() == 0) { // id всех новых объектов == 0, иначе задача уже есть в менеджере с другим id
            task.setId(createNewId());
            tasksList.put(task.getId(), task);
        }
    }

    // добавить новый эпик
    public void setEpic(Epic epic) {
        if (epic.getId() == 0) { // если не 0, значит этот эпик уже есть в менеджере с другим id
            epic.setId(createNewId());
            epicsList.put(epic.getId(), epic);
            checkEpicStatus(epic.getId()); // статус эпика
        }
    }

    // добавить новую подзадачу в список и связка с эпиком
    public void setSubTask(SubTask subTask, int epicId) {
        // если не 0, значит подзадача уже заведена в менеджер + эпик с epicId должен существовать
        if (subTask.getId() == 0 && epicsList.containsKey(epicId)) {
            subTask.setId(createNewId());
            subTasksList.put(subTask.getId(), subTask);
            subTask.setMainEpicId(epicId); // передаем epicId в подзадачу
            Epic epic = epicsList.get(epicId);
            epic.getSubTasksIds().add(subTask.getId()); // добавляем id подзадачи в эпик
            checkEpicStatus(epic.getId()); // статус эпика
        }
    }

    // получение списка задач
    public Collection<Task> getTasksList() {
        return tasksList.values();
    }

    // получение списка епиков
    public Collection<Epic> getEpicsList() {
        return epicsList.values();
    }

    // получение списка подзадач
    public Collection<SubTask> getSubTasksList() {
        return subTasksList.values();
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
        checkTaskType(task);
        if (tasksList.containsKey(taskId)) {
            tasksList.get(taskId).setId(0); // обнуление id
            tasksList.put(taskId, task);
            task.setId(taskId);
        }
    }

    // обновление эпика по id
    public void updateEpic(Epic epic, int epicId) {
        if (epicsList.containsKey(epicId)) {
            epic.setSubTasksIds(epicsList.get(epicId).getSubTasksIds()); // передача списка subTasksIds
            epicsList.get(epicId).setId(0); // обнуление id
            epicsList.get(epicId).getSubTasksIds().clear(); // очистка subTasksIds
            epicsList.put(epicId, epic);
            epic.setId(epicId);
            checkEpicStatus(epicId); // статус эпика
        }
    }

    // обновление подзадачи по id
    public void updateSubTusk(SubTask subTask, int subTuskId) {
        if (subTasksList.containsKey(subTuskId)) {
            subTask.setMainEpicId(subTasksList.get(subTuskId).getMainEpicId()); // передача новой подзадаче mainEpicId
            subTasksList.get(subTuskId).setMainEpicId(0); // обнуление mainEpicId
            subTasksList.get(subTuskId).setId(0); // обнуление id
            subTasksList.put(subTuskId, subTask);
            subTask.setId(subTuskId);
        }
    }

    // удаление задачи по id
    public void removeTusk(int taskId) {
        tasksList.get(taskId).setId(0); // обнуление id
        tasksList.remove(taskId);
    }

    // удаление подзадачи по id
    public void removeSubTask(int subTaskId) {
        if (subTasksList.containsKey(subTaskId)) {
            SubTask subTask = subTasksList.get(subTaskId); // удаляемая подзадача
            Epic epic = epicsList.get(subTask.getMainEpicId()); // эпик удаляемой подзадачи
            epic.getSubTasksIds().remove((Integer) subTask.getId()); // удаление id подзадачи в эпике
            subTask.setMainEpicId(0); // обнуление поля mainEpicId в подзадаче
            subTasksList.get(subTaskId).setId(0); // обнуление id
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
            epicsList.get(epicId).setId(0); // обнуление id
            epicsList.remove(epicId); // удаление эпика
        }
    }

    // удаление всех задач
    public void removeAllTasks() {
        ArrayList<Integer> allTasksIds = new ArrayList<>(tasksList.keySet());
        for (int taskId : allTasksIds) {
            removeTusk(taskId);
        }
    }

    // удаление всех подзадач
    public void removeAllSubTasks() {
        ArrayList<Integer> allSubTusksIds = new ArrayList<>(subTasksList.keySet());
        for (int subTaskId : allSubTusksIds) {
            removeSubTask(subTaskId);
        }
    }

    // удаление всех эпиков
    public void removeAllEpics() {
        ArrayList<Integer> allEpicsIds = new ArrayList<>(epicsList.keySet());
        for (int epicId : allEpicsIds) {
            removeEpic(epicId);
        }
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

    //  генерарация ID для новой задачи
    private int createNewId() {
        defaultId++;
        return defaultId;
    }

    // запрет передачи объектов-наследников в методы
    private void checkTaskType(Task task) {
        if (task instanceof SubTask || task instanceof Epic) {
            throw new IllegalArgumentException("Можно передавать только объекты класса Task");
        }
    }
}

