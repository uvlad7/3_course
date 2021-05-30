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
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Create implements Initializable {
    @FXML
    private Dialog<Map<String, String>> dialog;
    @FXML
    private TextField from;
    @FXML
    private TextField to;
    @FXML
    private JFXTimePicker departure;
    @FXML
    private JFXTimePicker arrival;
    @FXML
    private TextField number;
    @FXML
    private TextField cost;
    private NumberFormat format;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image(getClass().getResource("icons/filter.png").toExternalForm()));
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(resources.getLocale());
        boolean isAM = formatter.format(LocalTime.MIN).toLowerCase().contains("a");
        createTimePicker(departure, formatter, LocalTime.MIN, isAM);
        createTimePicker(arrival, formatter, LocalTime.MAX, isAM);
        format = NumberFormat.getInstance(resources.getLocale());
        dialog.setResultConverter(buttonType -> {
            try {
                if (buttonType.getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                    Map<String, String> ini = new HashMap<>();
                    ini.put("from", from.getText());
                    ini.put("to", to.getText());
                    ini.put("departure", formatter.format((departure.getValue())));
                    ini.put("arrival", formatter.format(arrival.getValue()));
                    try {
                        ini.put("cost", Double.valueOf(cost.getText()).toString());
                    } catch (NumberFormatException e) {
                        ini.put("cost", Double.toString(format.parse(cost.getText()).doubleValue()));
                    }
                    ini.put("number", Integer.valueOf(number.getText()).toString());
                    return ini;
                }
            } catch (NumberFormatException | ParseException e) {
                return null;
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
