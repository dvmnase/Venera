package main.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class AdminController {
    @FXML
    private Button ButtonAddProcedure;
    @FXML
    private Button ButtonDeleteProcedure;
    @FXML
    private Button ButtonEditProcedure;
    @FXML
    private Button ButtonAddEmployee;
    @FXML
    private Button ButtonConnectionProcedureEmployee;

    public void AddProcedure_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) ButtonAddProcedure.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/AddProcedure.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void DeleteProcedure_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) ButtonDeleteProcedure.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewingProcedures.fxml"));
        Parent root = loader.load();
        ViewingProceduresController viewingProceduresController = loader.getController();
        viewingProceduresController.setType("DELETE");
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void EditProcedure_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) ButtonEditProcedure.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewingProcedures.fxml"));
        Parent root = loader.load();
        ViewingProceduresController viewingProceduresController = loader.getController();
        viewingProceduresController.setType("EDIT");
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void AddEmployee_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) ButtonAddEmployee.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/AddEmployee.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void ConnectionProcedureEmployee_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) ButtonConnectionProcedureEmployee.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/ConnectionProcedureEmployee.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}