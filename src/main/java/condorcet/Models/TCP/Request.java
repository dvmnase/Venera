package condorcet.Models.TCP;

import main.Enums.RequestType;

public class Request {
    private RequestType requestType;
    private String requestMessage;

    private int appointmentId;
    private int clientId; // Новое поле для хранения ID клиента

    public Request() {}

    public Request(RequestType requestType, String requestMessage, int clientId) {
        this.requestType = requestType;
        this.requestMessage = requestMessage;
        this.clientId = clientId;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public int getClientId() { // Метод для получения clientId
        return clientId;
    }

    public void setClientId(int clientId) { // Метод для установки clientId
        this.clientId = clientId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
}
