package main.Services;

import com.google.gson.Gson;
import main.Controllers.ErrorDialogController;
import main.Enums.RequestType;
import main.Models.Entities.Payment;
import main.Models.Entities.User;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.BufferedReader;
import java.io.IOException;

public class AddPayment_Service {

    public void AddPayment(Payment payment) {
        Gson gson = new Gson();
        Request request = new Request();
        request.setRequestType(RequestType.ADD_PAYMENT);
        request.setRequestMessage(gson.toJson(payment));

        try {
            // Отправка запроса на сервер
            ClientSocket.getInstance().getOut().println(gson.toJson(request));
            ClientSocket.getInstance().getOut().flush();

            // Ожидание ответа от сервера
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseJson = in.readLine();
            if (responseJson != null) {
                Response response = gson.fromJson(responseJson, Response.class);
                if ("SUCCESS".equals(response.getStatus())) {
                    ErrorDialogController.showErrorDialog("Платеж успешно добавлен.");
                } else {
                    ErrorDialogController.showErrorDialog("Ошибка: " + response.getMessage());
                }
            }
        } catch (IOException e) {
            ErrorDialogController.showErrorDialog("Нет подключения к серверу");
            e.printStackTrace(); // Выводим стек вызовов для отладки
        }
    }
}
