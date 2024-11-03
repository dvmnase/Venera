import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.io.IOException;
import javafx.event.ActionEvent;
import com.google.gson.Gson; // Не забудьте импортировать Gson
import main.Models.Entities.Client;
import main.Models.Entities.User;
import main.Models.TCP.Request;
import main.Enums.RequestType; // Импортируйте RequestType
import main.Utility.ClientSocket;

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
        User user = new User();
        user.setLogin(textFieldLogin.getText());
        user.setPassword(passwordFieldPassword.getText());
        user.setRole("Client");

        Client personData = new Client();
        personData.setName(textFieldName.getText());
        personData.setSurname(textFieldSurname.getText());
        personData.setPhone(textFieldPhone.getText());

        if (cbFemale.isSelected())
            personData.setSex("Ж");
        else
            personData.setSex("М");

        user.setPersonData(personData);

        // Создание запроса
        Request request = new Request();
        request.setRequestType(RequestType.REGISTER);
        request.setRequestMessage(new Gson().toJson(user));

        //ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        //ClientSocket.getInstance().getOut().flush();


    }
}