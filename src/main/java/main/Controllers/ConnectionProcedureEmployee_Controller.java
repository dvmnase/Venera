package main.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Models.Entities.Employee;
import main.Models.Entities.Procedure;
import main.Services.ConnectionProcedureEmployee_Service;

import java.io.IOException;
import java.util.List;

public class ConnectionProcedureEmployee_Controller {

    @FXML
    private VBox proceduresContainer; // Контейнер для динамического добавления процедур

    @FXML
    private Hyperlink HyperLinkExit;

    public void initialize() {
        fetchAndDisplayProcedures();
    }

    public void Exit_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) HyperLinkExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Admin.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    private void fetchAndDisplayProcedures() {
        ConnectionProcedureEmployee_Service service = new ConnectionProcedureEmployee_Service();
        List<Employee> employees = service.getEmployees();

        for (Employee employee : employees) {
            addEmployeeToUI(employee);
        }
    }

    private void addEmployeeToUI(Employee employee) {
        StackPane cell = new StackPane();
        cell.setPrefWidth(500);
        cell.setStyle("-fx-background-color: #003366; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Обработчики наведения мыши
        cell.setOnMouseEntered(event -> cell.setStyle("-fx-background-color: #87CEFA; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;"));
        cell.setOnMouseExited(event -> cell.setStyle("-fx-background-color: #003366; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;"));

        VBox textContainer = new VBox();
        textContainer.setSpacing(5);

        Label surnameLabel = new Label("Фамилия: " + employee.getSurname());
        Label nameLabel = new Label("Имя: " + employee.getName());
        Label specializationLabel = new Label("Специальность: " + employee.getSpecialization());
        Label additionalDataLabel = new Label("Доп. данные: " + employee.getShared_data());
        Label phoneLabel = new Label("Телефон: " + employee.getPhone());

        for (Label label : List.of(surnameLabel, nameLabel, specializationLabel, additionalDataLabel, phoneLabel)) {
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        }

        textContainer.getChildren().addAll(surnameLabel, nameLabel, specializationLabel, additionalDataLabel, phoneLabel);
        cell.getChildren().add(textContainer);

        // Обработчик клика
        cell.setOnMouseClicked(event -> openServiceSelection(employee));

        proceduresContainer.getChildren().add(cell);
    }

    private void openServiceSelection(Employee pressedEmployee) {
        try {
            // Создаем объект FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServiceSelection.fxml"));
            Parent root = loader.load(); // Загружаем FXML

            // Получаем контроллер для настройки
            ServiceSelection_Controller controller = loader.getController();
            controller.setEmployee(pressedEmployee);

            // Создаем новое окно
            Stage stage = new Stage();
            stage.setTitle("Выбор услуг для " + pressedEmployee.getName()); // Устанавливаем заголовок окна
            stage.setScene(new Scene(root)); // Устанавливаем сцену

            stage.show(); // Показываем окно без ожидания его закрытия
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
