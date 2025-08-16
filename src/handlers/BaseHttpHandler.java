package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import json.LocalDateAdapter;
import managers.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .create();
    }

    protected void sendResponse(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}
