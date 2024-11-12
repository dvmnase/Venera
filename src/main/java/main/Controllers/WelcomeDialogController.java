package main.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;

public class WelcomeDialogController {



    // Устанавливаем текст ошибки

    // Закрываем модальное окно
    @FXML
    private void closeDialog() {

        Stage stage = new Stage();
        stage.close();
    }

    // Статический метод для отображения модального окна с сообщением об ошибке
    public static void showWelcomeDialog() {
        try {
            // Убедитесь, что путь указан корректно
            FXMLLoader loader = new FXMLLoader(ErrorDialogController.class.getResource("/WelcomeDialog.fxml"));
            Parent root = loader.load();



            // Настроим и отображаем модальное окно
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Успешная регистрация");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Ошибка загрузки файла WelcomeDialog.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
