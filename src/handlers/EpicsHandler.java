package handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.OverlapException;
import exceptions.TaskCreateException;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] uri = exchange.getRequestURI().getPath().split("/");
        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> epicsGetHandler(exchange, uri);
                case "POST" -> epicsPostHandler(exchange, uri);
                case "DELETE" -> epicsDeleteHandler(exchange, uri);
                default -> sendResponse(exchange, "Неизвестный метод", 405);
            }
        } catch (IOException | NumberFormatException | JsonSyntaxException e) {
            sendResponse(exchange, "Ошибка ввода: некорректный формат", 404);
        } catch (OverlapException | TaskCreateException e) {
            sendResponse(exchange, e.getMessage(), 406);
        }
    }

    private void epicsGetHandler(HttpExchange exchange, String[] uri) throws IOException {
        if (uri.length == 2) {
            List<Epic> epicsList = taskManager.getEpicsList();
            if (epicsList.isEmpty()) {
                sendResponse(exchange, "Список эпиков пуст", 200);
                return;
            }
            sendResponse(exchange, gson.toJson(epicsList), 200);
            return;
        }

        int taskID = Integer.parseInt(uri[2]);
        Optional<Epic> optional = taskManager.getEpic(taskID);
        if (optional.isEmpty()) {
            sendResponse(exchange, "Ошибка поиска: эпик не найден", 404);
            return;
        }

        switch (uri.length) {
            case 3:
                Epic epic = optional.get();
                sendResponse(exchange, gson.toJson(epic), 200);
                break;
            case 4:
                if (!uri[3].equals("subtasks")) {
                    sendResponse(exchange, "Неизвестный запрос", 404);
                    return;
                }
                List<SubTask> subTasks = taskManager.getSubTasksListByEpic(taskID);
                if (subTasks.isEmpty()) {
                    sendResponse(exchange, "У эпика ID " + taskID + " нет подзадач", 200);
                    return;
                }
                sendResponse(exchange, gson.toJson(subTasks), 200);
                break;
            default:
                sendResponse(exchange, "Ошибка поиска: неправильный формат ввода", 404);
        }
    }


    private void epicsPostHandler(HttpExchange exchange, String[] uri) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            if (uri.length == 2 && !body.isBlank()) {
                Epic epic = gson.fromJson(body, Epic.class);
                taskManager.addEpic(epic);
                sendResponse(exchange, "Эпик добавлен", 201);
            } else {
                sendResponse(exchange, "Ошибка создания эпика: неправильный формат ввода", 404);
            }
        }
    }

    private void epicsDeleteHandler(HttpExchange exchange, String[] uri) throws IOException {
        if (uri.length == 3) {
            int taskID = Integer.parseInt(uri[2]);
            Optional<Epic> optional = taskManager.getEpic(taskID);
            if (optional.isPresent()) {
                taskManager.removeEpic(taskID);
                sendResponse(exchange, "Эпик удален", 200);
            } else {
                sendResponse(exchange, "Эпик не найден", 404);
            }
        } else {
            sendResponse(exchange, "Ошибка удаления: неправильный формат ввода", 404);
        }
    }
}

