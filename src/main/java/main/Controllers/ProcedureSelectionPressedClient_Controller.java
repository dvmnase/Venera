package main.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.Models.Entities.Procedure;
import main.Services.ProcedureSelectionPressed_Service;
import main.Utility.Session;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ProcedureSelectionPressedClient_Controller {

    @FXML
    private HBox proceduresContainer;

    private ProcedureSelectionPressed_Service procedureService = new ProcedureSelectionPressed_Service();

    @FXML
    public void initialize() {
        // Инициализация контейнера или других параметров
    }

    @FXML
    public void ExitPressed(ActionEvent actionEvent) throws Exception {
        Session.getInstance().clear();

        // Получаем текущее окно (Stage) через объект actionEvent
        Stage stage = (Stage) proceduresContainer.getScene().getWindow();

        // Загружаем экран логина
        Parent root = FXMLLoader.load(getClass().getResource("/login6.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.show();
    }


    @FXML
    public void AppoimentsPressed(ActionEvent actionEvent) throws Exception{
        //Session.getInstance().clear();

        // Получаем текущее окно (Stage) через объект actionEvent
        Stage stage = (Stage) proceduresContainer.getScene().getWindow();

        // Загружаем экран логина
        Parent root = FXMLLoader.load(getClass().getResource("/GetAppintments.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.show();
    }

    @FXML
    private void handleMakeupClick() {
        openProcedureListWindow("МАКИЯЖ");
    }

    @FXML
    private void handleEyebrowsClick() {
        openProcedureListWindow("КОРРЕКЦИЯ БРОВЕЙ");
    }
    @FXML
    private void handleFacialCareClick(){
        openProcedureListWindow("УХОД ЗА ЛИЦОМ");
    }

    @FXML
    private void handlePedicureClick(){
        openProcedureListWindow("ПЕДИКЮР");
    }

    @FXML
    private void handleManicureClick(){
        openProcedureListWindow("МАНИКЮР");
    }

    @FXML
    private void handleHaircutClick(){
        openProcedureListWindow("СТРИЖКА");
    }

    @FXML
    private void handleColoringClick(){
        openProcedureListWindow("ОКРАШИВАНИЕ");
    }

    @FXML
    private void handleLayingClick(){
        openProcedureListWindow("УКЛАДКА");
    }

    @FXML
    private void handleHairCareClick(){
        openProcedureListWindow("УХОД ЗА ВОЛОСАМИ");
    }
    // Метод для открытия нового окна с FXML
    private void openProcedureListWindow(String serviceType) {
        try {
            // Загружаем FXML-файл
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProcedureListView.fxml"));
            Stage stage = new Stage();

            // Устанавливаем сцену
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(serviceType);

            // Передаем данные в контроллер
            ProcedureListController controller = loader.getController();
            List<Procedure> procedures = procedureService.getProceduresForType(serviceType);
            controller.setProcedures(procedures);

            // Показываем окно
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
