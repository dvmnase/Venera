package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Utility.ClientSocket;

import java.util.Scanner;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{

        ClientSocket.getInstance().getSocket();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login6.fxml"));
        Parent root = loader.load();
       // Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.setTitle("Venera");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


}
