package todo.tools;


/**
 * gefunden auf Github
 * <p>
 * -> es gibt für Datepicker in JavaFX keine Standard-TableCell
 */

import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;

import java.time.LocalDate;

public class DatePickerTableCell<S> extends TableCell<S, LocalDate> {
    private DatePicker datePicker;
    private StringConverter<LocalDate> converter = null;
    private boolean datePickerEditable = true;

    public DatePickerTableCell() {
        this.converter = new LocalDateStringConverter();
    }

    public DatePickerTableCell(boolean datePickerEditable) {
        this();
        this.datePickerEditable = datePickerEditable;
    }

    public DatePickerTableCell(StringConverter<LocalDate> converter) {
        this.converter = converter;
    }

    public DatePickerTableCell(StringConverter<LocalDate> converter, boolean datePickerEditable) {
        this.converter = converter;
        this.datePickerEditable = datePickerEditable;
    }

    @Override
    public void startEdit() {
        // Make sure the cell is editable
        if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
            return;
        }

        // Let the ancestor do the plumbing job
        super.startEdit();

        // Create a DatePicker, if needed, and set it as the graphic forthe cell
        if (null == datePicker) {
            this.createDatePicker();
        }

        this.setGraphic(datePicker);
    }

    @Override
    public void commitEdit(LocalDate newValue) {
        super.commitEdit(newValue);
    }

    @Override
//	@SuppressWarnings("unchecked")
    public void cancelEdit() {
        super.cancelEdit();
        this.setText(converter.toString(this.getItem()));
        this.setGraphic(null);
    }

    //	@SuppressWarnings("unchecked")
    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);

        // Take actions based on whether the cell is being edited or not
        if (empty) {
            this.setText(null);
            this.setGraphic(null);
        } else {
            if (this.isEditing()) {
                if (null != datePicker) {
                    datePicker.setValue((LocalDate) item);
                }
                this.setText(null);
                this.setGraphic(null);
            } else {
                this.setText(converter.toString(item));
                this.setGraphic(null);
            }
        }
    }

    private void createDatePicker() {
        datePicker = new DatePicker();
        datePicker.setConverter(converter);

        // Set the current value in the cell to the DatePicker
        datePicker.setValue(this.getItem());

        // Configure the DatePicker properties
        datePicker.setPrefWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        datePicker.setEditable(this.datePickerEditable);

        // Commit the new value when the user selects or neters a date
        datePicker.valueProperty().addListener((prop, oldVal, newVal) -> {
            if (this.isEditing()) {
                this.commitEdit(newVal);
            }
        });
    }

    public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn() {
        return forTableColumn(true);
    }

    public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn(
            boolean datePickerEditable) {
        return col -> new DatePickerTableCell<>(datePickerEditable);
    }

    public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn(
            StringConverter<LocalDate> converter) {
        return forTableColumn(converter, true);
    }

    public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn(
            StringConverter<LocalDate> converter, boolean datePickerEditable) {
        return col -> new DatePickerTableCell<>(converter, datePickerEditable);
    }

}