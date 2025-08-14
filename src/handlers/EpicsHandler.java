package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.OverlapException;
import exceptions.TaskCreateException;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] uri = exchange.getRequestURI().getPath().split("/");
        switch (exchange.getRequestMethod()) {
            case "GET" -> epicsGetHandler(exchange, uri);
            case "POST" -> epicsPostHandler(exchange, uri);
            case "DELETE" -> epicsDeleteHandler(exchange, uri);
            default -> sendResponse(exchange, "Неизвестный метод", 405);
        }
    }

    private void epicsGetHandler(HttpExchange exchange, String[] uri) throws IOException {
        Gson gson = createGson();
        try {
            if (uri.length == 2) {
                List<Epic> epicsList = taskManager.getEpicsList();
                if (epicsList.isEmpty()) {
                    sendResponse(exchange, "Список эпиков пуст", 200);
                    return;
                }
                sendResponse(exchange, gson.toJson(epicsList), 200);
            } else if (uri.length > 2) {
                int taskID = Integer.parseInt(uri[2]);
                Optional<Epic> optional = taskManager.getEpic(taskID);
                if (optional.isPresent()) {
                    if (uri.length == 3) {
                        Task task = optional.get();
                        sendResponse(exchange, gson.toJson(task), 200);
                    } else if (uri.length == 4 && uri[3].equals("subtasks")) {
                        List<SubTask> subTasks = taskManager.getSubTasksListByEpic(taskID);
                        if (subTasks.isEmpty()) {
                            sendResponse(exchange, "У эпика ID " + taskID + " нет подзадач", 200);
                        } else {
                            sendResponse(exchange, gson.toJson(subTasks), 200);
                        }
                    } else {
                        sendResponse(exchange, "Ошибка поиска: неизвестный запрос", 404);
                    }
                } else {
                    sendResponse(exchange, "Ошибка поиска: эпик не найден", 404);
                }
            } else {
                sendResponse(exchange, "Ошибка поиска: неправильный формат ввода", 404);
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, "Ошибка поиска: неверно указан ID эпика", 404);
        }
    }


    private void epicsPostHandler(HttpExchange exchange, String[] uri) throws IOException {
        Gson gson = createGson();
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(body, Epic.class);

            switch (uri.length) {
                case 2:
                    taskManager.addEpic(epic);
                    sendResponse(exchange, "Эпик добавлен", 201);
                    break;
                case 3:
                    int taskID = Integer.parseInt(uri[2]);
                    Optional<Epic> optional = taskManager.getEpic(taskID);
                    if (optional.isPresent()) {
                        taskManager.updateEpic(epic, taskID);
                        sendResponse(exchange, "Эпик обновлен", 201);
                    } else {
                        sendResponse(exchange, "Ошибка обновления: эпик не найден", 404);
                    }
                    break;
                default:
                    sendResponse(exchange, "Ошибка создания эпика: неправильный формат ввода", 404);
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, "Ошибка обновления: неверно указан ID эпика", 404);
        } catch (IOException | JsonSyntaxException e) {
            sendResponse(exchange, "Ошибка создания эпика: некорректный формат данных", 404);
        } catch (OverlapException | TaskCreateException e) {
            sendResponse(exchange, e.getMessage(), 406);
        }
    }

    private void epicsDeleteHandler(HttpExchange exchange, String[] uri) throws IOException {
        switch (uri.length) {
            case 2:
                taskManager.removeAllEpics();
                sendResponse(exchange, "Все эпики удалены", 200);
                break;
            case 3:
                try {
                    int taskID = Integer.parseInt(uri[2]);
                    Optional<Epic> optional = taskManager.getEpic(taskID);
                    if (optional.isPresent()) {
                        taskManager.removeEpic(taskID);
                        sendResponse(exchange, "Эпик удален", 200);
                    } else {
                        sendResponse(exchange, "Эпик не найден", 404);
                    }
                } catch (NumberFormatException e) {
                    sendResponse(exchange, "Ошибка поиска: неверно указан ID эпика", 404);
                }
                break;
            default:
                sendResponse(exchange, "Ошибка удаления: неправильный формат ввода", 404);
        }
    }
}
