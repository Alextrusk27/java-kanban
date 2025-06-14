import enums.TaskStatus;
import managers.Managers;
import managers.TaskManager;
import tasks.*;
import org.junit.jupiter.api.*;

public class TaskManagerTest {

    private TaskManager manager;

    private Task task1;
    private Task task2;

    private Epic epic1;
    private Epic epic2;

    private SubTask subTask1Epic1;
    private SubTask subTask2Epic1;
    private SubTask subTask3Epic2;


    @BeforeEach
    public void setDefaultValues() {
        manager = Managers.getDefault();

        task1 = new Task("task1name", "task1description", TaskStatus.NEW);
        task2 = new Task("task2name", "task2description", TaskStatus.IN_PROGRESS);

        epic1 = new Epic("epic1name", "epic1description");
        epic2 = new Epic("epic2name", "epic2description");

        subTask1Epic1 = new SubTask("subTask1name", "subTask1description", TaskStatus.NEW);
        subTask2Epic1 = new SubTask("subTask2name", "subTask2description", TaskStatus.IN_PROGRESS);
        subTask3Epic2 = new SubTask("subTask3name", "subTask3description", TaskStatus.DONE);
    }

    @Test
    public void addNewTaskAndFindItInTaskList() {
        manager.addTask(task1);
        Task testTask = manager.getTask(task1.getId());

        Assertions.assertEquals(task1, testTask, "Задачи не совпадают.");
    }

    @Test
    public void addNewEpicAndFindItInEpicList() {
        manager.addEpic(epic1);
        Epic testEpic = manager.getEpic(epic1.getId());

        Assertions.assertNotNull(testEpic, "Эпик не найден.");
        Assertions.assertEquals(epic1, testEpic, "Эпики не совпадают.");
    }

