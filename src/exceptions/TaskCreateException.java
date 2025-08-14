package exceptions;

public class TaskCreateException extends RuntimeException {

    public TaskCreateException(String message) {
        super(message);
    }
}
