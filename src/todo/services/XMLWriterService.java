package todo.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import todo.model.Todo;
import todo.xml.XMLWriter;

import java.util.List;

public class XMLWriterService extends Service<Boolean> {

    private List<Todo> todoList;
    public void setTodoList(List<Todo> todoList) {
        this.todoList = todoList;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return XMLWriter.getInstance().writeFile(todoList);
            }
        };
    }
}