    @Test
    public void addNewSubTaskAndCheckCorrectConnectionWithEpic() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());

        final SubTask testSubTask = manager.getSubTask(subTask1Epic1.getId());

        Assertions.assertEquals(subTask1Epic1, testSubTask, "Подзадачи не совпадают.");
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
        Epic testEpic1 = manager.getEpic(epic1.getId());

        Assertions.assertTrue(manager.getEpicsList().contains(testEpic1), "Эпик не найден");
        Assertions.assertEquals(1, manager.getEpicsList().size(), "Размер списка не совпадает");
    }

    @Test
    public void subTasksListContainsOnlyAddedSubTasks() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());

        SubTask testSubTask1 = manager.getSubTask(subTask1Epic1.getId());

        Assertions.assertTrue(manager.getSubTasksList().contains(testSubTask1), "Подзадача не найдена");
        Assertions.assertEquals(1, manager.getSubTasksList().size(), "Размер списка не совпадает");
    }

    @Test
    public void findSubTasksByEpicId() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());
        manager.addSubTask(subTask2Epic1, epic1.getId());

        Epic testEpic1 = manager.getEpic(epic1.getId());
        SubTask testSubTask1 = manager.getSubTask(subTask1Epic1.getId());
        SubTask testSubTask2 = manager.getSubTask(subTask2Epic1.getId());

        Assertions.assertTrue(testEpic1.getSubTasksIds().contains(testSubTask1.getId()), "Подазадача не найдена");
        Assertions.assertTrue(testEpic1.getSubTasksIds().contains(testSubTask2.getId()), "Подазадача не найдена");
        Assertions.assertEquals(2, testEpic1.getSubTasksIds().size(), "Размер списка подзадач не совпадает");
    }

    @Test
    public void updateTaskAndSaveCorrectId() {
        manager.addTask(task1);

        final int taskId = task1.getId();

        Task taskBeforeUpdate = new Task(manager.getTask(taskId));
        manager.updateTask(task2, taskId);
        Task taskAfterUpdate = new Task(manager.getTask(taskId));

        Assertions.assertEquals(taskBeforeUpdate.getId(), taskAfterUpdate.getId(), "Id не совпадают");
        Assertions.assertEquals("task2name", taskAfterUpdate.getTaskName(), "Некорректный Task name");
        Assertions.assertEquals("task2description", taskAfterUpdate.getTaskDescription(),
                "Некорректный Task description");
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskAfterUpdate.getTaskStatus(), "Некорректный статус");
    }

    @Test
    public void updateEpicAndSaveCorrectEpicIdAndSubTasksIds() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());
        manager.addSubTask(subTask2Epic1, epic1.getId());

        final int epicId = epic1.getId();

        Epic epicBeforeUpdate = new Epic(manager.getEpic(epicId));
        manager.updateEpic(epic2, epicId);
        Epic epicAfterUpdate = new Epic(manager.getEpic(epicId));

        Assertions.assertEquals(epicBeforeUpdate.getId(), epicAfterUpdate.getId(), "Id не совпадают");
        Assertions.assertEquals("epic2name", epicAfterUpdate.getTaskName(), "Некорректный epic name");
        Assertions.assertEquals("epic2description", epicAfterUpdate.getTaskDescription(),
                "Некорректный epic description");
    }

    @Test
    public void updateSubTaskAndSaveCorrectIds() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());

        final int subTaskId = subTask1Epic1.getId();
        final int epicId = subTask1Epic1.getEpicId();

        SubTask subTaskBeforeUpdate = new SubTask(manager.getSubTask(subTaskId));
        manager.updateSubTask(subTask3Epic2, subTask1Epic1.getId());
        SubTask subTaskAfterUpdate = new SubTask(manager.getSubTask(subTaskId));

        Epic testEpic = manager.getEpic(epicId);

        Assertions.assertEquals(subTaskBeforeUpdate.getId(), subTaskAfterUpdate.getId(), "Id не совпадают");
        Assertions.assertEquals("subTask3name" , subTaskAfterUpdate.getTaskName(), "Некорректный subTask name");
        Assertions.assertEquals("subTask3description" , subTaskAfterUpdate.getTaskDescription(),
                "Некорректный subTask description");

        Assertions.assertEquals(epicId, subTaskAfterUpdate.getEpicId(), "epicId изменился");
        Assertions.assertTrue(testEpic.getSubTasksIds().contains(subTaskId), "subTaskId на нейден в эпике");
    }

    @Test
    public void epicStatusChangingCorrectly() {
        manager.addEpic(epic1);
        Epic testEpic = manager.getEpic(epic1.getId());

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
        final int taskId = task1.getId();

        Task foundTask = manager.getTask(taskId);

        Assertions.assertEquals(task1, foundTask, "Задача не найдена");
    }

    @Test
    public void findEpicById() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());

        final int epicId = epic1.getId();

        Epic foundEpic = manager.getEpic(epicId);

        Assertions.assertEquals(epic1, foundEpic, "Эпик не найден");
    }

    @Test
    public void findSubTaskById() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());

        final int subTaskId = subTask1Epic1.getId();

        SubTask foundSubTask = manager.getSubTask(subTaskId);

        Assertions.assertEquals(subTask1Epic1, foundSubTask, "Подзадача не найдена");
    }

    @Test public void removeTaskById() {
        manager.addTask(task1);
        manager.addTask(task2);

        final int taskId = task1.getId();
        Task testTask = manager.getTask(taskId);

        manager.removeTask(taskId);

        Assertions.assertFalse(manager.getTasksList().contains(testTask), "Задача не удалена");
        Assertions.assertNull(manager.getTask(taskId), "Задача не удалена");
    }

    @Test
    public void removeSubTaskByIdAndRemoveIdFromEpicSubTaskIds() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1Epic1, epic1.getId());

        final int subTaskId = subTask1Epic1.getId();
        manager.removeSubTask(subTaskId);

        Epic testEpic = manager.getEpic(epic1.getId());

        Assertions.assertFalse(testEpic.getSubTasksIds().contains(subTaskId), "ID подзадачи не удален из эпика");
    }

    @Test
    public void removeEpicByIdWithSubTasks() {
        manager.addEpic(epic1);
        final int epicId = epic1.getId();

        manager.addSubTask(subTask1Epic1, epicId);
        manager.addSubTask(subTask2Epic1, epicId);

        Epic testEpic = new Epic(manager.getEpic(epicId));
        SubTask testSubTask1 = new SubTask(manager.getSubTask(subTask1Epic1.getId()));
        SubTask testSubTask2 = new SubTask(manager.getSubTask(subTask2Epic1.getId()));

        manager.removeEpic(epicId);

        Assertions.assertFalse(manager.getEpicsList().contains(testEpic), "Эпик не удален");
        Assertions.assertFalse(manager.getSubTasksList().contains(testSubTask1), "Подзадача не удалена");
        Assertions.assertFalse(manager.getSubTasksList().contains(testSubTask2), "Подзадача не удалена");
    }

    @Test
    public void ifTasksListIsClearedItShouldNotHaveAnyValues() {
        final Task task = new Task("A", "B", TaskStatus.NEW);
        final Task task2 = new Task("C", "D", TaskStatus.NEW);

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

        Epic testEpic1 = manager.getEpic(epic1.getId());
        Epic testEpic2 = manager.getEpic(epic2.getId());

        Assertions.assertTrue(testEpic1.getSubTasksIds().isEmpty(), "Список подзадач не пуст");
        Assertions.assertTrue(testEpic2.getSubTasksIds().isEmpty(), "Список подзадач не пуст");
    }
}
