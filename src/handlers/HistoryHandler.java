package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] uri = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET")) {
            historyGetHandler(exchange, uri);
        } else {
            sendResponse(exchange, "Неизвестный метод", 405);
        }
    }

    private void historyGetHandler(HttpExchange exchange, String[] uri) throws IOException {
        if (uri.length == 2) {
            List<Task> history = taskManager.getHistory();
            if (history.isEmpty()) {
                sendResponse(exchange, "История просмотров пуста", 200);
            } else {
                sendResponse(exchange, gson.toJson(history), 200);
            }
        } else {
            sendResponse(exchange, "Ошибка запроса", 404);
        }
    }
}
