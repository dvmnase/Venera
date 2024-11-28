package main.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Models.Entities.Employee;
import main.Models.Entities.Payment;
import main.Models.Entities.Procedure;
import main.Services.AddPayment_Service;
import main.Services.EmployeeSelectionForClient_Service;
import main.Validation.InputValidation.AppointmentValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EmployeeSelectionForClient_Controller {

    @FXML
    private ListView<Employee> servicesListView;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    private Procedure procedure;
    private List<Employee> availableServices;
    private int clientId; // ID клиента, передается при авторизации

    public void setProcedureAndClient(Procedure pressedProcedure, int clientId) {
        this.procedure = pressedProcedure;
        this.clientId = clientId;
        fetchAndDisplayEmployees();
    }

    private void fetchAndDisplayEmployees() {
        EmployeeSelectionForClient_Service service = new EmployeeSelectionForClient_Service();
        availableServices = service.getEmployeesForProcedure(procedure.getId());

        System.out.println("Fetched employees: " + availableServices); // Для проверки данных

        if (availableServices != null && !availableServices.isEmpty()) {
            servicesListView.getItems().clear(); // Очистка перед добавлением
            servicesListView.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(Employee employee, boolean empty) {
                    super.updateItem(employee, empty);

                    if (empty || employee == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Text nameSurname = new Text(employee.getName() + " " + employee.getSurname());
                        nameSurname.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                        Text specialization = new Text(employee.getSpecialization());
                        specialization.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                        Text sharedData = new Text(employee.getShared_data());
                        sharedData.setStyle("-fx-font-size: 12px;");

                        VBox vbox = new VBox(nameSurname, specialization, sharedData);
                        vbox.setSpacing(5);

                        setGraphic(vbox);
                    }
                }
            });
            servicesListView.getItems().addAll(availableServices); // Добавление данных
        } else {
            System.out.println("No employees found for procedure ID: " + procedure.getId());
        }
    }

    @FXML
    private void onCancelPressed(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onOkPressed(ActionEvent event) {
        int selectedIndex = servicesListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex == -1) {
            showAlert("Ошибка", "Выберите сотрудника из списка!", Alert.AlertType.WARNING);
            return;
        }

        Employee selectedEmployee = availableServices.get(selectedIndex);

        // Получение даты как LocalDateTime
        LocalDateTime selectedDate = promptForDate();
        if (selectedDate == null) {
            showAlert("Ошибка", "Дата записи не выбрана или формат неверный!", Alert.AlertType.WARNING);
            return;
        }

        String paymentMethod = promptForPaymentMethod(procedure.getPrice());
        if (paymentMethod == null || paymentMethod.isBlank()) {
            showAlert("Ошибка", "Метод оплаты не выбран!", Alert.AlertType.WARNING);
            return;
        }



        // Создаем запись с процедурой
        EmployeeSelectionForClient_Service service = new EmployeeSelectionForClient_Service();

        // Создаем объект Payment с ценой и выбранным методом оплаты

        int appointmentId = service.createAppointment(clientId, selectedEmployee.getId(), procedure.getId(), selectedDate);
System.out.println("Appointment "+appointmentId);

        if (appointmentId != -1) {

            Payment payment = new Payment();
            payment.setAmount(procedure.getPrice().floatValue());
            payment.setPaymentMethod(paymentMethod);
            // Если создание записи прошло успешно, отправляем данные для платежа
            payment.setAppointment_id(appointmentId);
            AddPayment_Service paymentService = new AddPayment_Service();
            paymentService.AddPayment(payment);

            showAlert("Успех", "Запись успешно добавлена и оплата оформлена!", Alert.AlertType.INFORMATION);
        }
        else {
            showAlert("Ошибка", "Не удалось добавить запись. Попробуйте еще раз.", Alert.AlertType.ERROR);
        }

        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    // @FXML
    private LocalDateTime promptForDate() {
        TextInputDialog dateDialog = new TextInputDialog();
        dateDialog.setTitle("Выбор даты");
        dateDialog.setHeaderText("Введите дату записи");
        dateDialog.setContentText("Дата (YYYY-MM-DD HH:mm):");

        // Получение даты от пользователя
        String input = dateDialog.showAndWait().orElse(null);
        if (input == null || input.isBlank()) {
            return null; // Пользователь ничего не ввел
        }

        // Валидация даты и времени
        if (!AppointmentValidator.isValidDateTime(input)) {
            ErrorDialogController.showErrorDialog("Неверный формат даты/времени или дата в прошлом. Время записи должно быть с 10:00 до 19:00.");
            return null;
        }

        // Преобразование строки в LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(input, formatter);
    }

    private String promptForPaymentMethod(double price) {
        ChoiceDialog<String> paymentDialog = new ChoiceDialog<>("CASH", "CARD", "ONLINE");
        paymentDialog.setTitle("Оформление платежа");
        paymentDialog.setHeaderText("Выберите способ оплаты");
        paymentDialog.setContentText("Стоимость: " + price + " BYN\nМетод оплаты:");

        return paymentDialog.showAndWait().orElse(null);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
