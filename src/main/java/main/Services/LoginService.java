package main.Services;

import com.google.gson.Gson;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

public class LoginService {

    private Gson gson; // Gson для сериализации и десериализации

    public LoginService() {
        this.gson = new Gson(); // Инициализация Gson
    }

    public Response login(Request request) {
        Response response = null;
        ClientSocket clientSocket = ClientSocket.getInstance(); // Получаем экземпляр ClientSocket

        try {
            clientSocket.sendRequest(request); // Отправка запроса на сервер

            // Получаем ответ от сервера
            response = clientSocket.getResponse();
        } catch (Exception e) {
            // Логирование ошибки или сообщение пользователю
            response = new Response();
            response.setMessage("Ошибка соединения с сервером: " + e.getMessage());
        }

        return response; // Возвращаем ответ
    }

    public void closeConnection() {
        ClientSocket.getInstance().close(); // Закрытие соединения при необходимости
    }
}