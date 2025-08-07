package managers;

import enums.TaskStatus;
import enums.TaskType;
import exceptions.OverlapException;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class InMemoryTaskManager implements TaskManager {
    protected static int defaultId = 0; // для метода генерации id объектов при добавлении в менеджер

    protected final HashMap<Integer, Task> tasksList = new HashMap<>();
    protected final HashMap<Integer, Epic> epicsList = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasksList = new HashMap<>();
    private Set<Task> prioritizedTasks = new TreeSet<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void addTask(Task task) {
        if (isOverlap(task)) {
            throw new OverlapException("Задачи пересекаются");
        }
        task.setId(createNewId());
        Task newTask = new Task(task);
        tasksList.put(newTask.getId(), newTask);
        syncTasksPriority();
    }

    @Override
    public void addEpic(Epic epic) {
        if (isOverlap(epic)) {
            throw new OverlapException("Задачи пересекаются");
        }
        epic.setId(createNewId());
        Epic newEpic = new Epic(epic);
        epicsList.put(newEpic.getId(), newEpic);
        syncTasksPriority();
    }

    @Override
    public void addSubTask(SubTask subTask, int epicId) {
        if (isOverlap(subTask)) {
            throw new OverlapException("Задачи пересекаются");
        }
        // эпик с epicId должен существовать
        if (epicsList.containsKey(epicId)) {
            subTask.setId(createNewId());
            subTask.setEpicId(epicId); // передача epicId

            SubTask newSubTask = new SubTask(subTask);

            subTasksList.put(newSubTask.getId(), newSubTask);
            Epic epic = epicsList.get(epicId);
            epic.getSubTasksIds().add(newSubTask.getId()); // добавляем id подзадачи в эпик
            setEpicParameters(epic.getId());
            syncTasksPriority();
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

        List<SubTask> result = getEpic(epicId)
                .map(epic -> epic.getSubTasksIds().stream()
                        .map(this::getSubTask)
                        .flatMap(Optional::stream)
                        .toList())
                .orElseGet(Collections::emptyList);

        return new ArrayList<>(result);
    }

    @Override
    public Optional<Task> getTask(int id) {
        if (tasksList.containsKey(id)) {
            historyManager.addToHistory(tasksList.get(id));
        }
        return Optional.of(tasksList.get(id));
    }

    @Override
    public Optional<Epic> getEpic(int id) {
        if (epicsList.containsKey(id)) {
            historyManager.addToHistory(epicsList.get(id));
        }
        return Optional.of(epicsList.get(id));
    }

    @Override
    public Optional<SubTask> getSubTask(int id) {
        if (subTasksList.containsKey(id)) {
            historyManager.addToHistory(subTasksList.get(id));
        }
        return Optional.of(subTasksList.get(id));
    }

    @Override
    public void updateTask(Task task, int taskId) {
        if (isOverlap(task)) {
            throw new OverlapException("Задачи пересекаются");
        }
        if (tasksList.containsKey(taskId)) {
            task.setId(taskId);
            Task newTask = new Task(task);
            tasksList.put(taskId, newTask);
            syncTasksPriority();
        }
    }

    @Override
    public void updateEpic(Epic epic, int epicId) {
        if (isOverlap(epic)) {
            throw new OverlapException("Задачи пересекаются");
        }
        if (epicsList.containsKey(epicId)) {
            epic.setId(epicId);

            Epic newEpic = new Epic(epic);
            epicsList.put(epicId, newEpic);

            setEpicParameters(epicId); // статус эпика
            syncTasksPriority();
        }
    }

    @Override
    public void updateSubTask(SubTask subTask, int subTaskId) {
        if (isOverlap(subTask)) {
            throw new OverlapException("Задачи пересекаются");
        }
        if (subTasksList.containsKey(subTaskId)) {
            int epicId = subTasksList.get(subTaskId).getEpicId();
            subTask.setId(subTaskId);

            SubTask newSubTusk = new SubTask(subTask);
            newSubTusk.setEpicId(epicId);
            subTasksList.put(subTaskId, newSubTusk);

            setEpicParameters(epicId);
            syncTasksPriority();
        }
    }

    @Override
    public void removeTask(int taskId) {
        tasksList.remove(taskId);
        historyManager.remove(taskId);
        syncTasksPriority();
    }

    @Override
    public void removeSubTask(int subTaskId) {
        if (subTasksList.containsKey(subTaskId)) {
            SubTask subTask = subTasksList.get(subTaskId); // удаляемая подзадача
            Epic epic = epicsList.get(subTask.getEpicId()); // эпик удаляемой подзадачи
            epic.getSubTasksIds().remove((Integer) subTask.getId()); // удаление id подзадачи в эпике
            setEpicParameters(subTask.getEpicId());
            subTask.setEpicId(0); // обнуление поля epicId в подзадаче
            subTasksList.remove(subTaskId);
            historyManager.remove(subTaskId);// удаление
            syncTasksPriority();
        }
    }

    @Override
    public void removeEpic(int epicId) {
        if (epicsList.containsKey(epicId)) {
            ArrayList<Integer> subTasksIds = new ArrayList<>(epicsList.get(epicId).getSubTasksIds());
            subTasksIds.forEach(this::removeSubTask);

            epicsList.remove(epicId); // удаление эпика
            historyManager.remove(epicId);
            syncTasksPriority();
        }
    }

    @Override
    public void removeAllTasks() {
        tasksList.clear();
        syncTasksPriority();
    }

    @Override
    public void removeAllSubTasks() {
        epicsList.values().forEach(epic -> epic.getSubTasksIds().clear());
        subTasksList.clear();
        syncTasksPriority();
    }

    @Override
    public void removeAllEpics() {
        epicsList.clear();
        subTasksList.clear();
        syncTasksPriority();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private void setEpicParameters(int epicId) {
        Epic currentEpic = getEpic(epicId)
                .orElseThrow(() -> new NoSuchElementException("Задача не найдена"));
        List<SubTask> subTasks = new ArrayList<>();

        // вычисление TaskStatus
        List<TaskStatus> subTasksStatus = currentEpic.getSubTasksIds().stream()
                .map(this::getSubTask)
                .flatMap(Optional::stream)
                .peek(subTasks::add)
                .map(Task::getTaskStatus)
                .toList();

        boolean statusInProgress = subTasksStatus.contains(TaskStatus.IN_PROGRESS);
        boolean statusNew = subTasksStatus.contains(TaskStatus.NEW);
        boolean statusDone = subTasksStatus.contains(TaskStatus.DONE);

        if (statusInProgress || statusNew && statusDone) {
            currentEpic.setTaskStatus(TaskStatus.IN_PROGRESS);
        } else if (statusDone && !statusNew) {
            currentEpic.setTaskStatus(TaskStatus.DONE);
        } else {
            currentEpic.setTaskStatus(TaskStatus.NEW);
        }

        if (!subTasks.isEmpty()) {
            List<SubTask> sortedSubTasks = subTasks.stream()
                    .sorted(Comparator.comparing(SubTask::getTaskStartTime))
                    .toList();

            LocalDateTime startTime = sortedSubTasks.getFirst().getTaskStartTime();
            LocalDateTime endTime = sortedSubTasks.getLast().getTaskStartTime()
                    .plus(sortedSubTasks.getLast().getTaskDuration());

            // установка StartTime
            currentEpic.setTaskStartTime(startTime);

            // установка EndTime
            currentEpic.setEndTime(endTime);

            // установка Duration
            currentEpic.setTaskDuration(Duration.between(startTime, endTime).toMinutes());
        }
    }

    private void syncTasksPriority() {
        prioritizedTasks.clear();
        prioritizedTasks.addAll(
                Stream.of(tasksList.values(), subTasksList.values(), epicsList.values())
                        .flatMap(Collection::stream)
                        .filter(task -> task.getTaskStartTime() != LocalDateTime.MIN)
                        .toList()
        );
    }

    private boolean isOverlap(Task task) {
        if (task.getTaskStartTime() != LocalDateTime.MIN) {
            return getPrioritizedTasks().stream()
                    .filter(t -> !t.getTaskType().equals(TaskType.EPIC))
                    .anyMatch(t -> t.getTaskStartTime().isBefore(task.getTaskStartTime()) &&
                            t.getEndTime().isAfter(task.getTaskStartTime()) ||
                            (task.getTaskStartTime().isBefore(t.getEndTime())) &&
                                    (task.getEndTime().isAfter(t.getEndTime())));
        }
        return false;
    }

    private int createNewId() {
        defaultId++;
        return defaultId;
    }
}



