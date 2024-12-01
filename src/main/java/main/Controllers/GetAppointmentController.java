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
import javafx.stage.Stage;
import main.Models.Entities.Appointment;
import main.Models.Entities.Employee;
import main.Models.Entities.Procedure;
import main.Services.GetAppointment_Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetAppointmentController {

    @FXML
    private VBox proceduresContainer;

    @FXML
    private Hyperlink HyperLinkExit;

    public void initialize() {
        fetchAndDisplayAppointments();
    }

    public void Exit_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) HyperLinkExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void fetchAndDisplayAppointments() {
        GetAppointment_Service service = new GetAppointment_Service();
        List<Appointment> appointments = service.getAppointmentsForClient();

        for (Appointment appointment : appointments) {
            addAppointmentToUI(appointment);
        }
    }

    private void addAppointmentToUI(Appointment appointment) {
        Procedure procedure = appointment.getProcedure();
        Employee employee = appointment.getEmployee();

        StackPane cell = new StackPane();
        cell.setPrefWidth(500);
        cell.setStyle("-fx-background-color: #003366; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Добавляем эффекты при наведении
        cell.setOnMouseEntered(e -> cell.setStyle("-fx-background-color: #005599; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;"));
        cell.setOnMouseExited(e -> cell.setStyle("-fx-background-color: #003366; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;"));

        VBox textContainer = new VBox();
        textContainer.setSpacing(5);

        Label titleLabel = new Label(procedure.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white;");

        Label descriptionLabel = new Label(procedure.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        String durationText = procedure.getDuration() + " минут";
        String priceText = procedure.getPrice() + " BYN";
        Label durationLabel = new Label(durationText + "      " + priceText);
        durationLabel.setStyle("-fx-font-style: italic; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label employeeLabel = new Label("Мастер: " + employee.getName() + " " + employee.getSurname());
        employeeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-style: italic;");

        LocalDateTime appointmentDate = appointment.getAppointmentDate();
        String dateText = appointmentDate != null
                ? "Дата: " + appointmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : "Дата: Не указана";

        Label dateLabel = new Label(dateText);
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-style: italic;");

        textContainer.getChildren().addAll(titleLabel, descriptionLabel, durationLabel, employeeLabel, dateLabel);
        cell.getChildren().add(textContainer);

        // Добавляем обработчик клика
        cell.setOnMouseClicked(e -> openAppointmentModal(appointment));

        proceduresContainer.getChildren().add(cell);
    }

    private void openAppointmentModal(Appointment appointment) {
        Stage modalStage = new Stage();
        modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox modalContent = new VBox();
        modalContent.setSpacing(20);
        modalContent.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0; -fx-border-radius: 10;");

        Label titleLabel = new Label("Детали записи");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label detailsLabel = new Label(
                "Процедура: " + appointment.getProcedure().getTitle() + "\n" +
                        "Мастер: " + appointment.getEmployee().getName() + " " + appointment.getEmployee().getSurname() + "\n" +
                        "Дата: " + (appointment.getAppointmentDate() != null
                        ? appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        : "Не указана")
        );
        detailsLabel.setStyle("-fx-font-size: 14px;");

        // Кнопки
        javafx.scene.control.Button editButton = new javafx.scene.control.Button("Изменить");
        javafx.scene.control.Button cancelButton = new javafx.scene.control.Button("Отменить");
        javafx.scene.control.Button backButton = new javafx.scene.control.Button("Назад");

        editButton.setOnAction(e -> {
            // Логика изменения записи
            System.out.println("Изменение записи");
        });

        cancelButton.setOnAction(e -> {
            // Логика отмены записи
            System.out.println("Отмена записи");
        });

        backButton.setOnAction(e -> modalStage.close());

        VBox buttonsContainer = new VBox(editButton, cancelButton, backButton);
        buttonsContainer.setSpacing(10);
        buttonsContainer.setStyle("-fx-alignment: center;");

        modalContent.getChildren().addAll(titleLabel, detailsLabel, buttonsContainer);

        Scene modalScene = new Scene(modalContent, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.showAndWait();
    }


}
