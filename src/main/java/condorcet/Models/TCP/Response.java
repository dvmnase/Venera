package condorcet.Models.TCP;

public class Response {
    private String status;
    private String message; // Добавленное поле для детализированного сообщения
    private String role;
    private int userId; // Новое поле для ID пользователя

    private int appointmentId;

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

    public int getUserId() { // Геттер для userId
        return userId;
    }

    public void setUserId(int userId) { // Сеттер для userId
        this.userId = userId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
}
