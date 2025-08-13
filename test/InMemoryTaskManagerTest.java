import enums.TaskStatus;
import exceptions.OverlapException;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Test
    public void addNewTaskAndFindItInTaskList() {
        manager.addTask(task1);
        Optional<Task> testTask = manager.getTask(task1.getId());
        Assertions.assertTrue(testTask.isPresent());
        Assertions.assertEquals(task1, testTask.get(), "Задачи не совпадают.");
    }

    @Test
    public void addNewEpicAndFindItInEpicList() {
        manager.addEpic(epic1);
        Optional<Epic> testEpic = manager.getEpic(epic1.getId());
        Assertions.assertTrue(testEpic.isPresent());
        Assertions.assertEquals(epic1, testEpic.get(), "Эпики не совпадают.");
    }

    @Test
    public void addNewSubTaskAndCheckCorrectConnectionWithEpic() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());
        Optional<SubTask> testSubTask = manager.getSubTask(subTask1Epic1.getId());
        Assertions.assertTrue(testSubTask.isPresent());
        Assertions.assertEquals(subTask1Epic1, testSubTask.get(), "Подзадачи не совпадают.");
    }

    @Test
    public void taskListContainsOnlyAddedTasks() {
        manager.addTask(task1);
        Assertions.assertTrue(manager.getTasksList().contains(task1), "Задача не найдена");
        Assertions.assertFalse(manager.getTasksList().contains(task2), "Задача не добавлялась");
        Assertions.assertEquals(1, manager.getTasksList().size(), "Размер списка не совпадает");
    }

    @Test
    public void epicListContainsOnlyAddedEpics() {
        manager.addEpic(epic1);
        Optional<Epic> testEpic1 = manager.getEpic(epic1.getId());
        Assertions.assertTrue(testEpic1.isPresent());
        Assertions.assertTrue(manager.getEpicsList().contains(testEpic1.get()), "Эпик не найден");
        Assertions.assertEquals(1, manager.getEpicsList().size(), "Размер списка не совпадает");
    }

    @Test
    public void subTasksListContainsOnlyAddedSubTasks() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());
        Optional<SubTask> testSubTask1 = manager.getSubTask(subTask1Epic1.getId());
        Assertions.assertTrue(testSubTask1.isPresent());
        Assertions.assertTrue(manager.getSubTasksList().contains(testSubTask1.get()), "Подзадача не найдена");
        Assertions.assertEquals(1, manager.getSubTasksList().size(), "Размер списка не совпадает");
    }

    @Test
    public void findSubTasksByEpicId() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());
        manager.addSubTask(subTask2Epic1, epic1.getId());
        Optional<Epic> testEpic1 = manager.getEpic(epic1.getId());
        Optional<SubTask> testSubTask1 = manager.getSubTask(subTask1Epic1.getId());
        Optional<SubTask> testSubTask2 = manager.getSubTask(subTask2Epic1.getId());
        Assertions.assertTrue(testEpic1.isPresent());
        Assertions.assertTrue(testSubTask1.isPresent());
        Assertions.assertTrue(testSubTask2.isPresent());
        Assertions.assertTrue(testEpic1.get().getSubTasksIds().contains(testSubTask1.get().getId()),
                "Подзадача не найдена");
        Assertions.assertTrue(testEpic1.get().getSubTasksIds().contains(testSubTask2.get().getId()),
                "Подзадача не найдена");
        Assertions.assertEquals(2, testEpic1.get().getSubTasksIds().size(),
                "Размер списка подзадач не совпадает");
    }

    @Test
    public void updateTaskAndSaveCorrectId() {
        manager.addTask(task1);
        int taskId = task1.getId();
        Optional<Task> taskBeforeUpdate = manager.getTask(taskId);
        Assertions.assertTrue(taskBeforeUpdate.isPresent());
        manager.updateTask(task2, taskId);
        Optional<Task> taskAfterUpdate = manager.getTask(taskId);
        Assertions.assertTrue(taskAfterUpdate.isPresent());
        Assertions.assertEquals(taskBeforeUpdate.get().getId(), taskAfterUpdate.get().getId(), "Id не совпадают");
        Assertions.assertEquals("task2name", taskAfterUpdate.get().getTaskName(), "Некорректный Task name");
        Assertions.assertEquals("task2description", taskAfterUpdate.get().getTaskDescription(),
                "Некорректный Task description");
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskAfterUpdate.get().getTaskStatus(), "Некорректный статус");
    }

    @Test
    public void updateEpicAndSaveCorrectEpicIdAndSubTasksIds() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());
        manager.addSubTask(subTask2Epic1, epic1.getId());
        int epicId = epic1.getId();

        Optional<Epic> beforeUpdate = manager.getEpic(epicId);
        Assertions.assertTrue(beforeUpdate.isPresent());
        Epic epicBeforeUpdate = new Epic(beforeUpdate.get());

        manager.updateEpic(epic2, epicId);

        Optional<Epic> afterUpdate = manager.getEpic(epicId);
        Assertions.assertTrue(afterUpdate.isPresent());
        Epic epicAfterUpdate = new Epic(afterUpdate.get());

        Assertions.assertEquals(epicBeforeUpdate.getId(), epicAfterUpdate.getId(), "Id не совпадают");
        Assertions.assertEquals("epic2name", epicAfterUpdate.getTaskName(), "Некорректный epic name");
        Assertions.assertEquals("epic2description", epicAfterUpdate.getTaskDescription(),
                "Некорректный epic description");
    }

    @Test
    public void updateSubTaskAndSaveCorrectIds() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());

        int subTaskId = subTask1Epic1.getId();
        int epicId = subTask1Epic1.getEpicId();

        Optional<SubTask> beforeUpdate = manager.getSubTask(subTaskId);
        Assertions.assertTrue(beforeUpdate.isPresent());
        SubTask subTaskBeforeUpdate = new SubTask(beforeUpdate.get());

        manager.updateSubTask(subTask3Epic2, subTask1Epic1.getId());

        Optional<SubTask> afterUpdate = manager.getSubTask(subTaskId);
        Assertions.assertTrue(afterUpdate.isPresent());
        SubTask subTaskAfterUpdate = new SubTask(afterUpdate.get());

        Optional<Epic> testEpic = manager.getEpic(epicId);
        Assertions.assertTrue(testEpic.isPresent());

        Assertions.assertEquals(subTaskBeforeUpdate.getId(), subTaskAfterUpdate.getId(), "Id не совпадают");
        Assertions.assertEquals("subTask3name", subTaskAfterUpdate.getTaskName(), "Некорректный subTask name");
        Assertions.assertEquals("subTask3description", subTaskAfterUpdate.getTaskDescription(),
                "Некорректный subTask description");
        Assertions.assertEquals(epicId, subTaskAfterUpdate.getEpicId(), "epicId изменился");
        Assertions.assertTrue(testEpic.get().getSubTasksIds().contains(subTaskId), "subTaskId на найден в эпике");
    }

    @Test
    public void epicStatusChangingCorrectly() {
        manager.addEpic(epic1);
        Optional<Epic> test = manager.getEpic(epic1.getId());
        Assertions.assertTrue(test.isPresent());
        Epic testEpic = test.get();
        Assertions.assertEquals(TaskStatus.NEW, testEpic.getTaskStatus(), "Неверный статус при создании эпика");

        manager.addSubTask(subTask1Epic1, testEpic.getId());
        Assertions.assertEquals(TaskStatus.NEW, testEpic.getTaskStatus(), "Неверный статус при добавлении NEW");

        manager.addSubTask(subTask2Epic1, testEpic.getId());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, testEpic.getTaskStatus(), "Неверный статус " +
                "при добавлении IN PROGRESS");

        manager.addSubTask(subTask3Epic2, testEpic.getId());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, testEpic.getTaskStatus(), "Неверный статус " +
                "при добавлении DONE если есть IN PROGRESS");

        SubTask subTask4 = new SubTask("A", "B", TaskStatus.DONE);
        SubTask subTask5 = new SubTask("C", "D", TaskStatus.DONE);
        manager.updateSubTask(subTask4, subTask1Epic1.getId());
        manager.updateSubTask(subTask5, subTask2Epic1.getId());

        Assertions.assertEquals(TaskStatus.DONE, testEpic.getTaskStatus(), "Неверный статус если все DONE");
    }

    @Test
    public void findTaskById() {
        manager.addTask(task1);
        int taskId = task1.getId();
        Optional<Task> foundTask = manager.getTask(taskId);
        Assertions.assertTrue(foundTask.isPresent(), "Задача не найдена");
    }

    @Test
    public void findEpicById() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());
        int epicId = epic1.getId();
        Optional<Epic> foundEpic = manager.getEpic(epicId);
        Assertions.assertTrue(foundEpic.isPresent(), "Эпик не найден");
    }

    @Test
    public void findSubTaskById() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());
        int subTaskId = subTask1Epic1.getId();
        Optional<SubTask> foundSubTask = manager.getSubTask(subTaskId);
        Assertions.assertTrue(foundSubTask.isPresent(), "Подзадача не найдена");
    }

    @Test
    public void removeTaskById() {
        manager.addTask(task1);
        manager.addTask(task2);
        int taskId = task1.getId();
        Optional<Task> beforeRemoveTask = manager.getTask(taskId);
        Assertions.assertTrue(beforeRemoveTask.isPresent());
        manager.removeTask(taskId);
        Assertions.assertFalse(manager.getTasksList().contains(beforeRemoveTask.get()), "Задача не удалена");
    }

    @Test
    public void removeSubTaskByIdAndRemoveIdFromEpicSubTaskIds() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());
        int subTaskId = subTask1Epic1.getId();
        manager.removeSubTask(subTaskId);
        Optional<Epic> testEpic = manager.getEpic(epic1.getId());
        Assertions.assertTrue(testEpic.isPresent());
        Assertions.assertFalse(testEpic.get().getSubTasksIds().contains(subTaskId), "ID подзадачи не удален из эпика");
    }

    @Test
    public void removeEpicByIdWithSubTasks() {
        manager.addEpic(epic1);
        int epicId = epic1.getId();
        manager.addSubTask(subTask1Epic1, epicId);
        manager.addSubTask(subTask2Epic1, epicId);

        Optional<Epic> epic = manager.getEpic(epicId);
        Optional<SubTask> subTask1 = manager.getSubTask(subTask1Epic1.getId());
        Optional<SubTask> subTask2 = manager.getSubTask(subTask2Epic1.getId());

        Assertions.assertTrue(epic.isPresent());
        Assertions.assertTrue(subTask1.isPresent());
        Assertions.assertTrue(subTask2.isPresent());

        Epic testEpic = new Epic(epic.get());
        SubTask testSubTask1 = new SubTask(subTask1.get());
        SubTask testSubTask2 = new SubTask(subTask2.get());

        manager.removeEpic(epicId);

        Assertions.assertFalse(manager.getEpicsList().contains(testEpic), "Эпик не удален");
        Assertions.assertFalse(manager.getSubTasksList().contains(testSubTask1), "Подзадача не удалена");
        Assertions.assertFalse(manager.getSubTasksList().contains(testSubTask2), "Подзадача не удалена");
    }

    @Test
    public void ifTasksListIsClearedItShouldNotHaveAnyValues() {
        Task task = new Task("A", "B", TaskStatus.NEW);
        Task task2 = new Task("C", "D", TaskStatus.NEW);

        manager.addTask(task);
        manager.addTask(task2);
        manager.removeAllTasks();

        Assertions.assertTrue(manager.getTasksList().isEmpty(), "Задачи не удалены");
    }

    @Test
    public void ifAllEpicsRemovedSubTasksListShouldNotHaveAnyValues() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());

        manager.addEpic(epic2);
        manager.addSubTask(subTask3Epic2, epic2.getId());

        manager.removeAllEpics();

        Assertions.assertTrue(manager.getEpicsList().isEmpty(), "Список эпиков не пуст");
        Assertions.assertTrue(manager.getSubTasksList().isEmpty(), "Список подзадач не пуст");
    }

    @Test
    public void ifSubTasksListIsClearedSubTasksIdsInAllEpicsAreEmpty() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());
        manager.addEpic(epic2);
        manager.addSubTask(subTask3Epic2, epic2.getId());
        manager.removeAllSubTasks();

        Optional<Epic> testEpic1 = manager.getEpic(epic1.getId());
        Optional<Epic> testEpic2 = manager.getEpic(epic2.getId());

        Assertions.assertTrue(testEpic1.isPresent());
        Assertions.assertTrue(testEpic2.isPresent());
        Assertions.assertTrue(testEpic1.get().getSubTasksIds().isEmpty(), "Список подзадач не пуст");
        Assertions.assertTrue(testEpic2.get().getSubTasksIds().isEmpty(), "Список подзадач не пуст");
    }

    @Test
    public void cannotAddOverlappingTask() {
        task1.setTaskStartTime(LocalDateTime.of(2025, 7, 10, 12, 0));
        task1.setTaskDuration(300);
        task2.setTaskStartTime(LocalDateTime.of(2025, 7, 10, 14, 0));
        task2.setTaskDuration(200);

        manager.addTask(task1);

        Assertions.assertThrows(OverlapException.class, () -> manager.addTask(task2),
                "Была добавлена новая пересекающаяся задача");
    }

    @Test
    public void cannotUpdateOverlappingTask() {
        task1.setTaskStartTime(LocalDateTime.of(2025, 7, 10, 12, 0));
        task1.setTaskDuration(300);

        task2.setTaskStartTime(LocalDateTime.of(2025, 7, 10, 14, 0));
        task2.setTaskDuration(200);

        manager.addTask(task1);

        Assertions.assertThrows(OverlapException.class, () -> manager.updateTask(task2, task1.getId()),
                "Была обновлена новая пересекающаяся задача");
    }

    @Test
    public void setStartTimeAndDurationForEpics() {
        LocalDateTime startTime1 = LocalDateTime.of(2025, 8, 11, 10, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2025, 8, 11, 11, 30);
        subTask1Epic1.setTaskStartTime(startTime1);
        subTask1Epic1.setTaskDuration(30);
        subTask2Epic1.setTaskStartTime(startTime2);
        subTask2Epic1.setTaskDuration(40);

        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());
        manager.addSubTask(subTask2Epic1, epic1.getId());

        long calculatedDuration = Duration.between(startTime1, startTime2).plus(subTask2Epic1.getTaskDuration())
                .toMinutes();

        Optional<Epic> epic = manager.getEpic(epic1.getId());
        Assertions.assertTrue(epic.isPresent());

        long epicDurationFromManager = epic.get().getTaskDuration().toMinutes();

        Assertions.assertEquals(calculatedDuration, epicDurationFromManager);
        Assertions.assertEquals(startTime1, epic.get().getTaskStartTime());
    }

    @Test
    public void getPrioritizedTasks() {
        task1.setTaskStartTime(LocalDateTime.of(2025, 7, 10, 12, 0));
        task1.setTaskDuration(60);
        task2.setTaskStartTime(LocalDateTime.of(2025, 7, 10, 14, 0));
        task2.setTaskDuration(120);
        subTask1Epic1.setTaskStartTime(LocalDateTime.of(2025, 8, 11, 10, 0));
        subTask1Epic1.setTaskDuration(30);
        subTask2Epic1.setTaskStartTime(LocalDateTime.of(2025, 8, 11, 20, 45));
        subTask2Epic1.setTaskDuration(40);

        manager.addEpic(epic1);
        manager.addTask(task2);
        manager.addSubTask(subTask1Epic1, epic1.getId());
        manager.addTask(task1);
        manager.addSubTask(subTask2Epic1, epic1.getId());

        // список по дате вручную
        List<Task> tasksInOrder = new LinkedList<>();

        Optional<Task> optionalTask1 = manager.getTask(task1.getId());
        Optional<Task> optionalTask2 = manager.getTask(task2.getId());
        Optional<SubTask> optionalSubTask1 = manager.getSubTask(subTask1Epic1.getId());
        Optional<SubTask> optionalSubTask2 = manager.getSubTask(subTask2Epic1.getId());

        Assertions.assertTrue(optionalTask1.isPresent());
        Assertions.assertTrue(optionalTask2.isPresent());
        Assertions.assertTrue(optionalSubTask1.isPresent());
        Assertions.assertTrue(optionalSubTask2.isPresent());

        tasksInOrder.add(optionalTask1.get());
        tasksInOrder.add(optionalTask2.get());
        tasksInOrder.add(optionalSubTask1.get());
        tasksInOrder.add(optionalSubTask2.get());

        // список по порядку методом getPrioritizedTasks()
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        Assertions.assertEquals(tasksInOrder, prioritizedTasks);
    }
}
