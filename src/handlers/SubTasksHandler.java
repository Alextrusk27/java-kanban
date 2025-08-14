package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.OverlapException;
import exceptions.TaskCreateException;
import managers.TaskManager;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubTasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] uri = exchange.getRequestURI().getPath().split("/");

        switch (exchange.getRequestMethod()) {
            case "GET" -> subTasksGetHandler(exchange, uri);
            case "POST" -> subTasksPostHandler(exchange, uri);
            case "DELETE" -> subTasksDeleteHandler(exchange, uri);
            default -> sendResponse(exchange, "Неизвестный метод", 405);
        }
    }

    private void subTasksGetHandler(HttpExchange exchange, String[] uri) throws IOException {
        Gson gson = createGson();
        switch (uri.length) {
            case 2:
                List<SubTask> subTasksList = taskManager.getSubTasksList();
                if (subTasksList.isEmpty()) {
                    sendResponse(exchange, "Список подзадач пуст", 200);
                    return;
                }
                sendResponse(exchange, gson.toJson(subTasksList), 200);
                break;
            case 3:
                try {
                    int taskID = Integer.parseInt(uri[2]);
                    Optional<SubTask> optional = taskManager.getSubTask(taskID);
                    if (optional.isPresent()) {
                        Task task = optional.get();
                        sendResponse(exchange, gson.toJson(task), 200);
                    } else {
                        sendResponse(exchange, "Ошибка поиска: подзадача не найдена", 404);
                    }
                } catch (NumberFormatException e) {
                    sendResponse(exchange, "Ошибка поиска: неверно указан ID подзадачи", 404);
                }
                break;
            default:
                sendResponse(exchange, "Ошибка поиска: неправильный формат ввода", 404);
        }
    }

    private void subTasksPostHandler(HttpExchange exchange, String[] uri) throws IOException {
        Gson gson = createGson();
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            SubTask subTask = gson.fromJson(body, SubTask.class);

            switch (uri.length) {
                case 2:
                    taskManager.addSubTask(subTask, subTask.getEpicId());
                    sendResponse(exchange, "Подзадача добавлена", 201);
                    break;
                case 3:
                    int taskID = Integer.parseInt(uri[2]);
                    Optional<SubTask> optional = taskManager.getSubTask(taskID);
                    if (optional.isPresent()) {
                        taskManager.updateSubTask(subTask, taskID);
                        sendResponse(exchange, "Подзадача обновлена", 201);
                    } else {
                        sendResponse(exchange, "Ошибка обновления: подзадача не найдена", 404);
                    }
                    break;
                default:
                    sendResponse(exchange, "Ошибка создания подзадачи: неправильный формат ввода", 404);
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, "Ошибка обновления: неверно указан ID подзадачи", 404);
        } catch (IOException | JsonSyntaxException e) {
            sendResponse(exchange, "Ошибка создания задачи: некорректный формат данных", 404);
        } catch (OverlapException | TaskCreateException e) {
            sendResponse(exchange, e.getMessage(), 406);
        }
    }

    private void subTasksDeleteHandler(HttpExchange exchange, String[] uri) throws IOException {
        switch (uri.length) {
            case 2:
                taskManager.removeAllSubTasks();
                sendResponse(exchange, "Все подзадачи удалены", 200);
                break;
            case 3:
                try {
                    int taskID = Integer.parseInt(uri[2]);
                    Optional<SubTask> optional = taskManager.getSubTask(taskID);
                    if (optional.isPresent()) {
                        taskManager.removeSubTask(taskID);
                        sendResponse(exchange, "Подзадача удалена", 200);
                    } else {
                        sendResponse(exchange, "Подзадача не найдена", 404);
                    }
                } catch (NumberFormatException e) {
                    sendResponse(exchange, "Ошибка поиска: неверно указан ID подзадачи", 404);
                }
            default:
                sendResponse(exchange, "Ошибка удаления: неправильный формат ввода", 404);
        }
    }
}
