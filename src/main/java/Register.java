import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.event.ActionEvent;
import com.google.gson.Gson; // Не забудьте импортировать Gson
import main.Controllers.ErrorDialogController;
import main.Models.Entities.Client;
import main.Models.Entities.User;
import main.Models.TCP.Request;
import main.Enums.RequestType; // Импортируйте RequestType
import main.Services.RegistrationService;
import main.Utility.ClientSocket;
import main.Validation.InputValidation.PasswordValidator;
import main.Validation.InputValidation.PhoneValidator;

public class Register {
    @FXML
    private Button buttonRegister;
    @FXML
    private TextField textFieldLogin;
    @FXML
    private TextField textFieldSurname;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldPhone;
    @FXML
    private PasswordField passwordFieldPassword;
    @FXML
    private PasswordField passwordFieldConfirmPassword;
    @FXML
    private RadioButton cbFemale;
    @FXML
    private RadioButton cbMale;

    public void Login_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) buttonRegister.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("login6.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void Signup_Pressed(ActionEvent actionEvent) throws IOException {
        String login = textFieldLogin.getText().trim();
        String password = passwordFieldPassword.getText().trim();
        String confirmPassword = passwordFieldConfirmPassword.getText().trim();
        String name = textFieldName.getText().trim();
        String surname = textFieldSurname.getText().trim();
        String phone = textFieldPhone.getText().trim();

        // Проверка корректности введенных данных
        if (login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || surname.isEmpty() || phone.isEmpty()) {
            ErrorDialogController.showErrorDialog("Все поля должны быть заполнены");
            return;
        }

        if (!PasswordValidator.isValid(password, confirmPassword)) {
            ErrorDialogController.showErrorDialog("Пароль должен содержать не менее 6 символов и совпадать с подтверждением");
            return;
        }

        if (!PhoneValidator.isValid(phone)) {
            ErrorDialogController.showErrorDialog("Номер телефона некорректный (+375 XX XXX XX XX)");
            return;
        }

        // Создание пользователя и отправка данных на сервер
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setRole("CLIENT");

        Client personData = new Client();
        personData.setName(name);
        personData.setSurname(surname);
        personData.setPhone(phone);

        personData.setSex(cbFemale.isSelected() ? "FEMALE" : "MALE");

        user.setPersonData(personData);

        // Используем сервис регистрации для отправки данных
        RegistrationService registrationService = new RegistrationService();
        registrationService.registerUser(user);
    }




}