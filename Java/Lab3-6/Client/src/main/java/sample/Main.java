package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.controllers.Controller;

import java.io.InputStreamReader;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        Properties properties = new Properties();
        properties.load(new InputStreamReader(getClass().getResourceAsStream("/resource.properties")));
        controller.setProperties(properties);
        primaryStage.setTitle("Word");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(getClass().getResource("/icon.png").toExternalForm()));
        primaryStage.sizeToScene();
        primaryStage.show();
        primaryStage.requestFocus();
    }
}
