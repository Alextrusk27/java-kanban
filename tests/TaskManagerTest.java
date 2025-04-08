import enums.TaskStatus;
import managers.*;
import tasks.*;
import org.junit.jupiter.api.*;

import java.util.List;

public class TaskManagerTest {

    TaskManager manager;

    @BeforeEach
    public void newManager() {
        manager = Managers.getDefault();
    }

    @Test
    public void addNewTask() {
        Task task = new Task("A", "B", TaskStatus.NEW);

        manager.addTask(task);

        Task savedTask = manager.getTask(task.getId());
        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают.");

        Task task2 = new Task("C", "D", TaskStatus.NEW);
        manager.addTask(task2);

        final List<Task> tasks = manager.getTasksList();

        Assertions.assertNotNull(tasks, "Задачи не возвращаются.");
        Assertions.assertEquals(2, tasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(task2, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    public void newSubTuskAddWithCorrectIdInEpic() {
        Epic epic = new Epic("A", "B");
        SubTask subTask = new SubTask("C", "D", TaskStatus.NEW);

        manager.addEpic(epic);
        manager.addSubTask(subTask, epic.getId());

        Assertions.assertEquals(epic.getId(), subTask.getEpicId(), "В подзадаче некорректный EpicId");
        Assertions.assertTrue(epic.getSubTasksIds().contains(subTask.getId()), "В эпике нет SubTuskId");
    }

    @Test
    public void subTuskUpdateAndSaveCorrectIds() {
        Epic epic = new Epic("A", "B");
        SubTask subTask = new SubTask("C", "D", TaskStatus.NEW);
        SubTask subTaskNew = new SubTask("E", "F", TaskStatus.NEW);

        manager.addEpic(epic);
        manager.addSubTask(subTask, epic.getId());
        final int subTaskId = subTask.getId();
        final int epicId = subTask.getEpicId();
        manager.updateSubTask(subTaskNew, subTask.getId());

        Assertions.assertEquals(epicId, subTaskNew.getEpicId(), "epicId изменился");
        Assertions.assertTrue(epic.getSubTasksIds().contains(subTaskId), "SubTuskId был удален из эпика");
    }

    @Test
    public void epicStatusChangingCorrectly() {
        Epic epic = new Epic("A", "B");
        SubTask subTask = new SubTask("C", "D", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("E", "F", TaskStatus.IN_PROGRESS);
        SubTask subTask3 = new SubTask("G", "H", TaskStatus.DONE);

        manager.addEpic(epic);
        Assertions.assertEquals(TaskStatus.NEW, epic.getTaskStatus(), "Неверный статус при создании эпика");

        manager.addSubTask(subTask, epic.getId());
        Assertions.assertEquals(TaskStatus.NEW, epic.getTaskStatus(), "Неверный статус при добавлении NEW");

        manager.addSubTask(subTask2, epic.getId());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus(), "Неверный статус " +
                "при добавлении IN PROGRESS");

        manager.addSubTask(subTask3, epic.getId());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus(), "Неверный статус " +
                "при добавлении IN PROGRESS");

        SubTask subTask4 = new SubTask("C", "D", TaskStatus.DONE);
        SubTask subTask5 = new SubTask("E", "F", TaskStatus.DONE);
        manager.updateSubTask(subTask4, subTask.getId());
        manager.updateSubTask(subTask5, subTask2.getId());
        Assertions.assertEquals(TaskStatus.DONE, epic.getTaskStatus(), "Неверный статус все DONE");
    }

    @Test
    public void findTaskById() {
        Task task = new Task("A", "B", TaskStatus.NEW);
        manager.addTask(task);
        final int taskId = task.getId();

        Task foundTask = manager.getTask(taskId);

        Assertions.assertEquals(task, foundTask, "Задача не найдена");
    }

    @Test
    public void findEpicById() {
        Epic epic = new Epic("A", "B");
        manager.addEpic(epic);

        SubTask subTask = new SubTask("C", "D", TaskStatus.NEW);
        manager.addSubTask(subTask, epic.getId());

        final int epicId = epic.getId();

        Epic foundEpic = manager.getEpic(epicId);

        Assertions.assertEquals(epic, foundEpic, "Эпик не найден");
    }

    @Test
    public void findSubTuskById() {
        Epic epic = new Epic("A", "B");
        SubTask subTask = new SubTask("C", "D", TaskStatus.NEW);

        manager.addEpic(epic);
        manager.addSubTask(subTask, epic.getId());

        final int subTuskId = subTask.getId();

        SubTask foundSubTusk = manager.getSubTask(subTuskId);

        Assertions.assertEquals(subTask, foundSubTusk, "Подзадача не найдена");
    }

    @Test
    public void removeSubTuskByIdAndRemoveIdFromEpicSubTaskIds() {
        Epic epic = new Epic("A", "B");
        SubTask subTask = new SubTask("C", "D", TaskStatus.NEW);
        manager.addEpic(epic);
        manager.addSubTask(subTask, epic.getId());

        final int subTaskId = subTask.getId();

        manager.removeSubTask(subTaskId);
        Assertions.assertFalse(epic.getSubTasksIds().contains(subTaskId), "ID подзадачи не удален из эпика");
    }

    @Test
    public void removeEpicWithSubTusks() {
        Epic epic = new Epic("A", "B");
        SubTask subTask1 = new SubTask("C", "D", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("E", "F", TaskStatus.NEW);
        manager.addEpic(epic);
        manager.addSubTask(subTask1, epic.getId());
        manager.addSubTask(subTask2, epic.getId());

        final int epicId = epic.getId();
        final int subTusk1Id = subTask1.getId();
        final int subTusk2Id = subTask2.getId();

      //  manager.removeEpic(epicId);

        Assertions.assertNull(manager.getEpic(epicId),"Эпик не удален");


    }
}
