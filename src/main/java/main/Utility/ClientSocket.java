package main.Utility;

import com.google.gson.Gson;
import main.Models.TCP.Request;
import main.Models.TCP.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocket {

    private static final ClientSocket SINGLE_INSTANCE = new ClientSocket();
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private ClientSocket() {
        try {
            socket = new Socket("localhost", 5555);
            setIn(new BufferedReader(new InputStreamReader(socket.getInputStream())));
            out = new PrintWriter(socket.getOutputStream(), true); // Включен авто-очистка
        } catch (Exception e) {
            e.printStackTrace(); // Обработка исключений
        }
    }

    public static ClientSocket getInstance() {
        return SINGLE_INSTANCE;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void getSocket() {
    }

    // Метод для отправки запроса на сервер
    public void sendRequest(Request request) {
        String jsonRequest = new Gson().toJson(request);
        out.println(jsonRequest); // Отправка JSON-запроса на сервер
    }

    // Метод для получения ответа от сервера
    public Response getResponse() throws IOException {
        String jsonResponse = getIn().readLine(); // Чтение строки ответа от сервера
        return new Gson().fromJson(jsonResponse, Response.class); // Десериализация ответа
    }

    // Метод для закрытия соединения
    public void close() {
        try {
            if (getIn() != null) {
                getIn().close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }
}