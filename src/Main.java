import TaskManager.*;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Купить овощи", "Лук, морковь, свекла", TaskStatus.NEW);

        Epic epic1 = new Epic("Слетать на Луну", "Выполнить все шаги по порядку");
        Epic epic2 = new Epic("Слетать на Марс", "Почему бы не построить новую колонию?");

        SubTask subTask1 = new SubTask("Построить ракету", "Собрать материалы и построить", TaskStatus.DONE);
        SubTask subTask2 = new SubTask("Пройти подготовку", "Где вообще это делать?", TaskStatus.NEW);
        SubTask subTask3 = new SubTask("Обновить ВУ", "Без прав управлить ракетой нельзя", TaskStatus.NEW);
        SubTask subTask4 = new SubTask("Осуществить полет", "Слетать туда и обратно", TaskStatus.NEW);

        Task task2 = new Task("Сделать уборку", "Пропылесосить и протереть пыль", TaskStatus.IN_PROGRESS);

        taskManager.setEpic(epic1);
        taskManager.setSubTask(subTask1, epic1.getId());
        taskManager.setSubTask(subTask2, epic1.getId());

        System.out.println(taskManager.getEpic(epic1.getId()));
        System.out.println(taskManager.getSubTasksList());

        taskManager.updateEpic(epic2, epic1.getId());

        System.out.println();
        System.out.println(taskManager.getEpic(epic2.getId()));
        System.out.println(taskManager.getSubTasksList());







    }
}
