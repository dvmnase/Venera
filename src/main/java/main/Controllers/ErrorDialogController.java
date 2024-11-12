package main.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ErrorDialogController {
    @FXML
    private Label errorMessage;

    // Устанавливаем текст ошибки
    public void setErrorMessage(String message) {
        errorMessage.setText(message);
    }

    // Закрываем модальное окно
    @FXML
    private void closeDialog() {
        Stage stage = (Stage) errorMessage.getScene().getWindow();
        stage.close();
    }

    // Статический метод для отображения модального окна с сообщением об ошибке
    public static void showErrorDialog(String message) {
        try {
            // Убедитесь, что путь указан корректно
            FXMLLoader loader = new FXMLLoader(ErrorDialogController.class.getResource("/ErrorDialog.fxml"));
            Parent root = loader.load();

            // Получаем контроллер и устанавливаем сообщение об ошибке
            ErrorDialogController controller = loader.getController();
            controller.setErrorMessage(message);


            // Настроим и отображаем модальное окно
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ошибка");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Ошибка загрузки файла ErrorDialog.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
