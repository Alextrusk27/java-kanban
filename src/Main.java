import enums.TaskStatus;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Купить овощи", "Лук, морковь, свекла", TaskStatus.NEW);

        Epic epic1 = new Epic("Слетать на Луну", "Выполнить все шаги по порядку");
        Epic epic2 = new Epic("Слетать на Марс", "Почему бы не построить новую колонию?");

        SubTask subTask1 = new SubTask("Построить ракету", "Собрать материалы и построить", TaskStatus.DONE);
        SubTask subTask2 = new SubTask("Пройти подготовку", "Где вообще это делать?", TaskStatus.NEW);
        SubTask subTask3 = new SubTask("Обновить ВУ", "Без прав управлять ракетой нельзя", TaskStatus.NEW);
        SubTask subTask4 = new SubTask("Осуществить полет", "Слетать туда и обратно", TaskStatus.NEW);

        Task task2 = new Task("Сделать уборку", "Пропылесосить и протереть пыль", TaskStatus.IN_PROGRESS);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubTask(subTask1, epic1.getId());
        manager.addSubTask(subTask2, epic2.getId());



    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasksList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpicsList()) {
            System.out.println(epic);

            for (Task task : manager.getSubTasksListByEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasksList()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
