package musichub.shared;

public class ServerResponse<T> {
    private boolean success;
    private String message;
    private T data;

    private ServerResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ServerResponse<T> ok(String message, T data) {
        return new ServerResponse<>(true, message, data);
    }

    public static <T> ServerResponse<T> error(String message) {
        return new ServerResponse<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}