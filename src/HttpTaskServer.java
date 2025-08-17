import handlers.*;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        server.createContext("/tasks", new TasksHandler(taskManager));
        server.createContext("/subtasks", new SubTasksHandler(taskManager));
        server.createContext("/epics", new EpicsHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void startServer() {
        server.start();
    }

    public void stopServer() {
        this.server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.startServer();
    }
}
