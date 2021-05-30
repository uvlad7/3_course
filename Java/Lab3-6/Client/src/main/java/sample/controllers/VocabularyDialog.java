package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class VocabularyDialog implements Initializable, DialogController {
    @FXML
    private Dialog<Map<String, String>> dialog;
    @FXML
    private TextField name;
    @FXML
    private CheckBox isExplanatory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialog.setResultConverter(buttonType -> {
            if (buttonType.getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                Map<String, String> ini = new HashMap<>();
                if (name.getText().equals(""))
                    return null;
                ini.put("name", name.getText());
                ini.put("type", Boolean.toString(isExplanatory.isSelected()));
                return ini;
            }
            return null;
        });
    }

    public void init(Map<String, String> init) {
        name.setText(init.get("name"));
        isExplanatory.setSelected(Boolean.parseBoolean(init.get("type")));
    }
}
