package main.Models.TCP;

public class Response {
    private String status;
    private String message;
    private String role;
    private int userId;  // Новое поле для хранения ID пользователя

    // Конструктор, если необходимо
    public Response() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Дополнительный конструктор с userId, если это нужно
    public Response(String status, String message, String role, int userId) {
        this.status = status;
        this.message = message;
        this.role = role;
        this.userId = userId;
    }
}
