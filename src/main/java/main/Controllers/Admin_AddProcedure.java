package main.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import main.Models.Entities.Client;
import main.Models.Entities.Procedure;
import main.Models.Entities.User;
import main.Services.AddProcedure_Service;
import main.Services.RegistrationService;

import java.awt.*;

public class Admin_AddProcedure {
    @FXML
    private Button ButtonAdd;
    @FXML
    private Button ButtonExit;
    @FXML
    private TextField textFieldTittle;
    @FXML
    private TextArea textAreaDescription;
    @FXML
    private TextField textFieldPrice;
    @FXML
    private TextField textFieldTime;
    @FXML
    private ChoiceBox<String> choiceBoxService;

    public void Exit_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) ButtonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Admin.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void Add_Pressed(ActionEvent actionEvent) throws Exception {

        String title = textFieldTittle.getText().trim();
        String description = textAreaDescription.getText().trim();
        Integer duration = Integer.parseInt(textFieldTime.getText().trim());
        Float price = Float.parseFloat(textFieldPrice.getText().trim());
        String service_type = choiceBoxService.getTypeSelector();

        if (title.isEmpty() || description.isEmpty() || duration<=0 || price<=0) {
            ErrorDialogController.showErrorDialog("Все поля должны быть корректно заполнены");
            return;
        }



        // Создание пользователя и отправка данных на сервер
        Procedure procedure = new Procedure();
        procedure.setTitle(title);
        procedure.setDescription(description);
        procedure.setDuration(duration);
        procedure.setPrice(price);
        procedure.setService_type(service_type);


        AddProcedure_Service addProcedureService = new AddProcedure_Service();
        addProcedureService.AddProcedure(procedure);

    }



}
