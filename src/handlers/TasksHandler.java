package handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.OverlapException;
import exceptions.TaskCreateException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;


public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] uri = exchange.getRequestURI().getPath().split("/");
        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> tasksGetHandler(exchange, uri);
                case "POST" -> tasksPostHandler(exchange, uri);
                case "DELETE" -> tasksDeleteHandler(exchange, uri);
                default -> sendResponse(exchange, "Неизвестный метод", 405);
            }
        } catch (IOException | NumberFormatException | JsonSyntaxException e) {
            sendResponse(exchange, "Ошибка ввода: некорректный формат", 404);
        } catch (OverlapException | TaskCreateException e) {
            sendResponse(exchange, e.getMessage(), 406);
        }
    }

    private void tasksGetHandler(HttpExchange exchange, String[] uri) throws IOException {
        switch (uri.length) {
            case 2:
                List<Task> tasksList = taskManager.getTasksList();
                if (tasksList.isEmpty()) {
                    sendResponse(exchange, "Список задач пуст", 200);
                    return;
                }
                sendResponse(exchange, gson.toJson(tasksList), 200);
                break;
            case 3:
                    int taskID = Integer.parseInt(uri[2]);
                    Optional<Task> optionalTask = taskManager.getTask(taskID);
                    if (optionalTask.isPresent()) {
                        Task task = optionalTask.get();
                        sendResponse(exchange, gson.toJson(task), 200);
                    } else {
                        sendResponse(exchange, "Ошибка поиска: задача не найдена", 404);
                    }
                break;
            default:
                sendResponse(exchange, "Ошибка поиска: неправильный формат ввода", 404);
        }
    }

    private void tasksPostHandler(HttpExchange exchange, String[] uri) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isBlank()) {
                sendResponse(exchange, "Ошибка создания задачи: неправильный формат ввода", 404);
                return;
            }
            Task task = gson.fromJson(body, Task.class);

            switch (uri.length) {
                case 2:
                    taskManager.addTask(task);
                    sendResponse(exchange, "Задача добавлена", 201);
                    break;
                case 3:
                    int taskID = Integer.parseInt(uri[2]);
                    Optional<Task> optionalTask = taskManager.getTask(taskID);
                    if (optionalTask.isPresent()) {
                        taskManager.updateTask(task, taskID);
                        sendResponse(exchange, "Задача обновлена", 201);
                    } else {
                        sendResponse(exchange, "Ошибка обновления: задача не найдена", 404);
                    }
                    break;
                default:
                    sendResponse(exchange, "Ошибка создания задачи: неправильный формат ввода", 404);
            }
        }
    }

    private void tasksDeleteHandler(HttpExchange exchange, String[] uri) throws IOException {
        if (uri.length == 3) {
            int taskID = Integer.parseInt(uri[2]);
            Optional<Task> optionalTask = taskManager.getTask(taskID);
            if (optionalTask.isPresent()) {
                taskManager.removeTask(taskID);
                sendResponse(exchange, "Задача удалена", 200);
            } else {
                sendResponse(exchange, "Задача не найдена", 404);
            }
        } else {
            sendResponse(exchange, "Ошибка удаления: неправильный формат ввода", 404);
        }
    }
}
