package todo.ui.controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import todo.dao.TodoDAO;
import todo.dao.TodoMySQLDAO;
import todo.db.DBException;
import todo.model.Prio;
import todo.model.State;
import todo.model.Todo;
import todo.services.XMLWriterService;
import todo.tools.DatePickerTableCell;
import todo.tools.Filter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static todo.model.Prio.*;
import static todo.model.State.*;
import static todo.tools.ConstantManager.*;

public class TodoController {

    private TodoDAO dao;  // Interface
    private List<Todo> todoList;
    Filter filter = new Filter();

    private XMLWriterService xmlWriterService = new XMLWriterService();
    //region FXML fields
    @FXML
    private TableView<Todo> tableView;

    @FXML
    private TableColumn<Todo, String> columnTitle;

    @FXML
    private TableColumn<Todo, String> columnTask;

    @FXML
    private TableColumn<Todo, LocalDate> columnDeadline;

    @FXML
    private TableColumn<Todo, Prio> columnPrio;

    @FXML
    private TableColumn<Todo, State> columnState;

    @FXML
    private TextField textFieldTitle;

    @FXML
    private TextField textFieldTask;

    @FXML
    private DatePicker datePickerDeadline;

    @FXML
    private Button onSaveButton;

    @FXML
    private ComboBox<Prio> comboBoxPrio;

    @FXML
    private ComboBox<State> comboBoxState;

    @FXML
    private TextField textFieldSearch;

    @FXML
    private Button btnSearch;

    @FXML
    public Label infoField;

    @FXML
    private CheckBox checkBoxLowPrio;

    @FXML
    private CheckBox checkBoxMediumPrio;

    @FXML
    private CheckBox checkBoxHighPrio;

    @FXML
    private CheckBox checkBoxInProgress;

    @FXML
    private CheckBox checkBoxWait;

    @FXML
    private CheckBox checkBoxDone;

    //endregion

    //region INITIALIZE
    @FXML
    void initialize() {
//        dao = new TodoDummyDAO();
        try {
            dao = new TodoMySQLDAO();
        } catch (DBException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText((e.getMessage()));
            alert.setContentText("Bitte DB-Verbindung herstellen und neu startenQ");
            alert.showAndWait();
            Platform.exit();
        }

        setupCheckBoxFilter();

        setupContextMenu();

        setupTable();

        setupComboBox();

        catchCallBackOfXMLWriteService();
    }

    private void setupCheckBoxFilter() {

        checkBoxLowPrio.selectedProperty().addListener((o, oldValue, newValue) -> {
            System.out.println(this.getClass().getSimpleName() + " > " + "checkBoxLowPrio > Value: " + newValue);

//            if (newValue) {
//                FilteredList<Todo> filteredListObs = new FilteredList<>(tableView.getItems(), (todo -> todo.getPriority() == LOW));
//                tableView.getItems().setAll(filteredListObs.stream().toList());
//            } else {
//                tableView.getItems().setAll(todoList);
//            }
//            //------------------------------ODER----------------------
//            if (newValue) {
//                List<Todo> filteredListStream = todoList.stream().filter(todo -> todo.getPriority() == LOW).collect(Collectors.toList());
//                tableView.getItems().setAll(filteredListStream);
//            } else {
//                tableView.getItems().setAll(dao.findAll());
//            }
            filter.setPrioLow(newValue);
            updateFilter();
        });

        checkBoxMediumPrio.selectedProperty().addListener((o, oldValue, newValue) -> {
            System.out.println(this.getClass().getSimpleName() + " > " + "checkBox.... > Value: " + newValue);
            filter.setPrioMedium(newValue);
            updateFilter();
        });
        checkBoxHighPrio.selectedProperty().addListener((o, oldValue, newValue) -> {
            System.out.println(this.getClass().getSimpleName() + " > " + "checkBox.... > Value: " + newValue);
            filter.setPrioHigh(newValue);
            updateFilter();
        });

        checkBoxInProgress.selectedProperty().addListener((o, oldValue, newValue) -> {
            System.out.println(this.getClass().getSimpleName() + " > " + "checkBox.... > Value: " + newValue);
            filter.setStateInProgress(newValue);
            updateFilter();
        });

        checkBoxWait.selectedProperty().addListener((o, oldValue, newValue) -> {
            System.out.println(this.getClass().getSimpleName() + " > " + "checkBox.... > Value: " + newValue);

            filter.setStateWait(newValue);
            updateFilter();
        });

        checkBoxDone.selectedProperty().addListener((o, oldValue, newValue) -> {
            System.out.println(this.getClass().getSimpleName() + " > " + "checkBox.... > Value: " + newValue);
            filter.setStateDone(newValue);
            updateFilter();
        });
    }

