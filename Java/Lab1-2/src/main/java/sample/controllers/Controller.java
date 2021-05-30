package sample.controllers;

import com.sun.istack.internal.Nullable;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.dao.DataConnection;
import sample.dao.impl.MySQLDataConnection;
import sample.entity.Record;
import sample.entity.Vocabulary;
import sample.entity.Word;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Controller implements Initializable {
    final AtomicInteger poolNumber = new AtomicInteger(1);
    @FXML
    private Menu indicator;
    @FXML
    private MenuBar menu;
    @FXML
    private Menu vocabularies;
    @FXML
    private ListView<Record> view;
    private Properties properties;
    private DataConnection connection;
    private List<Vocabulary> vocabularyList;
    private List<MenuItem> vocabularyMenu;
    private ObservableList<Record> records;
    private Vocabulary selectedVocabulary;
    private String searchWord;
    private ExecutorService databaseExecutor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        records = view.getItems();
        view.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        databaseExecutor = Executors.newFixedThreadPool(1, r -> {
            Thread thread = new Thread(r, "Database-Connection-" + poolNumber.getAndIncrement() + "-thread");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void setProperties(Properties properties) throws SQLException {
        this.properties = properties;
        connection = new MySQLDataConnection(properties);
        updateVocabularies();
    }


    private void updateVocabularies() {
        DBTask<List<Vocabulary>> task = new DBTask<List<Vocabulary>>() {
            @Override
            protected List<Vocabulary> call() throws Exception {
                return connection.getVocabularies();
            }
        };
        run(task, event -> {
            vocabularyList = task.getValue();
            vocabularyMenu = vocabularyList.stream().map(vocabulary -> {
                MenuItem item = new MenuItem(vocabulary.getName());
                item.setOnAction(e -> selectVocabulary(vocabulary));
                return item;
            }).collect(Collectors.toList());
            vocabularies.getItems().setAll(vocabularyMenu);
        });
    }

    private Optional<ButtonType> alert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Word");
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(new Image(getClass().getResource("/icon.png").toExternalForm()));
        alert.setHeaderText(message);
        alert.getButtonTypes().setAll(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        return alert.showAndWait();
    }

    private Optional<String> input(String header, String content, @Nullable String init) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(header);
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().addAll(new Image(getClass().getResource("/icon.png").toExternalForm()));
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        if (init != null) {
            dialog.getEditor().setText(init);
        }
        return dialog.showAndWait();
    }

    private Optional<Map<String, String>> customInput(String res, String title, @Nullable Map<String, String> init) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(res));
            Dialog<Map<String, String>> dialog = loader.<Dialog<Map<String, String>>>load();
            DialogController controller = loader.getController();
            ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().addAll(new Image(getClass().getResource("/icon.png").toExternalForm()));
            dialog.setTitle(title);
            if (init != null) {
                controller.init(init);
            }
            return dialog.showAndWait();
        } catch (IOException ignored) {
        }
        return Optional.empty();
    }

    @FXML
    private void addVocabulary() {
        customInput("/vocabulary.fxml", "Add Vocabulary", null).ifPresent(ans -> {
            DBTask<Boolean> task = new DBTask<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return connection.addVocabulary(ans.get("name"), Boolean.parseBoolean(ans.get("type"))) == 1;
                }
            };
            run(task, event -> {
                updateVocabularies();
                alert(Alert.AlertType.INFORMATION, task.getValue() ? "Added" : "Already exists");
            });
        });
    }

    @FXML
    private void addRecord() {
        if (selectedVocabulary != null) {
            customInput("/record.fxml", "Add Record", null).ifPresent(ans -> {
                DBTask<Boolean> task = new DBTask<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return connection.addRecord(selectedVocabulary.getId(),
                                selectedVocabulary.isExplanatory(), ans.get("name"), ans.get("def")) == 1;
                    }
                };
                run(task, event -> {
                    updateView();
                    alert(Alert.AlertType.INFORMATION, task.getValue() ? "Added" : "Already exists");
                });
            });
        } else alert(Alert.AlertType.WARNING, "Vocabulary not selected");
    }

    @FXML
    private void addWord() {
        input("Add word", "Word", null).ifPresent(word -> {
            DBTask<Boolean> task = new DBTask<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return connection.addWord(word) == 1;
                }
            };
            run(task, event -> {
                updateView();
                alert(Alert.AlertType.INFORMATION, task.getValue() ? "Added" : "Already exists");
            });
        });
    }

    @FXML
    private void editWord() {
        if (!view.getSelectionModel().getSelectedItems().isEmpty()) {
            Word selected = view.getSelectionModel().getSelectedItem().getWord();
            input("Edit word", "Word", selected.getWord()).ifPresent(word -> {
                DBTask<Boolean> task = new DBTask<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return connection.editWord(selected.getId(), word) == 1;
                    }
                };
                run(task, event -> {
                    updateView();
                    alert(Alert.AlertType.INFORMATION, task.getValue() ? "Edited" : "Not exists");
                });
            });
        } else alert(Alert.AlertType.WARNING, "Record not selected");
    }


    @FXML
    private void editRecord() {
        if (!view.getSelectionModel().getSelectedItems().isEmpty()) {
            Record selected = view.getSelectionModel().getSelectedItem();
            Map<String, String> init = new HashMap<>();
            init.put("name", selected.getWord().getWord());
            init.put("def", selected.getWord2().getWord());
            customInput("/record.fxml", "Add Record", init).ifPresent(ans -> {
                DBTask<Boolean> task = new DBTask<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return connection.editRecord(selected.getId(),
                                selected.isExplanatory(), ans.get("name"), ans.get("def")) == 1;
                    }
                };
                run(task, event -> {
                    updateView();
                    alert(Alert.AlertType.INFORMATION, task.getValue() ? "Edited" : "Not exists");
                });
            });
        } else alert(Alert.AlertType.WARNING, "Record not selected");
    }

    @FXML
    private void deleteVocabulary() {
        if (selectedVocabulary != null) {
            alert(Alert.AlertType.CONFIRMATION, "Delete vocabulary " + selectedVocabulary.getName() + "?").ifPresent(ans -> {
                DBTask<Boolean> task = new DBTask<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return connection.deleteVocabulary(selectedVocabulary.getId()) == 1;
                    }
                };
                run(task, event -> {
                    selectedVocabulary = null;
                    updateVocabularies();
                    updateView();
                    alert(Alert.AlertType.INFORMATION, task.getValue() ? "Deleted" : "Not exists");
                });
            });
        } else alert(Alert.AlertType.WARNING, "Vocabulary not selected");
    }

    @FXML
    private void editVocabulary() {
        if (selectedVocabulary != null) {
            Map<String, String> init = new HashMap<>();
            init.put("name", selectedVocabulary.getName());
            init.put("type", Boolean.toString(selectedVocabulary.isExplanatory()));
            customInput("/vocabulary.fxml", "Edit Vocabulary", init).ifPresent(ans -> {
                DBTask<Boolean> task = new DBTask<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return connection.updateVocabulary(selectedVocabulary.getId(),
                                ans.get("name"), Boolean.parseBoolean(ans.get("type"))) == 1;
                    }
                };
                run(task, event -> {
                    updateVocabularies();
                    alert(Alert.AlertType.INFORMATION, task.getValue() ? "Edited" : "Not exists");
                });
            });
        } else alert(Alert.AlertType.WARNING, "Vocabulary not selected");
    }

    @FXML
    private void deleteRecord() {
        if (!view.getSelectionModel().getSelectedItems().isEmpty()) {
            Record selected = view.getSelectionModel().getSelectedItem();
            alert(Alert.AlertType.CONFIRMATION, "Delete record " + selected + "?").ifPresent(ans -> {
                DBTask<Boolean> task = new DBTask<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return connection.deleteRecord(selected.getId(), selected.isExplanatory()) == 1;
                    }
                };
                run(task, event -> {
                    updateView();
                    alert(Alert.AlertType.INFORMATION, task.getValue() ? "Deleted" : "Not exists");
                });
            });
        } else alert(Alert.AlertType.WARNING, "Record not selected");
    }

    @FXML
    private void deleteWord() {
        if (!view.getSelectionModel().getSelectedItems().isEmpty()) {
            Record selected = view.getSelectionModel().getSelectedItem();
            alert(Alert.AlertType.CONFIRMATION, "Delete word " + selected.getWord() + "?").ifPresent(ans -> {
                DBTask<Boolean> task = new DBTask<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return connection.deleteWord(selected.getWord().getId()) == 1;
                    }
                };
                run(task, event -> {
                    updateView();
                    alert(Alert.AlertType.INFORMATION, task.getValue() ? "Deleted" : "Not exists");
                });
            });
        } else alert(Alert.AlertType.WARNING, "Record not selected");
    }

    @FXML
    private void findWord() {
        input("Find word", "Substring", null).ifPresent(this::selectWord);
    }

    private void selectVocabulary(Vocabulary vocabulary) {
        DBTask<List<Record>> task = new DBTask<List<Record>>() {
            @Override
            protected List<Record> call() throws Exception {
                return connection.selectVocabulary(vocabulary.getId(), vocabulary.isExplanatory());
            }
        };
        run(task, event -> {
            records.setAll(task.getValue());
            selectedVocabulary = vocabulary;
            searchWord = null;
            ((Label) view.getPlaceholder()).setText(vocabulary.getName());
        });
    }

    private void selectWord(String word) {
        DBTask<List<Record>> task = new DBTask<List<Record>>() {
            @Override
            protected List<Record> call() throws Exception {
                return connection.findAllLikeStr(word);
            }
        };
        run(task, event -> {
            records.setAll(task.getValue());
            selectedVocabulary = null;
            searchWord = word;
            ((Label) view.getPlaceholder()).setText(word);
        });
    }

    @FXML
    private void refresh() {
        updateVocabularies();
        updateView();
    }

    private void updateView() {
        if (selectedVocabulary != null) {
            selectVocabulary(selectedVocabulary);
        } else if (searchWord != null) {
            selectWord(searchWord);
        } else {
            view.getItems().clear();
            ((Label) view.getPlaceholder()).setText("Nothing Selected");
        }
    }

    private <T> void run(final DBTask<T> task, EventHandler<WorkerStateEvent> handler) {
        task.setOnSucceeded(handler);
        databaseExecutor.submit(task);
    }

    abstract class DBTask<T> extends Task<T> {
        DBTask() {
            setOnFailed(t -> alert(Alert.AlertType.ERROR, getException().getLocalizedMessage()));
            runningProperty().addListener((observable, oldValue, newValue) -> {
                menu.setDisable(newValue);
                indicator.setVisible(newValue);
            });
        }
    }
}
