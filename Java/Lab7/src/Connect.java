import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Connect implements Initializable {
    @FXML
    private Dialog<Map<String, String>> dialog;
    @FXML
    private TextField url;
    @FXML
    private TextField schema;
    @FXML
    private TextField user;
    @FXML
    private PasswordField password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image(getClass().getResource("icons/filter.png").toExternalForm()));

        dialog.setResultConverter(buttonType -> {
            if (buttonType.getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                Map<String, String> ini = new HashMap<>();
                ini.put("url", url.getText());
                ini.put("schema", schema.getText());
                ini.put("user", user.getText());
                ini.put("password", password.getText());
                return ini;
            }
            return null;
        });
    }

    public void setIni(boolean dark, Map<String, String> params) {
        if (dark) {
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("darcula.css").toExternalForm());
        }
        url.setText(params.get("url"));
        schema.setText(params.get("schema"));
        user.setText(params.get("user"));
        password.setText(params.get("password"));
    }
}
