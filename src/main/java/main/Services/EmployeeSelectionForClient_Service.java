package main.Services;

import com.google.gson.Gson;
import main.Enums.RequestType;
import main.Models.Entities.Appointment;
import main.Models.Entities.Employee;
import main.Models.Entities.Payment;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;
import main.Utils.GsonProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmployeeSelectionForClient_Service {

    private final Gson gson = new Gson();

    public List<Employee> getEmployeesForProcedure(int procedureID) {
        List<Employee> employees = new ArrayList<>();
        Request request = new Request();
        request.setRequestType(RequestType.GET_EMPLOYEES_FOR_PROCEDURE);
        request.setRequestMessage(String.valueOf(procedureID));

        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(request));
            ClientSocket.getInstance().getOut().flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseJson = in.readLine();
            Response response = gson.fromJson(responseJson, Response.class);

            if ("SUCCESS".equals(response.getStatus())) {
                Employee[] employeeArray = gson.fromJson(response.getMessage(), Employee[].class);
                employees = List.of(employeeArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employees;
    }


    public int createAppointment(int clientId, int employeeId, int procedureId, LocalDateTime appointmentDate) {
        Gson gson = GsonProvider.getGson(); // Используем кастомный Gson с адаптером
        Request request = new Request();
        request.setRequestType(RequestType.ADD_APPOINTMENT);

        // Создаем объект Appointment
        Appointment appointment = new Appointment();
        appointment.setClientId(clientId);
        appointment.setEmployeeId(employeeId);
        appointment.setProcedureId(procedureId);
        appointment.setAppointmentDate(appointmentDate);

        // Сериализуем объект Appointment в JSON
        String appointmentJson = gson.toJson(appointment);
        request.setRequestMessage(appointmentJson);

        try {
            // Отправляем запрос на сервер
            ClientSocket.getInstance().getOut().println(gson.toJson(request));
            ClientSocket.getInstance().getOut().flush();

            // Получаем ответ от сервера
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseJson = in.readLine();

            // Десериализуем ответ
            Response response = gson.fromJson(responseJson, Response.class);

            // Проверяем статус ответа
            if ("SUCCESS".equals(response.getStatus())) {
                // Если сервер возвращает сообщение, обрабатываем его
                String message = response.getMessage();
                // Попробуем преобразовать сообщение в ID, если это необходимо
                try {
                    return Integer.parseInt(message); // Преобразуем сообщение в ID
                } catch (NumberFormatException e) {
                    System.out.println("ID записи не найден, возвращаем -1. Сообщение: " + message);
                    return -1; // Если не удалось преобразовать, возвращаем -1
                }
            } else {
                System.out.println("Ошибка при создании записи: " + response.getMessage());
                return -1; // В случае ошибки
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1; // Если ошибка при подключении
    }
}
