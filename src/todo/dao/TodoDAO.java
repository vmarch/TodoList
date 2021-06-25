package todo.dao;

import todo.model.Prio;
import todo.model.State;
import todo.model.Todo;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public interface TodoDAO {
    List<Todo> findAll();

    int save(Todo todo);
    boolean update(int id, Object newValue, String dbTableField);
    HashMap<Integer, Boolean> delete(List<Integer> ids);

    //TODO findBy/suche/filter
}
