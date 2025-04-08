import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;

class EpicTest {

        Epic epic = new Epic("Epic name", "Epic description");


    @Test
    void WhenEpicCreatedTaskNameMustBeEqual () {
        Assertions.assertEquals("Epic name", epic.getTaskName(), "Некорректное название эпика");
    }

    @Test
    void WhenEpicCreatedTaskDescriptionMustBeEqual () {
        Assertions.assertEquals("Epic name", epic.getTaskName(), "Некорректное описание эпика");
    }


}