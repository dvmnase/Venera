package condorcet.Utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import condorcet.DataAccessObjects.*;
import condorcet.Models.Entities.*;
import condorcet.Utils.GsonProvider;
import condorcet.Utils.LocalDateTimeAdapter;
import main.Enums.RequestType;
import condorcet.Models.TCP.Request;
import condorcet.Models.TCP.Response;
import condorcet.Validation.UserValidation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
                                    // Получаем роль и ID пользователя
                                    String role = userDAO.getUserRole(login);
                                    int userId = userDAO.getUserId(login); // Получаем ID пользователя

                                    // Устанавливаем статус успешной авторизации и отправляем роль и ID
                                    response.setStatus("SUCCESS");
                                    response.setMessage("Авторизация успешна");
                                    response.setRole(role);
                                    response.setUserId(userId); // Отправляем ID пользователя

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

                        case GET_PROCEDURES_FOR_TYPE: {
                            String procedureType = request.getRequestMessage(); // Получаем тип процедуры из запроса
                            ProcedureDAO procedureDAO = new ProcedureDAO(connection);

                            try {
                                // Получаем процедуры, соответствующие указанному типу
                                List<Procedure> filteredProcedures = procedureDAO.getProceduresByType(procedureType);

                                response.setStatus("SUCCESS");
                                response.setMessage(gson.toJson(filteredProcedures)); // Отправляем список процедур в формате JSON
                            } catch (SQLException e) {
                                response.setStatus("ERROR");
                                response.setMessage("Ошибка при получении процедур по типу: " + e.getMessage());
                            }

                            out.println(gson.toJson(response)); // Отправка ответа клиенту
                            break;
                        }

                        case GET_EMPLOYEES_FOR_PROCEDURE: {
                            int procedureId = Integer.parseInt(request.getRequestMessage());
                            System.out.println("Request for employees for procedure ID: " + procedureId);

                            EmployeeDAO employeeDAO = new EmployeeDAO(connection);
                            try {
                                List<Employee> employees = employeeDAO.getEmployeesByProcedureId(procedureId);
                                System.out.println("Employees fetched: " + employees);

                                response.setStatus("SUCCESS");
                                response.setMessage(gson.toJson(employees));
                            } catch (SQLException e) {
                                e.printStackTrace();
                                response.setStatus("ERROR");
                                response.setMessage("Ошибка при получении сотрудников для процедуры: " + e.getMessage());
                            }

                            out.println(gson.toJson(response));
                            break;
                        }
                        case ADD_APPOINTMENT: {
                            Gson gson = GsonProvider.getGson();
                            Appointment appointment = gson.fromJson(request.getRequestMessage(), Appointment.class);
                            AppointmentDAO appointmentDAO = new AppointmentDAO(connection);
                            Response response = new Response(); // Создаем новый объект ответа

                            try {
                                // Сохраняем запись о встрече и получаем ID
                                int appointmentId = appointmentDAO.saveAppointment(appointment);

                                // Устанавливаем ID в ответ
                                response.setStatus("SUCCESS");
                                response.setMessage(String.valueOf(appointmentId)); // Возвращаем ID как строку
                                response.setAppointmentId(appointmentId);  // Сохраняем ID записи в ответе

                                System.out.println("Новый ID записи: " + appointmentId);  // Выводим ID на консоль

                            } catch (SQLException e) {
                                // Обработка ошибки базы данных
                                System.out.println("Database error during add appointment: " + e.getMessage());
                                response.setStatus("ERROR");
                                response.setMessage("Failed to add appointment: " + e.getMessage());
                            }

                            // Отправка ответа клиенту
                            out.println(gson.toJson(response));
                            break;
                        }
                        case ADD_PAYMENT: {
                            // Используем кастомный Gson с адаптером
                            Gson gson = GsonProvider.getGson();
                            Payment payment = gson.fromJson(request.getRequestMessage(), Payment.class);
                            PaymentDAO paymentDAO = new PaymentDAO(connection);

                            try {
                                // Сохраняем запись о встрече и получаем ID
                                paymentDAO.savePayment(payment);
                                System.out.println("Пришло "+payment.getAppointment_id());
                              //  int appointmentId = appointmentDAO.saveAppointment(appointment);

                                // Устанавливаем ID в ответ
                                response.setStatus("SUCCESS");
                                response.setMessage("Appointment added successfully.");
                               // response.setAppointmentId(appointmentId);  // Добавляем ID записи в ответ

                             //   System.out.println("Новый ID записи: " + appointmentId);  // Выводим ID на консоль

                            } catch (SQLException e) {
                                // Обработка ошибки базы данных
                                System.out.println("Database error during add appointment: " + e.getMessage());
                                response.setStatus("ERROR");
                                response.setMessage("Failed to add appointment: " + e.getMessage());
                            }

                            // Отправка ответа клиенту
                            out.println(gson.toJson(response));
                            break;
                        }

                        case GET_APPOINTMENT_FOR_CLIENT: {
                            try {
                                int clientId = request.getClientId();
                                AppointmentDAO appointmentDAO = new AppointmentDAO(connection);
                                ProcedureDAO procedureDAO = new ProcedureDAO(connection);
                                EmployeeDAO employeeDAO = new EmployeeDAO(connection);
                                PaymentDAO paymentDAO = new PaymentDAO(connection); // Добавляем PaymentDAO

                                List<Appointment> appointments = appointmentDAO.getAppointmentsByClientId(clientId);
                                List<Map<String, Object>> appointmentsData = new ArrayList<>();

                                for (Appointment appointment : appointments) {
                                    Procedure procedure = procedureDAO.getProcedureById(appointment.getProcedureId());
                                    Employee employee = employeeDAO.getEmployeeById(appointment.getEmployeeId());
                                    Payment payment = paymentDAO.getPaymentByAppointmentId(appointment.getId()); // Получаем данные о платеже

                                    Map<String, Object> appointmentDetails = new HashMap<>();
                                    appointmentDetails.put("appointment_id", appointment.getId());
                                    appointmentDetails.put("appointment_date", appointment.getAppointmentDate());
                                    appointmentDetails.put("notes", appointment.getNotes());
                                    appointmentDetails.put("procedure", procedure);
                                    appointmentDetails.put("employee", employee);

                                    // Добавляем информацию о платеже, если она существует
                                    if (payment != null) {
                                        Map<String, Object> paymentDetails = new HashMap<>();
                                        paymentDetails.put("amount", payment.getAmount());
                                        paymentDetails.put("payment_method", payment.getPaymentMethod());
                                        appointmentDetails.put("payment", paymentDetails);
                                    } else {
                                        appointmentDetails.put("payment", null); // Если данных о платеже нет
                                    }

                                    appointmentsData.add(appointmentDetails);
                                }

                                Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                        .create();

                                Response response = new Response();
                                response.setStatus("SUCCESS");
                                response.setMessage(gson.toJson(appointmentsData));
                                out.println(gson.toJson(response));
                            } catch (SQLException e) {
                                System.out.println("Database error during GET_APPOINTMENT_FOR_CLIENT: " + e.getMessage());
                                Response response = new Response();
                                response.setStatus("ERROR");
                                response.setMessage("Failed to fetch appointments: " + e.getMessage());
                                out.println(gson.toJson(response));
                            }
                            break;
                        }


                        case CANCEL_APPOINTMENT: {
                            int appointmentId = request.getAppointmentId(); // Получаем ID записи из запроса

                            PaymentDAO paymentDAO = new PaymentDAO(connection);
                            AppointmentDAO appointmentDAO = new AppointmentDAO(connection);

                            try {
                                // Удаление платежа
                                paymentDAO.deletePaymentByAppointmentId(appointmentId);

                                // Удаление записи
                                appointmentDAO.deleteAppointmentById(appointmentId);

                                // Успешный ответ
                                response.setStatus("SUCCESS");
                                response.setMessage("Запись с ID " + appointmentId + " успешно отменена.");

                                System.out.println("Запись с ID " + appointmentId + " и связанный платеж удалены.");
                            } catch (SQLException e) {
                                System.out.println("Ошибка при отмене записи: " + e.getMessage());
                                response.setStatus("ERROR");
                                response.setMessage("Не удалось отменить запись: " + e.getMessage());
                            }

                            // Отправка ответа клиенту
                            out.println(gson.toJson(response));
                            break;
                        }
                        case EDIT_APPOINTMENT: {
                            int appointmentId = request.getAppointmentId();
                            LocalDateTime newDateTime = LocalDateTime.parse(request.getRequestMessage());

                            AppointmentDAO appointmentDAO = new AppointmentDAO(connection);

                            try {
                                // Обновление записи в базе данных
                                appointmentDAO.updateAppointmentDate(appointmentId, newDateTime);

                                // Успешный ответ
                                response.setStatus("SUCCESS");
                                response.setMessage("Запись успешно перенесена.");
                            } catch (SQLException e) {
                                System.out.println("Ошибка при переносе записи: " + e.getMessage());
                                response.setStatus("ERROR");
                                response.setMessage("Не удалось перенести запись: " + e.getMessage());
                            }

                            // Отправка ответа клиенту
                            out.println(gson.toJson(response));
                            break;
                        }
                        case SEARCH_PROCEDURES:{


                                String Query = request.getRequestMessage();
                                System.out.println("Запрос юзер: " + Query);

                                ProcedureDAO procedureDAO = new ProcedureDAO(connection);
                            List<Procedure> procedures = procedureDAO.searchProcedures(Query);


                            response.setStatus("SUCCESS");
                            response.setMessage(gson.toJson(procedures));

                            out.println(gson.toJson(response));
                                break;
                        }
                        case GET_EMPLOYEES: {
                            try {
                                int userId = request.getClientId();
                                System.out.println("Employee ID: " + userId);

                                AppointmentDAO appointmentDAO = new AppointmentDAO(connection);
                                ProcedureDAO procedureDAO = new ProcedureDAO(connection);
                                ClientDAO clientDAO = new ClientDAO(connection);
                                EmployeeDAO employeeDAO = new EmployeeDAO(connection);

                                Employee employee = employeeDAO.getEmployeeByUserId(userId);
                                int employeeId = employee.getId();
                                List<Appointment> appointments = appointmentDAO.getAppointmentsByEmployeeId(employeeId);
                                List<Map<String, Object>> appointmentsData = new ArrayList<>();

                                for (Appointment appointment : appointments) {
                                    Procedure procedure = procedureDAO.getProcedureById(appointment.getProcedureId());
                                    Client client = clientDAO.getClientById(appointment.getClientId());

                                    Map<String, Object> appointmentDetails = new HashMap<>();
                                    appointmentDetails.put("appointment_id", appointment.getId());
                                    appointmentDetails.put("appointment_date", appointment.getAppointmentDate());
                                    appointmentDetails.put("notes", appointment.getNotes());
                                    appointmentDetails.put("procedure", procedure);
                                    appointmentDetails.put("client", client);

                                    appointmentsData.add(appointmentDetails);
                                }

                                Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                        .create();

                                Response response = new Response();
                                response.setStatus("SUCCESS");
                                response.setMessage(gson.toJson(appointmentsData));
                                out.println(gson.toJson(response));
                            } catch (SQLException e) {
                                System.out.println("Database error: " + e.getMessage());
                                Response response = new Response();
                                response.setStatus("ERROR");
                                response.setMessage("Failed to fetch appointments: " + e.getMessage());
                                out.println(gson.toJson(response));
                            }
                            break;
                        }
                        case GET_STATISTICS_PROCEDURES: {
                            try {
                                ProcedureDAO procedureDAO = new ProcedureDAO(connection);

                                // Получаем статистику сотрудников
                                List<Map<String, Object>> procedureStatistics = procedureDAO.getProcedureStatistics();

                                Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                        .create();

                                Response response = new Response();
                                response.setStatus("SUCCESS");
                                response.setMessage(gson.toJson(procedureStatistics));
                                out.println(gson.toJson(response));
                            } catch (SQLException e) {
                                System.out.println("Database error: " + e.getMessage());
                                Response response = new Response();
                                response.setStatus("ERROR");
                                response.setMessage("Failed to fetch employee statistics: " + e.getMessage());
                                out.println(gson.toJson(response));
                            }
                            break;
                        }

                        case GET_STATISTICS_EMPLOYEES: {
                            try {
                                EmployeeDAO employeeDAO = new EmployeeDAO(connection);

                                // Получаем статистику сотрудников
                                List<Map<String, Object>> employeeStatistics = employeeDAO.getEmployeeStatistics();

                                Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                        .create();

                                Response response = new Response();
                                response.setStatus("SUCCESS");
                                response.setMessage(gson.toJson(employeeStatistics));
                                out.println(gson.toJson(response));
                            } catch (SQLException e) {
                                System.out.println("Database error: " + e.getMessage());
                                Response response = new Response();
                                response.setStatus("ERROR");
                                response.setMessage("Failed to fetch employee statistics: " + e.getMessage());
                                out.println(gson.toJson(response));
                            }
                            break;
                        }
                        case GET_STATISTICS_VISIT: {
                            try {
                                AppointmentDAO appointmentDAO = new AppointmentDAO(connection);

                                // Получаем статистику посещений
                                Map<String, Double> visitStatistics = appointmentDAO.getVisitStatistics();

                                // Формируем список для отправки
                                List<Map<String, Object>> result = new ArrayList<>();
                                for (Map.Entry<String, Double> entry : visitStatistics.entrySet()) {
                                    Map<String, Object> entryMap = new HashMap<>();
                                    entryMap.put("date", entry.getKey());
                                    entryMap.put("total_amount", entry.getValue());
                                    result.add(entryMap);
                                }

                                // Отправляем ответ клиенту
                                Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                        .create();

                                Response response = new Response();
                                response.setStatus("SUCCESS");
                                response.setMessage(gson.toJson(result));
                                out.println(gson.toJson(response));
                            } catch (SQLException e) {
                                System.out.println("Database error: " + e.getMessage());
                                Response response = new Response();
                                response.setStatus("ERROR");
                                response.setMessage("Failed to fetch visit statistics: " + e.getMessage());
                                out.println(gson.toJson(response));
                            }
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
