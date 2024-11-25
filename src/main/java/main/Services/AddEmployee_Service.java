package main.Services;

import com.google.gson.Gson;
import javafx.stage.Stage;
import main.Controllers.ErrorDialogController;
import main.Controllers.WelcomeDialogController;
import main.Enums.RequestType;
import main.Models.Entities.Employee;
import main.Models.Entities.Procedure;
import main.Models.Entities.User;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.BufferedReader;
import java.io.IOException;

public class AddEmployee_Service {

    public void AddEmployee(User employee) {
        Gson gson = new Gson();

        // Создание объекта запроса
        Request request = new Request();
        request.setRequestType(RequestType.ADD_EMPLOYEE);
        request.setRequestMessage(gson.toJson(employee));

        try {
            // Отправка запроса на сервер
            ClientSocket.getInstance().getOut().println(gson.toJson(request));
            ClientSocket.getInstance().getOut().flush();

            // Ожидание и обработка ответа от сервера
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseJson = in.readLine();
            if (responseJson != null) {
                Response response = gson.fromJson(responseJson, Response.class);
                if ("SUCCESS".equals(response.getStatus())) {
                    ErrorDialogController.showErrorDialog("Получилось");
                } else {
                    ErrorDialogController.showErrorDialog("Ошибка: " + response.getMessage());

                }
            }
        } catch (IOException e) {
            ErrorDialogController.showErrorDialog("Нет подключения к серверу");

        }
    }
}
