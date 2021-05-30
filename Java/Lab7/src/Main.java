import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setUserAgentStylesheet(STYLESHEET_MODENA);
        Controller controller = new Controller(primaryStage);
        Scene scene = new Scene(controller.getPane(), 900, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        if (controller.dark()) {
            scene.getStylesheets().add(getClass().getResource("darcula.css").toExternalForm());
        }
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResource("icons/icon.png").toExternalForm()));
        primaryStage.sizeToScene();
        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            if (!controller.onClose()) {
                e.consume();
            }
        });
        primaryStage.show();
        primaryStage.requestFocus();
    }
}
