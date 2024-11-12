package condorcet.Utility;

import com.google.gson.Gson;
import condorcet.Models.Entities.User;
import condorcet.Models.Entities.Client;
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
import java.sql.SQLException;

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
                                } else{
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
                            }catch (SQLException e) {
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
                                    response.setStatus("SUCCESS");
                                    response.setMessage("Login successful for user: " + login);
                                } else {
                                    response.setStatus("ERROR");
                                    response.setMessage("Invalid login or password.");
                                }
                            } catch (SQLException e) {
                                System.out.println("Database error during login: " + e.getMessage());
                                response.setStatus("ERROR");
                                response.setMessage("Login failed: " + e.getMessage());
                            }

                            // Отправка ответа клиенту
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
