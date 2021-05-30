import com.jfoenix.controls.JFXTimePicker;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.util.converter.LocalTimeStringConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimePickerTableCell<S> extends TableCell<S, LocalTime> {

    // Static methods for creating TableColumn.cellFactory Callbacks

    private final ObjectProperty<DateTimeFormatter> formatter = new SimpleObjectProperty<>(this, "formatter");
    private JFXTimePicker timePicker;
    private boolean isAM;

    // Formatter property

    public TimePickerTableCell() {
        this(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public TimePickerTableCell(DateTimeFormatter formatter) {
        getStyleClass().add("time-picker-table-cell");
        setFormatter(formatter);
    }

    public static <S> Callback<TableColumn<S, LocalTime>, TableCell<S, LocalTime>> forTableColumn() {
        return v -> new TimePickerTableCell<>();
    }

    public static <S> Callback<TableColumn<S, LocalTime>, TableCell<S, LocalTime>> forTableColumn(DateTimeFormatter formatter) {
        return v -> new TimePickerTableCell<>(formatter);
    }

    // JFXTimePicker field

    public final DateTimeFormatter getFormatter() {
        return formatter.get();
    }

    // Constructors

    public final void setFormatter(DateTimeFormatter formatter) {
        this.formatter.set(formatter);
        isAM = formatter.format(LocalTime.MIN).toLowerCase().contains("a");
    }

    public final ObjectProperty<DateTimeFormatter> formatterProperty() {
        return formatter;
    }

    // Display logic

    @Override
    protected void updateItem(LocalTime item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);

        if (empty || item == null) {
            setText(null);
        } else {
            setText(formatItem(item));
        }
    }

    private String formatItem(LocalTime item) {
        if (item == null) {
            return null;
        }
        return getFormatter() == null ? item.toString() : getFormatter().format(item);
    }

    // Edit logic

    @Override
    public void startEdit() {
        if (!isEditable() ||
                !getTableColumn().isEditable() ||
                !getTableView().isEditable()) {
            return;
        }
        super.startEdit();
        if (isEditing()) {
            if (timePicker == null) {
                createTimePicker();
            }
            timePicker.setValue(getItem());
            setText(null);
            setGraphic(timePicker);

            // Wrapped this in a Platform#runLater call because otherwise
            // I couldn't get this to work properly. Despite this, there are
            // times where this still seems buggy.
            Platform.runLater(() -> {
                timePicker.requestFocus();
                timePicker.getEditor().selectAll();
                timePicker.show();
            });
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(formatItem(getItem()));
        setGraphic(null);
    }

    private void createTimePicker() {
        timePicker = new JFXTimePicker();
        timePicker.setConverter(new LocalTimeStringConverter(getFormatter(), null));
        formatter.addListener((observable, oldValue, newValue) ->
                timePicker.setConverter(new LocalTimeStringConverter(newValue, null)));
        timePicker.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    commitEdit(timePicker.getValue());
                    event.consume();
                } catch (NullPointerException ignored) {
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                try {
                    cancelEdit();
                    event.consume();
                } catch (NullPointerException ignored) {
                }
            }
        });
        timePicker.getEditor().setDisable(true);

        timePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                cancelEdit();
            }
        });
        timePicker.set24HourView(!isAM);
    }
}
