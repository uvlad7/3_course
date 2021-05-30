import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.*;

public class Settings implements Initializable {
    @FXML
    private Dialog<Map<String, String>> dialog;
    @FXML
    private ComboBox<String> language;
    @FXML
    private ToggleSwitch darkMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image(getClass().getResource("icons/settings.png").toExternalForm()));
        dialog.setResultConverter(buttonType -> {
            if (buttonType.getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                Map<String, String> ini = new HashMap<>();
                ini.put("dark", Boolean.toString(darkMode.isSelected()));
                ini.put("lang", language.getValue());
                return ini;
            }
            return null;
        });
    }

    public void setIni(boolean dark, Collection<String> languages, String selectedLanguage) {
        if (dark) {
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("darcula.css").toExternalForm());
            darkMode.setSelected(true);
        }
        language.setItems(FXCollections.observableArrayList(languages));
        language.setValue(selectedLanguage);
    }
}
