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
import todo.tools.DatePickerTableCell;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static todo.tools.ConstantManager.*;

public class TodoController {

    private TodoDAO dao;  // Interface
    private List<Todo> todoList;
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
    private ComboBox<Prio> comboBoxPrio;

    @FXML
    private ComboBox<State> comboBoxState;

    @FXML
    private Button onSaveButton;

    @FXML
    private TextField textFieldSearch;

    @FXML
    private Button btnSearch;

    @FXML
    public Label infoField;
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

        setupContextMenu();

        setupTable();

        setupComboBox();
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


    //SEARCH by part of word
    @FXML
    void onSearch(ActionEvent event) {

        String searchText = textFieldSearch.getText();
        List<Todo> filteredList = new ArrayList<>();

        filteredList = todoList.stream().filter(todo -> todo.getTask().matches("(?i).*" + searchText + ".*"))
                .collect(Collectors.toList());

        tableView.getItems().setAll(filteredList);
    }

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

    //endregion
}
