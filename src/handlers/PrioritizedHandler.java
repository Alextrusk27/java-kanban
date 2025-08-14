package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] uri = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET")) {
            prioritizedGetHandler(exchange, uri);
        } else {
            sendResponse(exchange, "Неизвестный метод", 405);
        }
    }

    private void prioritizedGetHandler(HttpExchange exchange, String[] uri) throws IOException {
        if (uri.length == 2) {
            List<Task> prioritized = taskManager.getPrioritizedTasks();
            if (prioritized.isEmpty()) {
                sendResponse(exchange, "Нет приоритетных задач", 200);
            } else {
                sendResponse(exchange, gson.toJson(prioritized), 200);
            }
        } else {
            sendResponse(exchange, "Ошибка запроса", 404);
        }
    }
}
