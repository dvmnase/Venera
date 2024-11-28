package main.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.Models.Entities.Procedure;
import main.Utility.Session;

import java.io.IOException;
import java.util.List;

public class ProcedureListController {

    @FXML
    private ListView<Procedure> procedureListView;

    private Session user;  // Инициализация переменной для сессии

    // Метод инициализации, вызывается автоматически при запуске контроллера
    public void initialize() {
        // Инициализация сессии для получения текущего пользователя
        user = Session.getInstance();  // Получаем текущего пользователя из сессии

        // Настройка cellFactory для кастомного отображения
        procedureListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Procedure> call(ListView<Procedure> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Procedure procedure, boolean empty) {
                        super.updateItem(procedure, empty);

                        if (empty || procedure == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            // Создаем визуальные элементы для отображения
                            //Text title = new Text(String.valueOf(user.getUserId()));

                             Text title = new Text(procedure.getTitle());
                            title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;"); // Название жирным и больше

                            Text description = new Text(procedure.getDescription());
                            description.setStyle("-fx-font-size: 12px;"); // Описание меньше

                            Text details = new Text(
                                    String.format("Длительность: %d мин   Цена: %.2f BYN",
                                            procedure.getDuration(),
                                            procedure.getPrice())
                            );
                            details.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"); // Время и цена среднего размера

                            // Размещаем элементы в вертикальном контейнере
                            VBox vbox = new VBox(title, description, details);
                            vbox.setSpacing(5); // Отступы между строками

                            setGraphic(vbox);
                        }
                    }
                };
            }
        });

        // Добавляем обработчик выбора элемента
        procedureListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                openEmployeeSelectionWindow(newValue);
            }
        });
    }

    public void setProcedures(List<Procedure> procedures) {
        procedureListView.getItems().addAll(procedures);
    }

    private void openEmployeeSelectionWindow(Procedure procedure) {
        try {
            // Загружаем FXML для нового окна
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EmployeeSelectionClient.fxml"));
            Parent root = loader.load();

            EmployeeSelectionForClient_Controller controller = loader.getController();
            System.out.println("Передаем в контроллер процедуру: " + procedure.getTitle() + " и clientId: " + user.getUserId());
            controller.setProcedureAndClient(procedure, user.getUserId()); // Передаем выбранную процедуру и userId

            // Настраиваем и отображаем новое окно
            Stage stage = new Stage();
            stage.setTitle("Выбор сотрудников");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

