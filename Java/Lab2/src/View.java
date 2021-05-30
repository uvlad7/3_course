import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;


public class View implements Initializable {
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem open;
    @FXML
    private MenuItem create;
    @FXML
    private MenuItem save;
    @FXML
    private MenuItem saveAs;
    @FXML
    private MenuItem settings;
    @FXML
    private TableView<Train> table;
    private Controller controller;
    private Stage stage;
    private FileChooser fileChooser;
    private ResourceBundle resources;
    private File selectedFile;
    @FXML
    private TableColumn<Train, Integer> number;
    @FXML
    private TableColumn<Train, String> from;
    @FXML
    private TableColumn<Train, String> to;
    @FXML
    private TableColumn<Train, LocalTime> departure;
    @FXML
    private TableColumn<Train, LocalTime> arrival;
    @FXML
    private TableColumn<Train, String> cost;
    @FXML
    private MenuItem add;
    @FXML
    private MenuItem delete;
    @FXML
    private MenuItem filter;
    private NumberFormat formatter;

    public View() {

    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER && table.getItems().isEmpty()) {
                event.consume();
            }
        });
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(resources.getString("binFiles"), "*.bin"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        number.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Integer object) {
                return object.toString();
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException ignored) {
                }
                return 0;
            }
        }));
        number.setOnEditCommit(event -> {
            final Integer value = event.getNewValue() != null ?
                    event.getNewValue() : event.getOldValue();
            event.getTableView().getItems()
                    .get(event.getTablePosition().getRow()).setNumber(value);
            controller.onChange();
            table.refresh();
        });
        from.setCellFactory(TextFieldTableCell.forTableColumn());
        from.setOnEditCommit(event -> {
            final String value = event.getNewValue() != null ?
                    event.getNewValue() : event.getOldValue();
            event.getTableView().getItems()
                    .get(event.getTablePosition().getRow()).setFrom(value);
            controller.onChange();
            table.refresh();
        });
        to.setCellFactory(TextFieldTableCell.forTableColumn());
        to.setOnEditCommit(event -> {
            final String value = event.getNewValue() != null ?
                    event.getNewValue() : event.getOldValue();
            event.getTableView().getItems()
                    .get(event.getTablePosition().getRow()).setTo(value);
            controller.onChange();
            table.refresh();
        });
        departure.setCellFactory(list -> new TimePickerTableCell<>(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(resources.getLocale())));
        departure.setOnEditCommit(event -> {
            final LocalTime value = event.getNewValue() != null ?
                    event.getNewValue() : event.getOldValue();
            event.getTableView().getItems()
                    .get(event.getTablePosition().getRow()).setDeparture(value);
            controller.onChange();
            table.refresh();
        });
        arrival.setCellFactory(list -> new TimePickerTableCell<>(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(resources.getLocale())));
        arrival.setOnEditCommit(event -> {
            final LocalTime value = event.getNewValue() != null ?
                    event.getNewValue() : event.getOldValue();
            event.getTableView().getItems()
                    .get(event.getTablePosition().getRow()).setArrival(value);
            controller.onChange();
            table.refresh();
        });
        cost.setCellFactory(TextFieldTableCell.forTableColumn());
        formatter = NumberFormat.getInstance(resources.getLocale());
        cost.setOnEditCommit(event -> {
            final String value = event.getNewValue() != null ?
                    event.getNewValue() : event.getOldValue();
            try {
                event.getTableView().getItems()
                        .get(event.getTablePosition().getRow()).setCost(value);
            } catch (ParseException e) {
                try {
                    event.getTableView().getItems()
                            .get(event.getTablePosition().getRow()).setCost(formatter.parse(value).doubleValue());
                } catch (ParseException ignored) {
                }
            }
            controller.onChange();
            table.refresh();
        });
    }

    public void add(Train element) {
        table.getSelectionModel().clearSelection();
        table.getItems().add(element);
        table.scrollTo(element);
    }

    public void remove(Train element) {
        table.getSelectionModel().clearSelection();
        table.getItems().remove(element);
    }

    public void set(Collection<Train> elements) {
        table.getSelectionModel().clearSelection();
        table.setItems(FXCollections.observableArrayList(elements));
    }

    @FXML
    public void actionPerformed(Event actionEvent) {
        Object source = actionEvent.getSource();
        if (source == open) {
            openFile();
        } else if (source == create) {
            createFile();
        } else if (source == save) {
            saveFile();
        } else if (source == saveAs) {
            saveAsFile();
        } else if (source == settings) {
            controller.settings();
        } else if (source == add) {
            controller.add();
        } else if (source == delete) {
            controller.delete(table.getSelectionModel().getSelectedItem());
        } else if (source == filter) {
            controller.filter();
        }
    }

    public Map<String, String> showFilter(boolean dark) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(getClass().getResource("Filter.fxml"));
            Dialog<Map<String, String>> filterDialog = loader.<Dialog<Map<String, String>>>load();
            Filter filterController = loader.getController();
            filterController.setIni(dark);
            Optional<Map<String, String>> answer = filterDialog.showAndWait();
            if (answer.isPresent()) {
                return answer.get();
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public boolean saveFile() {
        boolean res;
        showWait();
        if (controller.save()) {
            res = true;
        } else {
            res = saveAsFile();
        }
        hideWait();
        return res;
    }

    private boolean saveAsFile() {
        selectedFile = fileChooser.showSaveDialog(stage);
        boolean res;
        if (selectedFile != null) {
            showWait();
            res = controller.saveAs(selectedFile);
            hideWait();
        } else {
            res = false;
        }
        return res;
    }

    private void openFile() {
        if (controller.onClose()) {
            selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                showWait();
                controller.open(selectedFile);
                hideWait();
            }
        }
    }

    private void createFile() {
        if (controller.onClose()) {
            showWait();
            controller.create();
            hideWait();
        }
    }

    public boolean saveOnClose(String filename) {
        switch (saveDialog(resources.getString("saveChanges").replace("filename", filename))) {
            case 0: {
                return saveFile();
            }
            case 1: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public Map<String, String> showSettings(boolean dark, Collection<String> languages, String selectedLanguage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(getClass().getResource("Settings.fxml"));
            Dialog<Map<String, String>> settingsDialog = loader.<Dialog<Map<String, String>>>load();
            Settings settingsController = loader.getController();
            settingsController.setIni(dark, languages, selectedLanguage);
            Optional<Map<String, String>> answer = settingsDialog.showAndWait();
            if (answer.isPresent()) {
                if (!answer.get().get("lang").equals(selectedLanguage)) {
                    dialog(Alert.AlertType.INFORMATION, resources.getString("reloadApp"));
                }
                return answer.get();
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public void warningDialog(String message) {
        dialog(Alert.AlertType.WARNING, message);
    }

    private Optional<ButtonType> dialog(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        customize(alert);
        alert.setHeaderText(message);
        alert.getButtonTypes().setAll(new ButtonType(resources.getString("okAction"), ButtonBar.ButtonData.OK_DONE));
        return alert.showAndWait();
    }

    public int saveDialog(String message) {
        ButtonType[] buttons = new ButtonType[]{new ButtonType(resources.getString("saveAction"), ButtonBar.ButtonData.YES),
                new ButtonType(resources.getString("notSaveAction"), ButtonBar.ButtonData.NO),
                new ButtonType(resources.getString("cancelAction"), ButtonBar.ButtonData.CANCEL_CLOSE)};
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        customize(alert);
        alert.setHeaderText(message);
        alert.getButtonTypes().setAll(buttons);
        Button firstButton = (Button) alert.getDialogPane().lookupButton(buttons[0]);
        firstButton.setDefaultButton(true);
        firstButton.requestFocus();
        Optional<ButtonType> result = alert.showAndWait();
        return result.map(buttonType -> Arrays.asList(buttons).indexOf(buttonType)).orElse(-1);
    }

    private void customize(Dialog dialog) {
        dialog.setTitle("Расписание движения поездов");
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().addAll(stage.getIcons());
        dialog.getDialogPane().getScene().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        if (controller.dark()) {
            dialog.getDialogPane().getScene().getStylesheets().add(getClass().getResource("darcula.css").toExternalForm());
        }
    }

    private void showWait() {
        Platform.runLater(() -> {
            table.setMouseTransparent(true);
            menuBar.setMouseTransparent(true);
            stage.getScene().setCursor(Cursor.WAIT);
        });
    }

    private void hideWait() {
        Platform.runLater(() -> {
            table.setMouseTransparent(false);
            menuBar.setMouseTransparent(false);
            stage.getScene().setCursor(Cursor.DEFAULT);
        });
    }
}
