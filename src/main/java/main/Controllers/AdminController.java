package main.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.awt.*;

public class AdminController {
    @FXML
    private Button ButtonAddProcedure;
    @FXML
    private Button ButtonDeleteProcedure;
    @FXML
    private Button ButtonEditProcedure;

    public void AddProcedure_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) ButtonAddProcedure.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/AddProcedure.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void DeleteProcedure_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) ButtonAddProcedure.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/ViewingProcedures.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void EditProcedure_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) ButtonAddProcedure.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/AddProcedure.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }


}
