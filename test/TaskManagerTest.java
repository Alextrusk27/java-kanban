import enums.TaskStatus;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import tasks.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected TaskManager manager;

    protected Task task1;
    protected Task task2;

    protected Epic epic1;
    protected Epic epic2;

    protected SubTask subTask1Epic1;
    protected SubTask subTask2Epic1;
    protected SubTask subTask3Epic2;


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
}
