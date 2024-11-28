package main.Utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class Session {
    private static Session instance;
    private int userId;
    private String role;

    // Приватный конструктор для Singleton
    private Session() {}

    // Получение единственного экземпляра
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    // Методы для управления данными сессии
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Очистка данных сессии
    public void clear() {
        userId = 0;
        role = null;
    }

    public void initialize() {
        Session session = Session.getInstance();
        int userId = session.getUserId();
        String role = session.getRole();

        System.out.println("Текущий пользователь ID: " + userId + ", роль: " + role);

        // Используйте userId для выполнения операций
    }
    public void logout(ActionEvent actionEvent) {
        Session.getInstance().clear(); // Очистить сессию
        try {
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
