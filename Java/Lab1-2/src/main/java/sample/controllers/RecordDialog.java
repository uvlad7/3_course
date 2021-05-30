package sample.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class RecordDialog implements Initializable, DialogController {
    @FXML
    private Dialog<Map<String, String>> dialog;
    @FXML
    private TextField name;
    @FXML
    private TextField def;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialog.setResultConverter(buttonType -> {
            if (buttonType.getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                Map<String, String> ini = new HashMap<>();
                if (name.getText().equals("") || def.getText().equals(""))
                    return null;
                ini.put("name", name.getText());
                ini.put("def", def.getText());
                return ini;
            }
            return null;
        });
    }

    public void init(Map<String, String> init) {
        name.setText(init.get("name"));
        def.setText(init.get("def"));
    }
}
