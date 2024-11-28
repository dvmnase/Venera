
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import com.google.gson.Gson;
import main.Controllers.ErrorDialogController;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Enums.RequestType;
import main.Utility.ClientSocket;
import main.Models.Entities.User;
import main.Services.LoginService;
import main.Utility.Session;

public class Login {

    @FXML
    private Button buttonLogin;
    @FXML
    private TextField textFieldLogin; // Поле для ввода логина
    @FXML
    private PasswordField passwordField; // Поле для ввода пароля

    private LoginService loginService; // Сервис для авторизации

    public Login() {
        loginService = new LoginService(); // Инициализация сервиса
    }

    public void Login_Pressed(ActionEvent actionEvent) {
        String login = textFieldLogin.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty() || password.isEmpty()) {
            ErrorDialogController.showErrorDialog("заполните все поля");
            return;
        }

        // Создание объекта запроса для авторизации
        Request request = new Request();
        request.setRequestType(RequestType.LOGIN);
        request.setRequestMessage(new Gson().toJson(new User(login, password)));


        Response response = loginService.login(request); // Используем сервис для авторизации



        // При успешной авторизации можно перейти на другой экран
        if ("SUCCESS".equals(response.getStatus())) {
            int userId = response.getUserId(); // Получаем ID из ответа
            String role = response.getRole();

            // Сохраняем данные в сессии
            Session session = Session.getInstance();
            session.setUserId(userId);
            session.setRole(role);

            try {
                Stage stage = (Stage) buttonLogin.getScene().getWindow();
                Parent root;
                if ("CLIENT".equals(role)) {
                    root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
                } else if ("ADMIN".equals(role)) {
                    root = FXMLLoader.load(getClass().getResource("/Admin.fxml"));
                } else if ("EMPLOYEE".equals(role)) {
                    root = FXMLLoader.load(getClass().getResource("Employee.fxml"));
                } else {
                    throw new IllegalArgumentException("Unknown user role: " + role);
                }
                Scene newScene = new Scene(root);
                stage.setScene(newScene);
            } catch (Exception e) {
                e.printStackTrace();
                ErrorDialogController.showErrorDialog("не удалось открыть главное окно");
            }
        } else {
            ErrorDialogController.showErrorDialog("Неверные данные");
        }

    }

    public void Register_Pressed(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) buttonLogin.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("registr.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }


}