    private void setupContextMenu() {
        //Setup ContextMenu
        ContextMenu cm = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        cm.getItems().addAll(deleteItem);

        deleteItem.setOnAction(e -> {
            onDeleteItems(tableView.getSelectionModel().getSelectedItems());

        });

        //TODO change DELETE
        tableView.setContextMenu(cm);
    }

    private void setupTable() {
        columnTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnTask.setCellValueFactory(new PropertyValueFactory<>("task"));
        columnDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        columnPrio.setCellValueFactory(new PropertyValueFactory<>("priority"));
        columnState.setCellValueFactory(new PropertyValueFactory<>("state"));

        //Cell-Data
        columnTitle.setCellFactory(TextFieldTableCell.forTableColumn()); // von "normalen" TableZellen zu "TextField"
        columnTask.setCellFactory(TextFieldTableCell.forTableColumn()); // von "normalen" TableZellen zu "TextField"
        columnDeadline.setCellFactory(DatePickerTableCell.forTableColumn()); // von "normalen" TableZellen zu "DatePicker"
        columnPrio.setCellFactory(ComboBoxTableCell.forTableColumn(Prio.values())); // von "normalen" TableZellen zu "ComboBox"
        columnState.setCellFactory(ComboBoxTableCell.forTableColumn(State.values())); // von "normalen" TableZellen zu "ComboBox"

        todoList = dao.findAll();
        tableView.getItems().setAll(todoList);
        if (todoList.size() == 0) {
            setInfoMessage("Write yours first Task!");
        }

        // Voraussetzung mehrere Datensätze zu markieren (zum Löschen)
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void setupComboBox() {
        //Setup ComboBox Menu
        comboBoxPrio.getItems().addAll(Prio.values());
        comboBoxState.getItems().addAll(State.values());
    }
    //endregion

    //region SAVE Item
    @FXML
    public void onSaveNewItem(ActionEvent actionEvent) {
        if (!textFieldTitle.getText().isEmpty() && !textFieldTask.getText().isEmpty()) {

            Todo todo = new Todo(
                    textFieldTitle.getText(),
                    textFieldTask.getText(),
                    getLocalDateIfNullOrNot(),
                    comboBoxPrio.getValue(),
                    comboBoxState.getValue());

            int result = dao.save(todo);
            System.out.println(this.getClass().getSimpleName() + " > " + "onSaveNewItem Todo > RESULT ID: " + result);

            if (result > 0) {
                clearTextFields();

                todo.setId(result);
                tableView.getItems().add(todo);

                setInfoMessage("Saved!");
            }

        } else if (textFieldTitle.getText().isEmpty() && textFieldTask.getText().isEmpty()) {
            setErrorMessage("Please write at little Title and Task, to create new Item.");
        } else if (textFieldTitle.getText().isEmpty()) {
            setErrorMessage("Title can not be empty!");
        } else if (textFieldTask.getText().isEmpty()) {
            setErrorMessage("Task can not be empty!");
        }
    }
    //endregion

    //region SEARCH by part of word
    @FXML
    void onSearch(ActionEvent event) {

        String searchText = textFieldSearch.getText();
        List<Todo> filteredList = new ArrayList<>();

        filteredList = todoList.stream().filter(todo -> todo.getTask().matches("(?i).*" + searchText + ".*"))
                .collect(Collectors.toList());

        tableView.getItems().setAll(filteredList);
    }


    public void clearSearchAction(ActionEvent actionEvent) {
    }
    //endregion

    //region CHANGE Item
    public void onChangeItem(int id, Object newValue, String tableColumn) throws SQLException {
        tableView.setEditable(true);

        if (dao.update(id, newValue, switch (tableColumn) {
            case "columnTitle" -> TABLE_TITLE;
            case "columnTask" -> TABLE_TASK;
            case "columnDeadline" -> TABLE_DEADLINE;
            case "columnPrio" -> TABLE_PRIORITY;
            case "columnState" -> TABLE_STATE;

            default -> throw new IllegalStateException("Unexpected value: " + tableColumn);
        })) {

            //update base TodoList
            Todo currentTodo = todoList.stream().filter(todo -> todo.getId() == id).collect(Collectors.toList()).get(0);
            switch (tableColumn) {
                case "columnTitle" -> currentTodo.setTitle((String) newValue);
                case "columnTask" -> currentTodo.setTask((String) newValue);
                case "columnDeadline" -> currentTodo.setDeadline((LocalDate) newValue);
                case "columnPrio" -> currentTodo.setPriority((Prio) newValue);
                case "columnState" -> currentTodo.setState((State) newValue);
            }
            setInfoMessage("Item is changed.");
        } else
            setErrorMessage("Item is not changed!");
    }
    //endregion

    //region DELETE Items
    private void onDeleteItems(List<Todo> todos) {
        List<Integer> listIDToDelete = new ArrayList<>();
        for (Todo item : todos) {
            listIDToDelete.add(item.getId());
        }

        HashMap<Integer, Boolean> hashMapIDAfterDeletionInDB = dao.delete(listIDToDelete);

        List<Todo> deletedItemsInDB = new ArrayList<>();
        List<Todo> notDeletedItemsInDB = new ArrayList<>();


        for (Todo todoToDelete : todos) {

            if (hashMapIDAfterDeletionInDB.get(todoToDelete.getId())) {
                deletedItemsInDB.add(todoToDelete);
            } else {
                notDeletedItemsInDB.add(todoToDelete);
            }
        }

        if (notDeletedItemsInDB.size() > 0) {
            //TODO say User that some Items was not deleted in DB.
            System.out.println(this.getClass().getSimpleName() + " > " + "onDeleteItems() " + "Some Items war not deleted.");
            setErrorMessage(" Items war not deleted: " + notDeletedItemsInDB.size());
        } else if (deletedItemsInDB.size() == todos.size()) {
            setInfoMessage("All was successfully deleted.");
            System.out.println(this.getClass().getSimpleName() + " > " + "onDeleteItems() " + "All was successfully deleted.");
        } else {
            System.out.println(this.getClass().getSimpleName() + " > " + "onDeleteItems() " + "Error during deleting.");
        }
        todoList.removeAll(deletedItemsInDB);              // actualize base TodoList.
        tableView.getItems().removeAll(deletedItemsInDB);  // actualize current TodoList
        tableView.getSelectionModel().clearSelection();

    }
    //endregion

    //region Common methods

    private void updateFilter() {

        if (filter.isSomeOneFiltersActive()) {
            List<Todo> filteredListStream = todoList.stream().filter(todo -> {


                if (filter.getPrioLow() && todo.getPriority() == LOW) {
                    return true;
                } else if (filter.getPrioMedium() && todo.getPriority() == MEDIUM) {
                    return true;
                } else if (filter.getPrioHigh() && todo.getPriority() == HIGH) {
                    return true;
                } else if (filter.getStateInProgress() && todo.getState() == IN_PROGRESS) {
                    return true;
                } else if (filter.getStateWait() && todo.getState() == WAIT) {
                    return true;
                } else if (filter.getStateDone() && todo.getState() == DONE) {
                    return true;
                } else {
                    return false;
                }

            }).collect(Collectors.toList());

            tableView.getItems().setAll(filteredListStream);

        } else {
            tableView.getItems().setAll(todoList);
        }
    }

    private LocalDate getLocalDateIfNullOrNot() {
        if (null != datePickerDeadline.getValue()) {
            return datePickerDeadline.getValue();
        } else {
            return null;
        }
    }

    //Cleaning possible data from fields: "Title", "Task", "Deadline"
    private void clearTextFields() {
        textFieldTitle.clear();
        textFieldTask.clear();
        datePickerDeadline.setValue(null);

        //TODO FIX ME: set Text "Prio" after clearing
        comboBoxPrio.setValue(null);
        //TODO FIX ME: set Text "State" after clearing
        comboBoxState.setValue(null);

    }

    // Show info-message
    private void setInfoMessage(String msg) {

        infoField.setTextFill(Color.BLACK);
        infoField.setOpacity(1);             // 100% initial
        infoField.setText(msg);
        FadeTransition fade = new FadeTransition(Duration.seconds(2), infoField);
        fade.setDelay(Duration.seconds(0)); // start Animation nach 0 Sekunden.
        fade.setFromValue(1);               // 100%
        fade.setToValue(0);
        fade.play();
    }

    // Show error-message
    private void setErrorMessage(String msg) {
        infoField.setTextFill(Color.RED);
        infoField.setOpacity(1);
        infoField.setText(msg);
        FadeTransition fade = new FadeTransition(Duration.seconds(3), infoField);
        fade.setDelay(Duration.seconds(0));
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.play();
    }

    //<..., ?>  ?-> belibiger Datentyp, aber Typsicher
    @FXML
    public void onCommit(TableColumn.CellEditEvent<Todo, ?> event) throws SQLException {
//        String columnName = event.getTableColumn().getText();
//        List<?> list = List.of("a", "s");

        if (null != event.getNewValue()) {
            if (event.getNewValue() != event.getOldValue()) {
                onChangeItem(event.getTableView().getSelectionModel().getSelectedItem().getId(),
                        event.getNewValue(),
                        event.getTablePosition().getTableColumn().getId());
            }
        } else if (null != event.getOldValue()) {
            onChangeItem(event.getTableView().getSelectionModel().getSelectedItem().getId(),
                    event.getNewValue(),
                    event.getTablePosition().getTableColumn().getId());
        }

//        System.out.println("res click2: " + event.getOldValue());
//        System.out.println("res click3: " + event.getNewValue());
//        System.out.println("res click4: " + event.getTableColumn().getText()); //Name of Column
//        System.out.println("res click5: " + event.getTableView().getSelectionModel().getSelectedItem().getId());// Todo ID
//        System.out.println("res click8: " + event.getTablePosition().getTableColumn().getId());//Column ID (fx:id...)

    }

    //region XMLExportCallBack

    @FXML
    public void onActionSaveAsXML() {
        List<Todo> list = dao.findAll();
        if (null != list) {
            xmlWriterService.setTodoList(list);
            xmlWriterService.restart();
        }
    }

    private void catchCallBackOfXMLWriteService() {

        //if running
        xmlWriterService.setOnRunning(running -> {
            System.out.println(this.getClass().getSimpleName() + " -> urlService Running");
        });

        // if succeeded
        xmlWriterService.setOnSucceeded(s -> {
            System.out.println(this.getClass().getSimpleName() + " -> xmlWriterService Succeeded");
            boolean result = xmlWriterService.getValue();
            if (result) {
                setInfoMessage("Your DB is saved as XML");
            }else{
                setErrorMessage("Your DB is NOT saved as XML");
            }


        });

        // if failed
        xmlWriterService.setOnFailed(fail -> {
            fail.getSource().getException().printStackTrace();
            System.out.println(this.getClass().getSimpleName() + " -> urlService Failed");

        });

        //if cancelled
        xmlWriterService.setOnCancelled(cancelled -> {
            System.out.println(this.getClass().getSimpleName() + " -> urlService Cancelled");
        });
    }
    //endregion
}
