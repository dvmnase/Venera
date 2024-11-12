package main.Services;

import com.google.gson.Gson;
import main.Controllers.ErrorDialogController;
import main.Controllers.WelcomeDialogController;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Enums.RequestType;
import main.Utility.ClientSocket;
import main.Models.Entities.User;

import java.io.BufferedReader;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class RegistrationService {

    public void registerUser(User user) {
        Gson gson = new Gson();

        // Создание объекта запроса
        Request request = new Request();
        request.setRequestType(RequestType.REGISTER);
        request.setRequestMessage(gson.toJson(user));

        try {
            // Отправка запроса на сервер
            ClientSocket.getInstance().getOut().println(gson.toJson(request));
            ClientSocket.getInstance().getOut().flush();

            // Ожидание и обработка ответа от сервера
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseJson = in.readLine();
            if (responseJson != null) {
                Response response = gson.fromJson(responseJson, Response.class);
                handleServerResponse(response);
            }
        } catch (IOException e) {
            ErrorDialogController.showErrorDialog("Нет подключения к серверу");

        }
    }

    // Метод для обработки ответа от сервера
    private void handleServerResponse(Response response) {
        if ("SUCCESS".equals(response.getStatus())) {
            WelcomeDialogController.showWelcomeDialog();
           // Platform.runLater(() -> showAlert("Регистрация", "Регистрация прошла успешно: " + response.getMessage()));
        } else {
            ErrorDialogController.showErrorDialog("Ошибка: " + response.getMessage());

        }
    }

    // Метод для отображения всплывающего окна с сообщением

}
