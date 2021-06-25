package todo.dao;

import todo.model.Prio;
import todo.model.State;
import todo.model.Todo;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TodoDummyDAO implements TodoDAO {

    private List<Todo> todoList = new ArrayList<>();

    public TodoDummyDAO() {

        //model.Todo.Todo(int id, String title, String task, LocalDate deadline, Prio priority, State state)

        todoList.add(new Todo(
                0,
                "einkaufen",
                "Kartoffeln",
                LocalDate.of(2021, Month.JUNE, 18),
                Prio.HIGH,
                State.IN_PROGRESS));

        todoList.add(new Todo(
                1,
                "baden",
                "see",
                LocalDate.of(2021, Month.JULY, 3),
                Prio.LOW,
                State.WAIT));

        todoList.add(new Todo(
                2,
                "fliegen",
                "Ticket kaufen",
                LocalDate.of(2021, Month.AUGUST, 5),
                Prio.HIGH,
                State.IN_PROGRESS));

        todoList.add(new Todo(
                3,
                "baden",
                "see",
                LocalDate.of(2021, Month.JULY, 3),
                Prio.LOW,
                State.WAIT));

        todoList.add(new Todo(
                4,
                "fliegen",
                "Ticket kaufen",
                LocalDate.of(2021, Month.AUGUST, 5),
                Prio.HIGH,
                State.IN_PROGRESS));

    }

    //region GET ALL Items
    @Override
    public List<Todo> findAll() {

        System.out.println(this.getClass().getSimpleName() + " > " + "findAll()" + " > RESULT: " + todoList);
        return todoList;
    }
    //endregion

    //region SAVE Item as TodoObject
    @Override
    public int save(Todo todo) {

        //If id in DB starting from "1"
        if (todo.getId() <= 0) {
            System.out.println(this.getClass().getSimpleName() + " > " + "save()" + " id: " + todo.getId());
            todo.setId(getMaxItemID() + 1);
        }

        if (todoList.add(todo)) {

            System.out.println(this.getClass().getSimpleName() + " > " + "save()" + " > RESULT: " + true);
            return todo.getId();
        } else {
            System.out.println(this.getClass().getSimpleName() + " > " + "save()" + " > RESULT: " + false);
            return -1;
        }
    }
    //endregion

    //TODO später
    //region UPDATE Item
    @Override
    public boolean update(int id, Object newValue, String dbTableField) {

        return false;
    }
    //endregion

    //region DELETE one or many Items
    @Override
    public HashMap<Integer, Boolean> delete(List<Integer> ids) {
        HashMap<Integer, Boolean> hm = new HashMap<>();

        for (Integer id : ids) {
            //TODO change RESULT to ID
            boolean result = todoList.removeIf(todo -> id == todo.getId());
            hm.put(id, result);

            System.out.println(this.getClass().getSimpleName() + " > " + "save()" + " > RESULT: " + result);
        }

        return hm;
    }
    //endregion

    private int getMaxItemID() {
        int todoID = -1;
        if (todoList.size() > 0) {
            for (Todo todo : todoList) {
                if (todo.getId() > todoID) {
                    todoID = todo.getId();
                }
            }
        }
        return todoID;
    }

}
