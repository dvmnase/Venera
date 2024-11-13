package main.Controllers;

import com.google.gson.Gson;
import main.Enums.RequestType;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.BufferedReader;
import java.io.IOException;

public class DeleteProcedure_Controller {

    DeleteProcedure_Controller(){};
    public void deleteProcedure(int procedureId) {
        Gson gson = new Gson();
        Request request = new Request();
        request.setRequestType(RequestType.DELETE_PROCEDURE);
        request.setRequestMessage(String.valueOf(procedureId)); // Отправляем ID

        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(request));
            ClientSocket.getInstance().getOut().flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseJson = in.readLine();
            Response response = gson.fromJson(responseJson, Response.class);

            if ("SUCCESS".equals(response.getStatus())) {
                ErrorDialogController.showErrorDialog("Уничтожено");
                // Обработка успешного удаления (например, обновление интерфейса)
            }
        } catch (IOException e) {
            ErrorDialogController.showErrorDialog("Ошибка");
            e.printStackTrace();
        }
    }
}
