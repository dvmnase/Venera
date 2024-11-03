import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.awt.*;
import javafx.event.ActionEvent;

public class Login {
@FXML
    private Button buttonLogin;
    public void Login_Pressed(ActionEvent actionEvent) {
        // Логика для обработки входа
    }
    public void Register_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) buttonLogin.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("registr.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}
