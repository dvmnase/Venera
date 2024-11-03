package condorcet.Utility;

import com.google.gson.Gson;
import condorcet.Models.Entities.User;
import condorcet.Models.TCP.Request;
import condorcet.Models.TCP.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread implements Runnable {

    private Socket clientSocket;
    private Request request;
    private Response response;
    private Gson gson;
    private BufferedReader in;
    private PrintWriter out;

    public ClientThread(Socket clientSocket) throws IOException {
        response = new Response();
        request = new Request();
        this.clientSocket = clientSocket;
        gson = new Gson();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true); // добавлен true для автоматической очистки
    }

    @Override
    public void run() {
        try {
            while (clientSocket.isConnected()) {
                try {
                    String message = in.readLine();
                    if (message == null) break; // проверка на конец потока

                    request = gson.fromJson(message, Request.class);

                    switch (request.getRequestType()) {
                        case REGISTER: {
                            User user = gson.fromJson(request.getRequestMessage(), User.class);
                            // Обработка регистрации пользователя
                            // Например, сохранить пользователя в БД или выполнить другую логику
                            break; // добавлено для завершения блока
                        }
                        // Другие случаи могут быть добавлены здесь
                    }
                } catch (IOException e) {
                    System.out.println("Ох: " + e.getMessage() );
                    break; // Выход из цикла при ошибке чтения
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Общая обработка исключений
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close(); // Закрытие сокета в блоке finally
                }
            } catch (IOException e) {
                e.printStackTrace(); // Обработка исключения при закрытии сокета
            }
        }
    }
}