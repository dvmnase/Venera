package condorcet.Models.TCP;

public class Response {
    private String status;
    private String message; // Добавленное поле для детализированного сообщения

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
}
