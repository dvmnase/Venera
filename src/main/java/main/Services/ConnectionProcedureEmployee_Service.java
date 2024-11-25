package main.Services;

import com.google.gson.Gson;
import main.Enums.RequestType;
import main.Models.Entities.Employee;
import main.Models.Entities.Procedure;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionProcedureEmployee_Service {
    public List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();
        Gson gson = new Gson();

        Request request = new Request();
        request.setRequestType(RequestType.READ_EMPLOYEES);

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
}
