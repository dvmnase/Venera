package main.Services;

import com.google.gson.Gson;
import main.Enums.RequestType;
import main.Models.Entities.Procedure;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProcedureSelectionPressed_Service {

    public List<Procedure> getProceduresForType(String procedureType) {
        List<Procedure> procedures = new ArrayList<>();
        Gson gson = new Gson();

        // Создаем запрос с типом процедуры
        Request request = new Request();
        request.setRequestType(RequestType.GET_PROCEDURES_FOR_TYPE);
        request.setRequestMessage(procedureType); // Указываем тип процедуры

        try {
            // Отправляем запрос на сервер
            ClientSocket.getInstance().getOut().println(gson.toJson(request));
            ClientSocket.getInstance().getOut().flush();

            // Читаем ответ от сервера
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseJson = in.readLine();
            Response response = gson.fromJson(responseJson, Response.class);

            // Проверяем статус ответа
            if ("SUCCESS".equals(response.getStatus())) {
                Procedure[] procedureArray = gson.fromJson(response.getMessage(), Procedure[].class);
                procedures = List.of(procedureArray);
            } else {
                System.err.println("Ошибка получения процедур: " + response.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return procedures;
    }
}