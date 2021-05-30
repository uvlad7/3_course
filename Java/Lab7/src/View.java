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
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Consumer;


public class View implements Initializable {
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem create;
    @FXML
    private MenuItem connect;
    @FXML
    private MenuItem settings;
    @FXML
    private TableView<Train> table;
    private Controller controller;
    private Stage stage;
    private ResourceBundle resources;
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
        if (source == connect) {
            openSchema();
        } else if (source == create) {
            createSchema();
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

    public Map<String, String> createTrain(boolean dark) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(getClass().getResource("Create.fxml"));
            Dialog<Map<String, String>> trainDialog = loader.<Dialog<Map<String, String>>>load();
            Create createController = loader.getController();
            createController.setIni(dark);
            Optional<Map<String, String>> answer = trainDialog.showAndWait();
            if (answer.isPresent()) {
                return answer.get();
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public Optional<Map<String, String>> showConnect(boolean dark, Map<String, String> def) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resources);
            loader.setLocation(getClass().getResource("Connect.fxml"));
            Dialog<Map<String, String>> connectDialog = loader.<Dialog<Map<String, String>>>load();
            Connect connectController = loader.getController();
            connectController.setIni(dark, def);
            return connectDialog.showAndWait();
        } catch (IOException ignored) {
        }
        return Optional.empty();
    }

    private void openSchema() {
        showWait();
        controller.open();
        hideWait();
    }

    private void createSchema() {
        showWait();
        controller.create();
        hideWait();
    }

    public boolean onClose() {
        return confirmationDialog(resources.getString("onClose")).map(buttonType -> buttonType.equals(ButtonType.OK)).orElse(false);
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

    public Optional<ButtonType> confirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        customize(alert);
        alert.setHeaderText(message);
        return alert.showAndWait();
    }

    private Optional<ButtonType> dialog(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        customize(alert);
        alert.setHeaderText(message);
        alert.getButtonTypes().setAll(new ButtonType(resources.getString("okAction"), ButtonBar.ButtonData.OK_DONE));
        return alert.showAndWait();
    }

    private void customize(Dialog<?> dialog) {
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
