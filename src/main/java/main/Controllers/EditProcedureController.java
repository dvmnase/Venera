package main.Controllers;

import com.google.gson.Gson;
import main.Enums.RequestType;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.BufferedReader;
import java.io.IOException;

public class EditProcedureController {

    public void EditProcedure(int procedureId, RequestType requestType, String newData) {
        Gson gson = new Gson();
        Request request = new Request();
        request.setRequestType(requestType);

        // Добавляем ID процедуры и новые данные в сообщение запроса
        String requestMessage = gson.toJson(new ProcedureUpdateRequest(procedureId, newData));
        request.setRequestMessage(requestMessage);

        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(request));
            ClientSocket.getInstance().getOut().flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseJson = in.readLine();
            Response response = gson.fromJson(responseJson, Response.class);

            if ("SUCCESS".equals(response.getStatus())) {
                ErrorDialogController.showErrorDialog("Процедура успешно отредактирована.");
            } else {
                ErrorDialogController.showErrorDialog("Ошибка редактирования: " + response.getMessage());
            }
        } catch (IOException e) {
            ErrorDialogController.showErrorDialog("Ошибка");
            e.printStackTrace();
        }
    }

    // Вспомогательный класс для передачи данных редактирования
    class ProcedureUpdateRequest {
        private int procedureId;
        private String newData;

        public ProcedureUpdateRequest(int procedureId, String newData) {
            this.procedureId = procedureId;
            this.newData = newData;
        }

        // Геттеры и сеттеры
    }

}
