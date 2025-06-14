import enums.TaskStatus;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("Первая задача", "Описание первой задачи", TaskStatus.NEW);
        Task task2 = new Task("Вторая задача", "Описание второй задачи", TaskStatus.IN_PROGRESS);
        Epic epicWithSubs = new Epic("Эпик с подзадачами", "Описание эпика с подзадачами");
        SubTask subTask1 = new SubTask("Первая подзадача", "Описание первой задачи", TaskStatus.IN_PROGRESS);
        SubTask subTask2 = new SubTask("Вторая подзадача", "Описание второй задачи", TaskStatus.DONE);
        SubTask subTask3 = new SubTask("Третья подзадача", "Описание третьей задачи", TaskStatus.NEW);
        Epic epicNoSubs = new Epic("Эпик без подзадач", "Описание эпика без подзадач");

        TaskManager taskManager = Managers.getDefault();

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epicWithSubs);
        taskManager.addSubTask(subTask1, epicWithSubs.getId());
        taskManager.addSubTask(subTask2, epicWithSubs.getId());
        taskManager.addSubTask(subTask3, epicWithSubs.getId());
        taskManager.addEpic(epicNoSubs);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task1.getId());
        taskManager.getSubTask(subTask3.getId());
        taskManager.getSubTask(subTask1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getEpic(epicNoSubs.getId());
        taskManager.getSubTask(subTask2.getId());
        taskManager.getEpic(epicWithSubs.getId());

        printAllTasks(taskManager);
        System.out.println("=== Удалили первую задачу ===");
        taskManager.removeTask(task1.getId());
        printAllTasks(taskManager);

        System.out.println("=== Удалили эпик с тремя подзадачами ===");
        taskManager.removeEpic(epicWithSubs.getId());
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
//        System.out.println("Задачи:");
//        for (Task task : manager.getTasksList()) {
//            System.out.println(task);
//        }
//        System.out.println("Эпики:");
//        for (Task epic : manager.getEpicsList()) {
//            System.out.println(epic);
//
//            for (Task task : manager.getSubTasksListByEpic(epic.getId())) {
//                System.out.println("--> " + task);
//            }
//        }
//        System.out.println("Подзадачи:");
//        for (Task subtask : manager.getSubTasksList()) {
//            System.out.println(subtask);
//        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
