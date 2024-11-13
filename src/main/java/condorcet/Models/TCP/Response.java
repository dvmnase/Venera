package condorcet.Models.TCP;

public class Response {
    private String status;
    private String message; // Добавленное поле для детализированного сообщения

    private String role;

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
        return role; // Метод для получения роли
    }

    public void setRole(String role) { // Метод для установки роли
        this.role = role;
    }
}
