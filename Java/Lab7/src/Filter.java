import com.jfoenix.controls.JFXTimePicker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Filter implements Initializable {
    @FXML
    private Dialog<Map<String, String>> dialog;
    @FXML
    private TextField from;
    @FXML
    private TextField to;
    @FXML
    private JFXTimePicker departure_from;
    @FXML
    private JFXTimePicker departure_to;
    @FXML
    private JFXTimePicker arrival_from;
    @FXML
    private JFXTimePicker arrival_to;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image(getClass().getResource("icons/filter.png").toExternalForm()));
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(resources.getLocale());
        boolean isAM = formatter.format(LocalTime.MIN).toLowerCase().contains("a");
        createTimePicker(departure_from, formatter, LocalTime.MIN, isAM);
        createTimePicker(departure_to, formatter, LocalTime.MAX, isAM);
        createTimePicker(arrival_from, formatter, LocalTime.MIN, isAM);
        createTimePicker(arrival_to, formatter, LocalTime.MAX, isAM);
        dialog.setResultConverter(buttonType -> {
            if (buttonType.getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                Map<String, String> ini = new HashMap<>();
                ini.put("from", from.getText());
                ini.put("to", to.getText());
                ini.put("departure_from", formatter.format((departure_from.getValue())));
                ini.put("departure_to", formatter.format(departure_to.getValue()));
                ini.put("arrival_from", formatter.format(arrival_from.getValue()));
                ini.put("arrival_to", formatter.format(arrival_to.getValue()));
                return ini;
            }
            return null;
        });
    }

    public void setIni(boolean dark) {
        if (dark) {
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("darcula.css").toExternalForm());
        }
    }

    private void createTimePicker(JFXTimePicker timePicker, DateTimeFormatter formatter, LocalTime time, boolean isAM) {
        timePicker.setConverter(new LocalTimeStringConverter(formatter, null));
        timePicker.getEditor().setDisable(true);
        timePicker.set24HourView(!isAM);
        timePicker.setValue(time);
    }
}
