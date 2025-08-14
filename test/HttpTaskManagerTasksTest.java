import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import enums.TaskStatus;
import json.LocalDateAdapter;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    private final TaskManager manager = Managers.getDefault();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .create();
    private final HttpClient client = HttpClient.newHttpClient();


    private Task task;
    private Epic epic;
    private SubTask subTask;

    private Task task2;
    private Epic epic2;
    private SubTask subTask2;

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.removeAllTasks();
        manager.removeAllSubTasks();
        manager.removeAllEpics();
        taskServer.startServer();

        task = new Task("Task_Name", "Task_Description", TaskStatus.NEW,
                LocalDateTime.of(2025, 8, 10, 14, 30), 60);
        epic = new Epic("Epic_Name", "Epic_Description");
        subTask = new SubTask("SubTask_Name", "SubTask_Description", TaskStatus.NEW,
                LocalDateTime.of(2025, 8, 11, 13, 20), 30);

        task2 = new Task("Task2_Name", "Task2_Description", TaskStatus.NEW,
                LocalDateTime.of(2025, 8, 10, 17, 30), 15);
        epic2 = new Epic("Epic2_Name", "Epic2_Description");
        subTask2 = new SubTask("SubTask2_Name", "SubTask2_Description", TaskStatus.NEW,
                LocalDateTime.of(2025, 8, 11, 20, 20), 45);
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    private HttpResponse<String> post(String json, URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> delete(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private int addEpicAndGetItsID() throws IOException, InterruptedException {
        String epicToJson = gson.toJson(epic);
        HttpResponse<String> responseAddEpic = post(epicToJson, URI.create("http://localhost:8080/epics"));
        HttpResponse<String> responseGetEpic = get(URI.create("http://localhost:8080/epics"));
        Type listType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epics = gson.fromJson(responseGetEpic.body(), listType);
        return epics.getFirst().getId();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        String taskToJson = gson.toJson(task);
        HttpResponse<String> response = post(taskToJson, URI.create("http://localhost:8080/tasks"));
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = manager.getTasksList();

        assertNotNull(tasksFromManager, "Задача не добавилась");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task_Name", tasksFromManager.getFirst().getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskList() throws IOException, InterruptedException {
        String taskToJson = gson.toJson(task);
        HttpResponse<String> t1Response = post(taskToJson, URI.create("http://localhost:8080/tasks"));
        assertEquals(201, t1Response.statusCode());

        String task2ToJson = gson.toJson(task2);
        HttpResponse<String> t2Response = post(task2ToJson, URI.create("http://localhost:8080/tasks"));
        assertEquals(201, t2Response.statusCode());

        HttpResponse<String> response = get(URI.create("http://localhost:8080/tasks"));
        assertEquals(200, response.statusCode());
        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), listType);

        Optional<Task> taskToCompare = tasks.stream()
                .filter(task1 -> task1.getTaskName().equals("Task_Name"))
                .findAny();

        assertTrue(taskToCompare.isPresent(), "Задача не найдена");
        assertEquals(2, tasks.size(), "Количество задач не совпадает");
        assertEquals("Task_Description", taskToCompare.get().getTaskDescription());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        String taskToJson = gson.toJson(task);
        HttpResponse<String> t1Response = post(taskToJson, URI.create("http://localhost:8080/tasks"));
        assertEquals(201, t1Response.statusCode());

        HttpResponse<String> resp = get(URI.create("http://localhost:8080/tasks"));
        assertEquals(200, resp.statusCode());
        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(resp.body(), listType);

        int taskId = tasks.getFirst().getId();

        HttpResponse<String> response = get(URI.create("http://localhost:8080/tasks/" + taskId));
        assertEquals(200, response.statusCode());
        Task testTask = gson.fromJson(response.body(), Task.class);

        assertEquals("Task_Name", testTask.getTaskName());
        assertEquals("Task_Description", testTask.getTaskDescription());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        String taskToJson = gson.toJson(task);
        HttpResponse<String> addResponse = post(taskToJson, URI.create("http://localhost:8080/tasks"));
        assertEquals(201, addResponse.statusCode());

        HttpResponse<String> resp = get(URI.create("http://localhost:8080/tasks"));
        assertEquals(200, resp.statusCode());
        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(resp.body(), listType);

        int taskId = tasks.getFirst().getId();

        String task2ToJson = gson.toJson(task2);
        HttpResponse<String> response = post(task2ToJson, URI.create("http://localhost:8080/tasks/" + taskId));
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = manager.getTasksList();

        assertNotNull(tasksFromManager, "Задача не добавилась");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task2_Name", tasksFromManager.getFirst().getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        String taskToJson = gson.toJson(task);
        HttpResponse<String> addResponse = post(taskToJson, URI.create("http://localhost:8080/tasks"));
        assertEquals(201, addResponse.statusCode());

        HttpResponse<String> getResponse = get(URI.create("http://localhost:8080/tasks"));
        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(getResponse.body(), listType);
        int taskId = tasks.getFirst().getId();

        HttpResponse<String> response = delete(URI.create("http://localhost:8080/tasks/" + taskId));
        assertEquals(200, response.statusCode());

        assertTrue(manager.getTasksList().isEmpty(), "Список задач не пуст");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        String epicToJson = gson.toJson(epic);
        HttpResponse<String> response = post(epicToJson, URI.create("http://localhost:8080/epics"));
        assertEquals(201, response.statusCode());
        List<Epic> epicsFromManager = manager.getEpicsList();

        assertNotNull(epicsFromManager, "Эпик не добавился");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic_Name", epicsFromManager.getFirst().getTaskName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetEpicList() throws IOException, InterruptedException {
        String epicToJson = gson.toJson(epic);
        HttpResponse<String> t1Response = post(epicToJson, URI.create("http://localhost:8080/epics"));
        assertEquals(201, t1Response.statusCode());

        String epic2ToJson = gson.toJson(epic2);
        HttpResponse<String> t2Response = post(epic2ToJson, URI.create("http://localhost:8080/epics"));
        assertEquals(201, t2Response.statusCode());

        HttpResponse<String> response = get(URI.create("http://localhost:8080/epics"));
        assertEquals(200, response.statusCode());
        Type listType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epics = gson.fromJson(response.body(), listType);

        Optional<Epic> epicToCompare = epics.stream()
                .filter(epic1 -> epic1.getTaskName().equals("Epic_Name"))
                .findAny();

        assertTrue(epicToCompare.isPresent(), "Эпик не найден");
        assertEquals(2, epics.size(), "Количество эпиков не совпадает");
        assertEquals("Epic_Description", epicToCompare.get().getTaskDescription());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        String epicToJson = gson.toJson(epic);
        HttpResponse<String> t1Response = post(epicToJson, URI.create("http://localhost:8080/epics"));
        assertEquals(201, t1Response.statusCode());

        HttpResponse<String> resp = get(URI.create("http://localhost:8080/epics"));
        assertEquals(200, resp.statusCode());
        Type listType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Task> epics = gson.fromJson(resp.body(), listType);

        int epicId = epics.getFirst().getId();

        HttpResponse<String> response = get(URI.create("http://localhost:8080/epics/" + epicId));
        assertEquals(200, response.statusCode());
        Epic testEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals("Epic_Name", testEpic.getTaskName());
        assertEquals("Epic_Description", testEpic.getTaskDescription());
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        String epicToJson = gson.toJson(task);
        HttpResponse<String> addResponse = post(epicToJson, URI.create("http://localhost:8080/epics"));
        assertEquals(201, addResponse.statusCode());

        HttpResponse<String> resp = get(URI.create("http://localhost:8080/epics"));
        assertEquals(200, resp.statusCode());
        Type listType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epics = gson.fromJson(resp.body(), listType);

        int epicId = epics.getFirst().getId();

        String epic2ToJson = gson.toJson(epic2);
        HttpResponse<String> response = post(epic2ToJson, URI.create("http://localhost:8080/epics/" + epicId));
        assertEquals(201, response.statusCode());
        List<Epic> epicsFromManager = manager.getEpicsList();

        assertNotNull(epicsFromManager, "Эпик не добавился");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic2_Name", epicsFromManager.getFirst().getTaskName(), "Некорректное имя эпика");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        String epicToJson = gson.toJson(epic);
        HttpResponse<String> addResponse = post(epicToJson, URI.create("http://localhost:8080/epics"));
        assertEquals(201, addResponse.statusCode());

        HttpResponse<String> getResponse = get(URI.create("http://localhost:8080/epics"));
        Type listType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epics = gson.fromJson(getResponse.body(), listType);
        int epicId = epics.getFirst().getId();

        HttpResponse<String> response = delete(URI.create("http://localhost:8080/epics/" + epicId));
        assertEquals(200, response.statusCode());

        assertTrue(manager.getEpicsList().isEmpty(), "Список эпиков не пуст");
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        String epicToJson = gson.toJson(epic);
        HttpResponse<String> responseAddEpic = post(epicToJson, URI.create("http://localhost:8080/epics"));
        assertEquals(201, responseAddEpic.statusCode());

        HttpResponse<String> responseGetEpic = get(URI.create("http://localhost:8080/epics"));
        Type listType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epics = gson.fromJson(responseGetEpic.body(), listType);
        int epicId = epics.getFirst().getId();

        subTask.setEpicId(epicId);
        String subTaskToJson = gson.toJson(subTask);
        HttpResponse<String> response = post(subTaskToJson, URI.create("http://localhost:8080/subtasks"));
        assertEquals(201, response.statusCode());

        List<SubTask> subTasksFromManager = manager.getSubTasksList();

        assertNotNull(subTasksFromManager, "Подзадача не добавилась");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("SubTask_Name", subTasksFromManager.getFirst().getTaskName(),
                "Некорректное имя подзадачи");
    }

    @Test
    public void testGetSubtasksByEpicId() throws IOException, InterruptedException {
        int epicId = addEpicAndGetItsID();

        subTask.setEpicId(epicId);
        String subTaskToJson = gson.toJson(subTask);
        HttpResponse<String> response1 = post(subTaskToJson, URI.create("http://localhost:8080/subtasks"));
        assertEquals(201, response1.statusCode());

        subTask2.setEpicId(epicId);
        String subTask2ToJson = gson.toJson(subTask2);
        HttpResponse<String> response2 = post(subTask2ToJson, URI.create("http://localhost:8080/subtasks"));
        assertEquals(201, response2.statusCode());

        HttpResponse<String> response = get(URI.create("http://localhost:8080/epics/" + epicId + "/subtasks"));
        Type listType = new TypeToken<List<SubTask>>() {
        }.getType();
        List<SubTask> subTasks = gson.fromJson(response.body(), listType);
        Optional<SubTask> subTaskToCompare = subTasks.stream()
                .filter(subTask1 -> subTask1.getTaskName().equals("SubTask_Name"))
                .findAny();

        assertEquals(2, subTasks.size(), "Количество подзадач не совпадает");
        assertTrue(subTaskToCompare.isPresent(), "Подзадача не найдена");
        assertEquals("SubTask_Description", subTaskToCompare.get().getTaskDescription(),
                "Подзадачи не совпадают");
    }

    @Test
    public void testGetSubTaskList() throws IOException, InterruptedException {
        int epicId = addEpicAndGetItsID();

        subTask.setEpicId(epicId);
        String subTaskToJson = gson.toJson(subTask);
        HttpResponse<String> response1 = post(subTaskToJson, URI.create("http://localhost:8080/subtasks"));
        assertEquals(201, response1.statusCode());

        subTask2.setEpicId(epicId);
        String subTask2ToJson = gson.toJson(subTask2);
        HttpResponse<String> response2 = post(subTask2ToJson, URI.create("http://localhost:8080/subtasks"));
        assertEquals(201, response2.statusCode());

        HttpResponse<String> response = get(URI.create("http://localhost:8080/subtasks"));
        Type listType = new TypeToken<List<SubTask>>() {
        }.getType();
        List<SubTask> subTasks = gson.fromJson(response.body(), listType);
        Optional<SubTask> subTaskToCompare = subTasks.stream()
                .filter(subTask1 -> subTask1.getTaskName().equals("SubTask2_Name"))
                .findAny();

        assertTrue(subTaskToCompare.isPresent(), "Подзадача не найдена");
        assertEquals(2, subTasks.size(), "Количество подзадач не совпадает");
        assertEquals("SubTask2_Description", subTaskToCompare.get().getTaskDescription());
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        int epicId = addEpicAndGetItsID();

        subTask.setEpicId(epicId);
        String subTaskToJson = gson.toJson(subTask);
        HttpResponse<String> response1 = post(subTaskToJson, URI.create("http://localhost:8080/subtasks"));
        assertEquals(201, response1.statusCode());

        HttpResponse<String> responseList = get(URI.create("http://localhost:8080/subtasks"));
        Type listType = new TypeToken<List<SubTask>>() {
        }.getType();
        List<SubTask> subTasks = gson.fromJson(responseList.body(), listType);
        int subTaskId = subTasks.getFirst().getId();

        HttpResponse<String> response = get(URI.create("http://localhost:8080/subtasks/" + subTaskId));
        SubTask testSubTask = gson.fromJson(response.body(), SubTask.class);

        assertEquals("SubTask_Name", testSubTask.getTaskName());
        assertEquals("SubTask_Description", testSubTask.getTaskDescription());
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        int epicId = addEpicAndGetItsID();

        subTask.setEpicId(epicId);
        String subTaskToJson = gson.toJson(subTask);
        HttpResponse<String> response1 = post(subTaskToJson, URI.create("http://localhost:8080/subtasks"));
        assertEquals(201, response1.statusCode());

        HttpResponse<String> responseList = get(URI.create("http://localhost:8080/subtasks"));
        assertEquals(200, responseList.statusCode());
        Type listType = new TypeToken<List<SubTask>>() {
        }.getType();
        List<SubTask> subTasks = gson.fromJson(responseList.body(), listType);
        int subTaskId = subTasks.getFirst().getId();

        String subTask2ToJson = gson.toJson(subTask2);
        HttpResponse<String> responseUpdate = post(subTask2ToJson, URI.create("http://localhost:8080/subtasks/" +
                subTaskId));
        assertEquals(201, responseUpdate.statusCode());

        HttpResponse<String> response = get(URI.create("http://localhost:8080/subtasks/" + subTaskId));
        assertEquals(200, response.statusCode());
        SubTask testSubTask = gson.fromJson(response.body(), SubTask.class);

        assertEquals("SubTask2_Name", testSubTask.getTaskName());
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        int epicId = addEpicAndGetItsID();

        subTask.setEpicId(epicId);
        String subTaskToJson = gson.toJson(subTask);
        HttpResponse<String> response1 = post(subTaskToJson, URI.create("http://localhost:8080/subtasks"));
        assertEquals(201, response1.statusCode());

        HttpResponse<String> responseList = get(URI.create("http://localhost:8080/subtasks"));
        assertEquals(200, responseList.statusCode());
        Type listType = new TypeToken<List<SubTask>>() {
        }.getType();
        List<SubTask> subTasks = gson.fromJson(responseList.body(), listType);
        int subTaskId = subTasks.getFirst().getId();

        HttpResponse<String> response = delete(URI.create("http://localhost:8080/subtasks/" + subTaskId));
        assertEquals(200, response.statusCode());

        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        String taskToJson = gson.toJson(task);
        HttpResponse<String> addTaskResponse = post(taskToJson, URI.create("http://localhost:8080/tasks"));
        assertEquals(201, addTaskResponse.statusCode());

        HttpResponse<String> getTaskListResponse = get(URI.create("http://localhost:8080/tasks"));
        assertEquals(200, getTaskListResponse.statusCode());
        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(getTaskListResponse.body(), listType);
        int taskId = tasks.getFirst().getId();

        int epicId = addEpicAndGetItsID();
        subTask.setEpicId(epicId);
        String subTaskToJson = gson.toJson(subTask);
        HttpResponse<String> addSubTaskResponse = post(subTaskToJson, URI.create("http://localhost:8080/subtasks"));
        assertEquals(201, addSubTaskResponse.statusCode());

        HttpResponse<String> getSubTaskListResponse = get(URI.create("http://localhost:8080/subtasks"));
        assertEquals(200, getSubTaskListResponse.statusCode());
        Type listType1 = new TypeToken<List<SubTask>>() {
        }.getType();
        List<SubTask> subTasks1 = gson.fromJson(getSubTaskListResponse.body(), listType1);

        int subTaskId = subTasks1.getFirst().getId();

        HttpResponse<String> getSubTaskResponse = get(URI.create("http://localhost:8080/subtasks/" + subTaskId));
        HttpResponse<String> getTaskResponse = get(URI.create("http://localhost:8080/tasks/" + taskId));
        HttpResponse<String> getEpicResponse = get(URI.create("http://localhost:8080/epics/" + epicId));
        assertEquals(200, getSubTaskResponse.statusCode());
        assertEquals(200, getTaskResponse.statusCode());
        assertEquals(200, getEpicResponse.statusCode());

        HttpResponse<String> response = get(URI.create("http://localhost:8080/history"));
        assertEquals(200, response.statusCode());
        Type listType2 = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(response.body(), listType2);

        assertEquals(4, history.size(), "Размер списка не соответствует");
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        int epicId = addEpicAndGetItsID();
        subTask.setEpicId(epicId);
        String subTaskToJson = gson.toJson(subTask);
        HttpResponse<String> addSubTaskResponse = post(subTaskToJson, URI.create("http://localhost:8080/subtasks"));

        String taskToJson = gson.toJson(task);
        HttpResponse<String> addTaskResponse = post(taskToJson, URI.create("http://localhost:8080/tasks"));
        assertEquals(201, addTaskResponse.statusCode());

        HttpResponse<String> response = get(URI.create("http://localhost:8080/prioritized"));
        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> prioritizedList = gson.fromJson(response.body(), listType);

        assertEquals(2, prioritizedList.size(), "Размер списка не соответствует");
        assertEquals("Task_Name", prioritizedList.getFirst().getTaskName(), "Нарушена приоритетность");
    }
}
