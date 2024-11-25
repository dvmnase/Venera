package main.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import main.Models.Entities.Employee;
import main.Models.Entities.Procedure;
import main.Services.ProcedureConnectionService;

import java.util.List;

public class ServiceSelection_Controller {

    @FXML
    private ListView<String> servicesListView;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    private Employee employee;
    private List<Procedure> availableServices;

    public void setEmployee(Employee pressedEmployee) {
        this.employee = pressedEmployee;
        fetchAndDisplayServices();
    }

    private void fetchAndDisplayServices() {
        ProcedureConnectionService service = new ProcedureConnectionService();
        availableServices = service.getAvailableServicesForEmployee(employee.getId());

        for (Procedure procedure : availableServices) {
            servicesListView.getItems().add(procedure.getTitle());
        }
    }

    @FXML
    private void onCancelPressed(ActionEvent event) {
        // Получаем Stage из источника события (кнопки Cancel)
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }


    @FXML
    private void onOkPressed(ActionEvent event) {
        int selectedIndex = servicesListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex == -1) {
            showAlert("Ошибка", "Выберите услугу из списка!", Alert.AlertType.WARNING);
            return;
        }

        Procedure selectedProcedure = availableServices.get(selectedIndex);

        ProcedureConnectionService service = new ProcedureConnectionService();
        boolean isSuccess = service.connectEmployeeToProcedure(employee.getId(), selectedProcedure.getId());

        if (isSuccess) {
            showAlert("Успех", "Услуга успешно привязана к сотруднику!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Ошибка", "Не удалось привязать услугу. Попробуйте еще раз.", Alert.AlertType.ERROR);
        }

        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
