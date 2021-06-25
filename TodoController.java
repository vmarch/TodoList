package ui.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dao.TodoDAO;
import dao.TodoDummyDAO;
import dao.TodoMySQLDAO;
import fxutil.DatePickerTableCell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import model.Prio;
import model.State;
import model.Todo;

public class TodoController {

	private TodoDAO dao;

	@FXML
	private TableView<Todo> tableView;

	@FXML
	private TableColumn<Todo, String> titleCol;

	@FXML
	private TableColumn<Todo, String> taskCol;

	@FXML
	private TableColumn<Todo, LocalDate> deadlineCol;

	@FXML
	private TableColumn<Todo, Prio> prioCol;

	@FXML
	private TableColumn<Todo, State> stateCol;

	@FXML
	private ComboBox<Prio> prioBox;

	@FXML
	private ComboBox<State> stateBox;

	@FXML
	private TextField titleField;

	@FXML
	private TextField taskField;

	@FXML
	private DatePicker deadLinefield;

	@FXML
	void initialize() {
		System.out.println("initialize...");
		//dao = new TodoDummyDAO();
		dao = new TodoMySQLDAO();
		setupTable();

		setupContextMenu();

		setupComboBox();

	}

	private void setupComboBox() {
		// setup ComboBox

		prioBox.getItems().addAll(Prio.values());
		stateBox.getItems().addAll(State.values());
	}

	private void setupTable() {
		
		// Cell-Data
		titleCol.setCellValueFactory(new PropertyValueFactory<>("title")); // getFarbname()
		taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));// getHexwert
		deadlineCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));// getHexwert
		prioCol.setCellValueFactory(new PropertyValueFactory<>("priority"));// getHexwert
		stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));// getHexwert
		
		// cell -UI(Funktionalität der Zelle)
		titleCol.setCellFactory(TextFieldTableCell.forTableColumn());// von "normaler" Tabllenzelle zu TextField
		taskCol.setCellFactory(TextFieldTableCell.forTableColumn());// von "normaler" Tabllenzelle zu TextField
		
		deadlineCol.setCellFactory(DatePickerTableCell.forTableColumn()); 
		
		prioCol.setCellFactory(ComboBoxTableCell.forTableColumn(Prio.values()));  
		
		stateCol.setCellFactory(ComboBoxTableCell.forTableColumn( State.values() ));
		
		tableView.getItems().setAll(dao.findAll());
	}

	private void setupContextMenu() {
		// ContextMenu
		//////////////////////////////
		ContextMenu cm = new ContextMenu();
		MenuItem deleteItem = new MenuItem("Delete");
		cm.getItems().add(deleteItem);
		deleteItem.setOnAction(e -> {// Lambda

			System.out.println("delete...");
			
			Todo delTodo = tableView.getSelectionModel().getSelectedItem();
			
			boolean deleted = dao.delete(delTodo.getId());
			if(deleted) {
				tableView.getItems().setAll(dao.findAll());
			}
		});
		tableView.setContextMenu(cm);
	}

	@FXML
    void onSave(ActionEvent event) {
    	
 
    	//Todo(int id, String title, String task, LocalDate deadline, Prio priority, State state) {
    	Todo todo = new Todo( 
    						titleField.getText() ,
    						taskField.getText(), 
    						deadLinefield.getValue(), 
    						prioBox.getValue(),
    						stateBox.getValue());
    	boolean saved = dao.save(todo);
    	if(saved) {
    		tableView.getItems().setAll(dao.findAll());
    	}
    		
    	
    	
    }

	//<..., ?> ?-> beliebiger Datentyp, aber Typsicher 
	@FXML 
	public void onCommit(CellEditEvent<Todo, ?>     event   ) {
		
		System.out.println("Update");
		Todo todo =   event.getRowValue();
		String newValue =  String.valueOf(event.getNewValue());
		dao.update(todo.getId(), newValue, null);//update(int id, String newValuem, String dbTableField  )
		// 3. Parameter für nächste Version mit DB 
	}

}
