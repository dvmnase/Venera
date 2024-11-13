package main.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;

public class WelcomeDialogController {

    @FXML
    private Hyperlink WelcomeHyperlink;

    // Устанавливаем текст ошибки

    // Закрываем модальное окно
    @FXML
    private void closeDialog() throws IOException {
        if (registerStage != null) {
            registerStage.close();
        }

        // Загружаем главный интерфейс main.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent root = loader.load();

        // Получаем модальное окно WelcomeDialog и закрываем его
        Stage welcomeDialogStage = (Stage) WelcomeHyperlink.getScene().getWindow();
        welcomeDialogStage.close();

        // Создаем новый основной Stage для main.fxml
        Stage mainStage = new Stage();
        mainStage.setScene(new Scene(root));
        mainStage.setTitle("Главное окно");
        mainStage.show();
    }

    private Stage registerStage;

    public void setRegisterStage(Stage registerStage) {
        this.registerStage = registerStage;
    }

    // Статический метод для отображения модального окна с сообщением об ошибке
    public static void showWelcomeDialog(Stage registerStage) {
        try {
            // Убедитесь, что путь указан корректно
            FXMLLoader loader = new FXMLLoader(ErrorDialogController.class.getResource("/WelcomeDialog.fxml"));
            Parent root = loader.load();

            // Получаем контроллер и передаем Stage окна регистрации
            WelcomeDialogController controller = loader.getController();
            controller.setRegisterStage(registerStage); // добавляем этот метод в контроллер



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
