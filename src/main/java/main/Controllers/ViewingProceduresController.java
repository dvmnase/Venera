package main.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Services.ViewingProcedures_Service;
import main.Models.Entities.Procedure;

import java.util.List;

public class ViewingProceduresController {

    @FXML
    private VBox proceduresContainer; // Контейнер для динамического добавления процедур

    @FXML
    private Hyperlink HyperLinkExit;

    public void initialize() {
        fetchAndDisplayProcedures();
    }

    public void Exit_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) HyperLinkExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Admin.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    private void fetchAndDisplayProcedures() {
        ViewingProcedures_Service service = new ViewingProcedures_Service();
        List<Procedure> procedures = service.getProcedures();

        for (Procedure procedure : procedures) {
            addProcedureToUI(procedure);
        }
    }

    private void addProcedureToUI(Procedure procedure) {
        StackPane cell = new StackPane();
        cell.setPrefWidth(500);
        cell.setStyle("-fx-background-color: #003366; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

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

        textContainer.getChildren().addAll(titleLabel, descriptionLabel, durationLabel);
        cell.getChildren().add(textContainer);

        // Добавляем обработчик клика для удаления
        cell.setOnMouseClicked(event -> showDeleteConfirmation(procedure.getId()));

        cell.setOnMouseEntered(event -> cell.setStyle("-fx-background-color: #002033; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;"));
        cell.setOnMouseExited(event -> cell.setStyle("-fx-background-color: #003366; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;"));

        proceduresContainer.getChildren().add(cell);
    }

    private void deleteProcedure(int procedureId) {
        // Создание и отправка запроса на удаление
        DeleteProcedure_Controller service = new DeleteProcedure_Controller();
        service.deleteProcedure(procedureId);
    }

    private void showDeleteConfirmation(int procedureId) {
        // Создание нового диалогового окна
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Подтверждение удаления");

        // Основной контейнер для диалогового окна
        VBox dialogVbox = new VBox(20);
        dialogVbox.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0; -fx-background-radius: 10;");

        // Заголовок
        Label messageLabel = new Label("Уверены, что хотите удалить?");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #003366;");

        // Контейнер для кнопок
        HBox buttonContainer = new HBox(10); // Промежуток между кнопками
        buttonContainer.setStyle("-fx-alignment: center;"); // Установка выравнивания по центру

        // Кнопки "Да" и "Нет"
        Button yesButton = new Button("Да");
        Button noButton = new Button("Нет");

        yesButton.setStyle("-fx-background-color: #969898; -fx-text-fill: #000000; ");
        noButton.setStyle("-fx-background-color: #969898; -fx-text-fill: #000000; ");

        yesButton.setOnMouseEntered(event -> yesButton.setStyle("-fx-background-color: #c2d3e5; "));
        yesButton.setOnMouseExited(event -> yesButton.setStyle("-fx-background-color: #969898; "));

        noButton.setOnMouseEntered(event -> noButton.setStyle("-fx-background-color: #c2d3e5; "));
        noButton.setOnMouseExited(event -> noButton.setStyle("-fx-background-color: #969898; "));

        yesButton.setOnAction(e -> {
            deleteProcedure(procedureId);
            dialog.close();
        });

        noButton.setOnAction(e -> dialog.close());

        // Добавляем кнопки в контейнер
        buttonContainer.getChildren().addAll(yesButton, noButton);

        // Добавляем элементы в основной контейнер
        dialogVbox.getChildren().addAll(messageLabel, buttonContainer);

        // Создаем и отображаем сцену
        Scene dialogScene = new Scene(dialogVbox, 300, 150);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}