package condorcet.Utility;

import com.google.gson.Gson;
import condorcet.DataAccessObjects.EmployeeDAO;
import condorcet.DataAccessObjects.ProcedureDAO;
import condorcet.Models.Entities.*;
import main.Enums.RequestType;
import condorcet.Models.TCP.Request;
import condorcet.Models.TCP.Response;
import condorcet.DataAccessObjects.UserDAO;
import condorcet.DataAccessObjects.ClientDAO;
import condorcet.Validation.UserValidation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class ClientThread implements Runnable {

    private Socket clientSocket;
    private Request request;
    private Response response;
    private Gson gson;
    private BufferedReader in;
    private PrintWriter out;
    private Connection connection;

    public ClientThread(Socket clientSocket) throws IOException, SQLException {
        response = new Response();
        request = new Request();
        this.clientSocket = clientSocket;
        gson = new Gson();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        // Initialize database connection
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/venera", "root", "root");
    }

    @Override
    public void run() {
        try {
            while (clientSocket.isConnected()) {
                try {
                    String message = in.readLine();
                    if (message == null) break;

                    request = gson.fromJson(message, Request.class);



                    switch (request.getRequestType()) {
                        case REGISTER: {
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            Client client = user.getPersonData();

                            // Use DAO to save user and client data
                            UserDAO userDAO = new UserDAO(connection);
                            ClientDAO clientDAO = new ClientDAO(connection);
                            UserValidation userValidation = new UserValidation(connection);

                            try {
                                if (userValidation.isLoginExists(user.getLogin())) {
                                    response.setStatus("ERROR");
                                    response.setMessage("Логин уже занят. Пожалуйста, выберите другой.");
                                } else if (userValidation.isPhoneExists(client.getPhone())) {
                                    response.setStatus("ERROR");
                                    response.setMessage("Номер телефона уже привязан к существующему аккаунту.");
                                } else {
                                    // Сохранение пользователя и получение его ID
                                    int userId = userDAO.saveUser(user);

                                    // Сохранение клиента с использованием полученного user_id
                                    clientDAO.saveClient(client, userId);

                                    // Выводим сообщение о регистрации на сервере
                                    System.out.println("Зарегистрирован новый пользователь: " + user.getLogin());

                                    // Send success response with a detailed message
                                    response.setStatus("SUCCESS");
                                    response.setMessage("Registration successful for user: " + user.getLogin());
                                }
                            } catch (SQLException e) {
                                System.out.println("Database error during registration: " + e.getMessage());
                                response.setStatus("ERROR");
                                response.setMessage("Registration failed: " + e.getMessage());
                            }

                            // Отправка ответа клиенту
                            out.println(gson.toJson(response));
                            break;
                        }

                        case LOGIN: {  // Обработка запроса на авторизацию
                            User user = gson.fromJson(request.getRequestMessage(), User.class);

                            String login = user.getLogin();
                            String password = user.getPassword();

                            UserDAO userDAO = new UserDAO(connection);

                            try {
                                // Проверяем, существует ли пользователь с данным логином и паролем
                                if (userDAO.isValidLogin(login, password)) {
                                    String role = userDAO.getUserRole(login);
                                    response.setStatus("SUCCESS");
                                    response.setMessage("Авторизация успешна");
                                    response.setRole(role);
                                } else {
                                    response.setStatus("ERROR");
                                    response.setMessage("Неверный логин или пароль");
                                }
                            } catch (SQLException e) {
                                System.out.println("Database error during login: " + e.getMessage());
                                response.setStatus("ERROR");
                                response.setMessage("Ошибка при авторизации: " + e.getMessage());
                            }

                            // Отправка ответа клиенту
                            out.println(gson.toJson(response));
                            break;
                        }

                        case ADD_PROCEDURE: {
                            Procedure procedure = gson.fromJson(request.getRequestMessage(), Procedure.class);


                            ProcedureDAO procedureDAO = new ProcedureDAO(connection);



                            try {
                                procedureDAO.saveProcedure(procedure);



                                    // Выводим сообщение о регистрации на сервере
                                    System.out.println("Добавлена новая услуга: " + procedure.getTitle());

                                    // Send success response with a detailed message
                                    response.setStatus("SUCCESS");
                                    response.setMessage("Add a new procedure: " + procedure.getTitle());
                                }
                            catch (SQLException e)
                            {
                                System.out.println("Database error during add procedure: " + e.getMessage());
                                response.setStatus("ERROR");
                                response.setMessage("Add procedure failed: " + e.getMessage());
                            }
                            // Отправка ответа клиенту
                            out.println(gson.toJson(response));
                            break;
                        }

                        case READ_PROCEDURES: {
                            ProcedureDAO procedureDAO = new ProcedureDAO(connection);
                            List<Procedure> procedures = procedureDAO.getAllProcedures(); // Метод для получения всех процедур

                            response.setStatus("SUCCESS");
                            response.setMessage(gson.toJson(procedures)); // Отправляем список процедур в формате JSON
                            out.println(gson.toJson(response));
                            break;
                        }

                        case DELETE_PROCEDURE: {
                            int procedureId = Integer.parseInt(request.getRequestMessage());
                            ProcedureDAO procedureDAO = new ProcedureDAO(connection);

                            try {
                                procedureDAO.deleteProcedure(procedureId); // Метод, который удаляет процедуру по ID
                                response.setStatus("SUCCESS");
                                response.setMessage("Процедура успешно удалена.");
                            } catch (SQLException e) {
                                response.setStatus("ERROR");
                                response.setMessage("Ошибка при удалении процедуры: " + e.getMessage());
                            }

                            out.println(gson.toJson(response));
                            break;
                        }

                        case EDIT_DESCRIPTION:
                        case EDIT_PRICE:
                        case EDIT_DURATION: {
                            ProcedureUpdateRequest updateRequest = gson.fromJson(request.getRequestMessage(), ProcedureUpdateRequest.class);
                            int procedureId = updateRequest.getProcedureId();
                            String newData = updateRequest.getNewData();

                            ProcedureDAO procedureDAO = new ProcedureDAO(connection);
                            try {
                                switch (request.getRequestType()) {
                                    case EDIT_DESCRIPTION:
                                        procedureDAO.updateProcedureDescription(procedureId, newData);
                                        break;
                                    case EDIT_PRICE:
                                        procedureDAO.updateProcedurePrice(procedureId, Float.parseFloat(newData));
                                        break;
                                    case EDIT_DURATION:
                                        procedureDAO.updateProcedureDuration(procedureId, Integer.parseInt(newData));
                                        break;
                                }

                                response.setStatus("SUCCESS");
                                response.setMessage("Процедура успешно отредактирована.");
                            } catch (SQLException e) {
                                response.setStatus("ERROR");
                                response.setMessage("Ошибка при редактировании: " + e.getMessage());
                            }

                            out.println(gson.toJson(response));
                            break;
                        }
                        case ADD_EMPLOYEE:{
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            Employee employee = user.getEmployeeData();

                            // Use DAO to save user and client data
                            UserDAO userDAO = new UserDAO(connection);
                            EmployeeDAO employeeDAO = new EmployeeDAO(connection);
                            UserValidation userValidation = new UserValidation(connection);

                            try {
                                if (userValidation.isLoginExists(user.getLogin())) {
                                    response.setStatus("ERROR");
                                    response.setMessage("Логин уже занят. Пожалуйста, выберите другой.");
                                } else if (userValidation.isPhoneExists(employee.getPhone())) {
                                    response.setStatus("ERROR");
                                    response.setMessage("Номер телефона уже привязан к существующему аккаунту.");
                                } else {
                                    // Сохранение пользователя и получение его ID
                                    int userId = userDAO.saveUser(user);

                                    // Сохранение клиента с использованием полученного user_id
                                    employeeDAO.saveEmployee(employee, userId);

                                    // Выводим сообщение о регистрации на сервере
                                    System.out.println("Зарегистрирован новый пользователь: " + user.getLogin());

                                    // Send success response with a detailed message
                                    response.setStatus("SUCCESS");
                                    response.setMessage("Registration successful for user: " + user.getLogin());
                                }
                            } catch (SQLException e) {
                                System.out.println("Database error during registration: " + e.getMessage());
                                response.setStatus("ERROR");
                                response.setMessage("Registration failed: " + e.getMessage());
                            }

                            // Отправка ответа клиенту
                            out.println(gson.toJson(response));
                            break;
                        }
                        case READ_EMPLOYEES:{
                            EmployeeDAO employeeDAO = new EmployeeDAO(connection);
                            List<Employee> employees = employeeDAO.getAllEmployees(); // Метод для получения всех процедур

                            response.setStatus("SUCCESS");
                            response.setMessage(gson.toJson(employees)); // Отправляем список процедур в формате JSON
                            out.println(gson.toJson(response));
                            break;
                        }
                        case GET_EMPLOYEE_SERVICES: {
                            int employeeId = Integer.parseInt(request.getRequestMessage());
                            ProcedureDAO procedureDAO = new ProcedureDAO(connection);

                            try {
                                // Получаем все услуги, не связанные с указанным сотрудником
                                List<Procedure> availableProcedures = procedureDAO.getUnassignedProcedures(employeeId);

                                response.setStatus("SUCCESS");
                                response.setMessage(gson.toJson(availableProcedures)); // Отправляем доступные услуги в формате JSON
                            } catch (SQLException e) {
                                response.setStatus("ERROR");
                                response.setMessage("Ошибка при получении услуг: " + e.getMessage());
                            }

                            out.println(gson.toJson(response));
                            break;
                        }


                        case CONNECT_PROCEDURE_EMPLOYEE: {
                            String[] ids = request.getRequestMessage().split(",");
                            int employeeId = Integer.parseInt(ids[0]);
                            int procedureId = Integer.parseInt(ids[1]);

                            String sql = "INSERT INTO employee_procedures (employee_id, procedure_id) VALUES (?, ?)";
                            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                                stmt.setInt(1, employeeId);
                                stmt.setInt(2, procedureId);
                                stmt.executeUpdate();

                                response.setStatus("SUCCESS");
                                response.setMessage("Услуга привязана к сотруднику");
                            } catch (SQLException e) {
                                response.setStatus("ERROR");
                                response.setMessage("Ошибка при привязке услуги: " + e.getMessage());
                            }

                            out.println(gson.toJson(response));
                            break;
                        }




                    }
                } catch (IOException e) {
                    System.out.println("Error reading client message: " + e.getMessage());
                    response.setStatus("ERROR");
                    response.setMessage("An error occurred while reading the request.");
                    out.println(gson.toJson(response));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
