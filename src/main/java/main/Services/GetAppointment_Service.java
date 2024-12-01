package main.Services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.Enums.RequestType;
import main.Models.Entities.Appointment;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;
import main.Utility.Session;
import main.Utils.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GetAppointment_Service {

    public List<Appointment> getAppointmentsForClient() {
        List<Appointment> appointments = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();


        Request request = new Request();
        request.setRequestType(RequestType.GET_APPOINTMENT_FOR_CLIENT);

        int userId = Session.getInstance().getUserId();
        System.out.println("USER " + userId);
        request.setClientId(userId);

        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(request));
            ClientSocket.getInstance().getOut().flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseJson = in.readLine();

            if (responseJson != null ) {
                Response response = gson.fromJson(responseJson, Response.class);

                if (response != null && "SUCCESS".equals(response.getStatus())) {
                    Appointment[] appointmentsArray = gson.fromJson(response.getMessage(), Appointment[].class);

                    for (Appointment appointment : appointmentsArray) {
                        System.out.println("Raw appointment: " + gson.toJson(appointment));
                        System.out.println("Appointment Date: " + appointment.getAppointmentDate());
                    }

                    appointments = List.of(appointmentsArray);
                } else {
                    System.out.println("Failed to get appointments: " + (response != null ? response.getMessage() : "Response is null"));
                }

            } else {
                System.out.println("Failed to get appointments: Response is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appointments;
    }
}
