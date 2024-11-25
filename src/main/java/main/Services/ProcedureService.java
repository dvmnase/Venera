package main.Services;

import com.google.gson.Gson;
import main.Enums.RequestType;
import main.Models.Entities.Procedure;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcedureService {



    public List<Procedure> getEmployeeServices(int employeeId) {
        List<Procedure> procedures = new ArrayList<>();
        Gson gson = new Gson();
        Request request = new Request();
        request.setRequestType(RequestType.GET_EMPLOYEE_SERVICES);
        request.setRequestMessage(String.valueOf(employeeId));

        try {
            ClientSocket.getInstance().getOut().println(gson.toJson(request));
            ClientSocket.getInstance().getOut().flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseJson = in.readLine();
            Response response = gson.fromJson(responseJson, Response.class);

            if ("SUCCESS".equals(response.getStatus())) {
                Procedure[] procedureArray = gson.fromJson(response.getMessage(), Procedure[].class);
                procedures = List.of(procedureArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return procedures;
    }
}