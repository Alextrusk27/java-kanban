package managers;

import enums.TaskStatus;
import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private static int defaultId = 0; // для метода генерации id объектов при добавлении в менеджер

    private final HashMap<Integer, Task> tasksList = new HashMap<>();
    private final HashMap<Integer, Epic> epicsList = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasksList = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public String addTask(Task task) {
            task.setId(createNewId());
            tasksList.put(task.getId(), task);
        return null;
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(createNewId());
        epicsList.put(epic.getId(), epic);
        checkEpicStatus(epic.getId()); // статус эпика
    }

    @Override
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

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasksList.values());
    }

    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epicsList.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasksList.values());
    }

    @Override
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

    @Override
    public Task getTask(int id) {
        historyManager.addToHistory(tasksList.get(id));
        return tasksList.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.addToHistory(epicsList.get(id));
        return epicsList.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        historyManager.addToHistory(subTasksList.get(id));
        return subTasksList.get(id);
    }

    @Override
    public void updateTask(Task task, int taskId) {
        if (tasksList.containsKey(taskId)) {
            tasksList.put(taskId, task);
            task.setId(taskId);
        }
    }

    @Override
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

    @Override
    public void updateSubTask(SubTask subTask, int subTaskId) {
        if (subTasksList.containsKey(subTaskId)) {
            int epicId = subTasksList.get(subTaskId).getEpicId();
            subTask.setEpicId(epicId); // передача новой подзадаче epicId
            subTasksList.put(subTaskId, subTask);
            subTask.setId(subTaskId);
            checkEpicStatus(epicId);
        }
    }

    @Override
    public void removeTask(int taskId) {
        tasksList.remove(taskId);
    }

    @Override
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

    @Override
    public void removeEpic(int epicId) {
        if (epicsList.containsKey(epicId)) {
            ArrayList<Integer> subTasksIds = new ArrayList<>(epicsList.get(epicId).getSubTasksIds());
            for (int subTaskId : subTasksIds) { // удаление подзадач эпика
                removeSubTask(subTaskId);
            }
            epicsList.remove(epicId); // удаление эпика
        }
    }

    @Override
    public void removeAllTasks() {
        tasksList.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (Epic epic : epicsList.values()) {
            epic.getSubTasksIds().clear();
            checkEpicStatus(epic.getId());
        }
        subTasksList.clear();
    }

    @Override
    public void removeAllEpics() {
        epicsList.clear();
        subTasksList.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // статус эпика
    private void checkEpicStatus(int epicId) {
        ArrayList<TaskStatus> subTasksStatus = new ArrayList<>(); // список статусов подзадач эпика

        for (int id : epicsList.get(epicId).getSubTasksIds()) {
            subTasksStatus.add(subTasksList.get(id).getTaskStatus());
        }

        boolean statusInProgress = subTasksStatus.contains(TaskStatus.IN_PROGRESS);
        boolean statusNew = subTasksStatus.contains(TaskStatus.NEW);
        boolean statusDone = subTasksStatus.contains(TaskStatus.DONE);

        if (statusInProgress || statusNew && statusDone) {
            epicsList.get(epicId).setTaskStatus(TaskStatus.IN_PROGRESS);
        } else if (statusDone && !statusNew) {
            epicsList.get(epicId).setTaskStatus(TaskStatus.DONE);
        } else {
            epicsList.get(epicId).setTaskStatus(TaskStatus.NEW);
        }
    }

    //  генерация ID для новой задачи
    private int createNewId() {
        defaultId++;
        return defaultId;
    }
}
