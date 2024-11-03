package condorcet;

import condorcet.Utility.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int PORT_NUMBER = 5555;
    private static ServerSocket serverSocket;
    private static List<Socket> currentSockets = new ArrayList<>(); // коллекция сокетов

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Сервер запущен на порту: " + PORT_NUMBER);

            while (true) {
                // Удаление закрытых сокетов
                currentSockets.removeIf(Socket::isClosed);

                // Вывод информации о текущих подключениях
                for (Socket socket : currentSockets) {
                    String socketInfo = "Клиент " + socket.getInetAddress() + ": " + socket.getPort();
                    System.out.println(socketInfo);
                }

                // Принятие нового соединения
                Socket socket = serverSocket.accept();
                currentSockets.add(socket);
                ClientThread clientHandler = new ClientThread(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
                System.out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Закрытие серверного сокета при завершении работы программы
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {

                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}