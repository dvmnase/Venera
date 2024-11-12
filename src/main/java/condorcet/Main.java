package condorcet;

import condorcet.database.DatabaseInitializer;
import condorcet.Utility.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int PORT_NUMBER = 5555;
    private static ServerSocket serverSocket;
    private static List<Socket> currentSockets = new ArrayList<>();

    public static void main(String[] args) {
        DatabaseInitializer databaseInitializer = new DatabaseInitializer();
        databaseInitializer.initializeDatabase(); // Инициализация базы данных

        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Сервер запущен на порту: " + PORT_NUMBER);

            while (true) {
                currentSockets.removeIf(Socket::isClosed);

                for (Socket socket : currentSockets) {
                    String socketInfo = "Клиент " + socket.getInetAddress() + ": " + socket.getPort();
                    System.out.println(socketInfo);
                }

                Socket socket = serverSocket.accept();
                currentSockets.add(socket);
                try {
                    ClientThread clientHandler = new ClientThread(socket);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                } catch (SQLException e) {
                    System.out.println("Database connection error: " + e.getMessage());
                    socket.close();
                }
                System.out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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