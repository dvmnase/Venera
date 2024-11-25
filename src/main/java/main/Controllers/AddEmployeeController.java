package main.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.Models.Entities.Client;
import main.Models.Entities.Employee;
import main.Models.Entities.Procedure;
import main.Models.Entities.User;
import main.Services.AddEmployee_Service;
import main.Services.AddProcedure_Service;
import main.Services.RegistrationService;
import main.Validation.InputValidation.PhoneValidator;
import org.w3c.dom.Text;

public class AddEmployeeController {

    @FXML
    private Button ButtonAdd;
    @FXML
    private Button ButtonExit;

    @FXML
    private TextField textFieldLogin;
    @FXML
    private PasswordField textFieldPassword;
    @FXML
    private TextField textFieldSurname;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldPhone;
    @FXML
    private TextArea textAreaShared_data;
    @FXML
    private TextField textFieldSpecialization;
    @FXML
    private RadioButton cbFemale;
    @FXML
    private RadioButton cbMale;

    public void Exit_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) ButtonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Admin.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void Add_Pressed(ActionEvent actionEvent) throws Exception {

        String surname = textFieldSurname.getText().trim();
        String name = textFieldName.getText().trim();
        String phone = textFieldPhone.getText().trim();
        String specialization = textFieldSpecialization.getText().trim();
        String shares_data = textAreaShared_data.getText().trim();
        String login = textFieldLogin.getText().trim();
        String password = textFieldPassword.getText().trim();


        if (surname.isEmpty() || name.isEmpty() || phone.isEmpty() || specialization.isEmpty() || shares_data.isEmpty() || login.isEmpty() || password.isEmpty()) {
            ErrorDialogController.showErrorDialog("Все поля должны быть корректно заполнены");
            return;
        }

        if (!PhoneValidator.isValid(phone)) {
            ErrorDialogController.showErrorDialog("Номер телефона некорректный (+375 XX XXX XX XX)");
            return;
        }


        // Создание пользователя и отправка данных на сервер
        // Создание пользователя и отправка данных на сервер
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setRole("EMPLOYEE");

        Employee personData = new Employee();
        personData.setName(name);
        personData.setSurname(surname);
        personData.setPhone(phone);
        personData.setShared_data(shares_data);
        personData.setSpecialization(specialization);
        personData.setSex(cbFemale.isSelected() ? "FEMALE" : "MALE");

        user.setEmployeeData(personData);

        //получили Stage окна регистрации чтобы потом иметь доступ и закрыть

        AddEmployee_Service addEmployeeService = new AddEmployee_Service();
        addEmployeeService.AddEmployee(user);




    }
}
