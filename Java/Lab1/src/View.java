import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.dialog.FontSelectorDialog;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    private MenuItem undo;
    @FXML
    private MenuItem redo;
    @FXML
    private MenuItem correct;
    @FXML
    private MenuItem settings;
    @FXML
    private CheckMenuItem wrapText;
    @FXML
    private MenuItem font;
    @FXML
    private TextArea textArea;
    private Controller controller;
    private Stage stage;
    private FileChooser fileChooser;
    private ResourceBundle resources;
    private File selectedFile;

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
        textArea.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            controller.onChange();
            undo.setDisable(false);
        });
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (undo.getAccelerator().match(event)) {
                event.consume();
                undoAction();
            } else if (redo.getAccelerator().match(event)) {
                event.consume();
                redoAction();
            }
        });
        textArea.wrapTextProperty().bindBidirectional(wrapText.selectedProperty());
        textArea.wrapTextProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                controller.setWrap(newValue);
            }
        });
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(resources.getString("textFiles"), "*.txt"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
    }

    @FXML
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == undo) {
            undoAction();
        } else if (source == redo) {
            redoAction();
        } else if (source == open) {
            openFile();
        } else if (source == create) {
            createFile();
        } else if (source == save) {
            saveFile();
        } else if (source == saveAs) {
            saveAsFile();
        } else if (source == correct) {
            controller.correct();
        } else if (source == settings) {
            controller.settings();
        } else if (source == font) {
            controller.setFont();
        }
    }

    private void undoAction() {
        textArea.undo();
        redo.setDisable(false);
        if (!textArea.undoableProperty().getValue()) {
            undo.setDisable(true);
        }
    }

    private void redoAction() {
        textArea.redo();
        undo.setDisable(false);
        if (!textArea.redoableProperty().getValue()) {
            redo.setDisable(true);
        }
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
            Dialog<Map<String, String>> settings = loader.<Dialog<Map<String, String>>>load();
            Settings settingsController = loader.getController();
            settingsController.setIni(dark, languages, selectedLanguage);
            Optional<Map<String, String>> answer = settings.showAndWait();
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

    public Optional<Font> showSelectFont(Font font) {
        FontSelectorDialog dialog = new FontSelectorDialog(font);
        customize(dialog);
        return dialog.showAndWait();
    }

    public String getText() {
        return textArea.getText();
    }

    public void setText(String text) {
        textArea.setText(text);
        undo.setDisable(true);
        redo.setDisable(true);
    }

    public void setFont(Font font) {
        textArea.setFont(font);
    }

    public void replaceText(String text) {
        textArea.replaceText(0, textArea.getText().length(), text);
    }

    public void setWrapText(boolean value) {
        textArea.setWrapText(value);
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
        dialog.setTitle("CaPs FixEr");
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().addAll(stage.getIcons());
        dialog.getDialogPane().getScene().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        if (controller.dark()) {
            dialog.getDialogPane().getScene().getStylesheets().add(getClass().getResource("darcula.css").toExternalForm());
        }
    }

    private void showWait() {
        Platform.runLater(() -> {
            textArea.setMouseTransparent(true);
            menuBar.setMouseTransparent(true);
            stage.getScene().setCursor(Cursor.WAIT);
        });
    }

    private void hideWait() {
        Platform.runLater(() -> {
            textArea.setMouseTransparent(false);
            menuBar.setMouseTransparent(false);
            stage.getScene().setCursor(Cursor.DEFAULT);
        });
    }
}
