package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyViews = new HashMap<>();
    private Node lastNode;
    private Node firstNode;

    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            int taskId = task.getId();
            Node newNode = new Node(task);

            if (historyViews.containsKey(taskId)) {
                remove(taskId);
            }
            if (historyViews.isEmpty()) {
                firstNode = newNode;
            } else {
                lastNode.head = newNode;
                newNode.tail = lastNode;
            }
            lastNode = newNode;

            historyViews.put(taskId, newNode);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new LinkedList<>();
        Node node = firstNode;

        while (node != null) {
            result.add(node.task);
            node = node.head;
        }
        return result;
    }

    @Override
    public void remove(int id) {

        if (!historyViews.containsKey(id)) {
            return;
        }
        unlinkNode(historyViews.get(id));

        if (firstNode.equals(historyViews.get(id))) {
            firstNode = firstNode.head;
        }
        historyViews.remove(id);

        if (historyViews.size() == 1) {
            firstNode = lastNode;
        }
    }

    private void unlinkNode(Node node) {
        Node tail = node.tail;
        Node head = node.head;

        if (tail != null) {
            tail.head = head;
        }

        if (head != null) {
            head.tail = tail;
        }
    }

    private static class Node {
        final Task task;
        Node tail;
        Node head;

        public Node(Task task) {
            this.task = task;
            this.tail = null;
            this.head = null;
        }
    }
}



















