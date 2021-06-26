package todo.dao;

import todo.db.DBConnect;
import todo.db.DBException;
import todo.model.Prio;
import todo.model.State;
import todo.model.Todo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static todo.tools.ConstantManager.*;

public class TodoMySQLDAO implements TodoDAO {

    private Connection con;

    public TodoMySQLDAO() throws DBException {
        con = DBConnect.getInstance().connection();
    }

    //region GET ALL FROM DB
    @Override
    public List<Todo> findAll() {
        List<Todo> todoList = new ArrayList<>();

        try {
            //  Statement always from java.sql Paket !!!!
            Statement selectStatement = con.createStatement();
            ResultSet rs = selectStatement.executeQuery("SELECT * FROM " + SQL_DB_NAME_TODOLISTE);

            while (rs.next()) {
                Todo todo = new Todo(
                        rs.getInt(TABLE_ID),
                        rs.getString(TABLE_TITLE),
                        rs.getString(TABLE_TASK),
                        convertDateToLocalDate(rs.getDate(TABLE_DEADLINE)),
                        Prio.intToPrio(rs.getInt(TABLE_PRIORITY)),
                        State.intToState(rs.getInt(TABLE_STATE))
                );

                todoList.add(todo);
                System.out.println(this.getClass().getSimpleName() + " > " + "findAll()> Todo: " + todo);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return todoList;
    }
    //endregion

    //region SAVE Item
    @Override
    public int save(Todo item) {

        int id = -1;
        try {
            //insert new Item and take the "id" in Callback
            PreparedStatement insertStatement = con.prepareStatement("INSERT INTO "
                    + SQL_DB_NAME_TODOLISTE
                    + "( "
                    + TABLE_TITLE + ","
                    + TABLE_TASK + ","
                    + TABLE_DEADLINE + ","
                    + TABLE_PRIORITY + ","
                    + TABLE_STATE
                    + ") VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

            insertStatement.setString(1, item.getTitle());
            insertStatement.setString(2, item.getTask());
            insertStatement.setDate(3, convertLocalDateToDate(item.getDeadline()));
            insertStatement.setInt(4, Prio.prioToInt(item.getPriority()));
            insertStatement.setInt(5, State.stateToInt(item.getState()));


            int affectedRows = insertStatement.executeUpdate();

            if (0 == affectedRows) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            // generatedKeys.next() -> generatedKeys.getInt(1) -> id
            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    //get "id" of new DBItem as result after complete added Item into DB.
                    id = generatedKeys.getInt(1);
                    System.out.println(this.getClass().getSimpleName() + " > " + "save(Todo item)> RESULT id: " + id);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    //endregion

    //region UPDATE Item
    @Override
    public boolean update(int id, Object newValue, String dbTableField) {
        try {
            PreparedStatement updateStatement = con.prepareStatement("UPDATE "
                    + SQL_DB_NAME_TODOLISTE
                    + " SET " + dbTableField + " = ? WHERE id = ?");

            switch (dbTableField) {
                case TABLE_TITLE, TABLE_TASK -> updateStatement.setString(1, (String) newValue);
                case TABLE_DEADLINE -> updateStatement.setDate(1, convertLocalDateToDate((LocalDate) newValue));
                case TABLE_PRIORITY -> updateStatement.setInt(1, Prio.prioToInt((Prio) newValue));
                case TABLE_STATE -> updateStatement.setInt(1, State.stateToInt((State) newValue));
            }

            updateStatement.setInt(2, id);

            int resultUpdate = updateStatement.executeUpdate();
            System.out.println(this.getClass().getSimpleName() + " > " + "update()> update result: " + updateStatement.getUpdateCount());

            return switch (resultUpdate) {
                case -1 -> false;
                case 0 -> false;
                case 1 -> true;
                default -> false;
            };


        } catch (SQLException e) {

            e.printStackTrace();
        }

        return false;
    }
    //endregion

    //region FIND ITEM

    @Override
    public List<Todo> findBy(String teilTaskName, String dbTableField) {
        return null;
    }
    //endregion

    //region DELETE one or more Items
    @Override
    public HashMap<Integer, Boolean> delete(List<Integer> ids) {
        HashMap<Integer, Boolean> hm = new HashMap<>();

        try {
            PreparedStatement deleteStatement = con.prepareStatement("DELETE FROM " + SQL_DB_NAME_TODOLISTE
                    + " WHERE id = ?", Statement.RETURN_GENERATED_KEYS);

            for (Integer id : ids) {
                System.out.println(this.getClass().getSimpleName() + " > " + "delete() -> for() -> id: " + id);
                deleteStatement.setInt(1, id);
                int affectedRows = deleteStatement.executeUpdate();

                if (affectedRows == 1) {
                    hm.put(id, true);
                } else if (affectedRows == 0) {
                    hm.put(id, false);
                    System.out.println(this.getClass().getSimpleName() + " > " + "delete() -> for() -> id: " + id + " = Error");
                    throw new SQLException("Creating user failed, no rows affected.");
                } else {
                    hm.put(id, false);
                    System.out.println(this.getClass().getSimpleName() + " > " + "delete() -> for() -> id: " + id + " = affectedRows ==" + affectedRows);
                }


//                ResultSet rs = deleteStatement.executeQuery();
//                while (rs.next())
//                    System.out.println(this.getClass().getSimpleName() + " > " + "delete() -> for() -> executeQuery() RESULT: "
//                            + rs.getString(1)
//                            + "\n"
//                            + rs.getString(2));
//
////                System.out.println("delete result: " + deleteStatement.getUpdateCount());
            }
            deleteStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hm;

//        for (Integer id : ids) {
//
//            //TODO change RESULT to ID
//            boolean result = todoList.removeIf(todo -> id == todo.getId());
//            hm.put(id, result);
//
//            System.out.println(this.getClass().getSimpleName() + " > " + "save()" + " > RESULT: " + result);
//        }
//
//        return hm;
//
//        try {
//
//            ///////////////////////DELETE///////////////////////
//            PreparedStatement deleteStatement = con.prepareStatement("DELETE FROM " + SQL_DB_NAME_TODOLISTE + " WHERE id = ?");
//            for (Integer id : ids) {
//                System.out.println("<delete>> id: " + id);
//                deleteStatement.setInt(1, id);
//                deleteStatement.executeUpdate();
//
////                System.out.println("delete result: " + deleteStatement.getUpdateCount());
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            Statement statement = con.createStatement();
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return null;
    }
    //endregion

    //region Common Methods

    //Converting LocalDate to Date
    private Date convertLocalDateToDate(LocalDate localDate) {
        if (null != localDate) {
            return Date.valueOf(localDate);
        } else {
            //TODO FIX ME
            return null;
        }
    }

    //Converting Date to LocalDate
    private LocalDate convertDateToLocalDate(Date sqlDate) {
        if (null != sqlDate) {
            LocalDate localDate = sqlDate
//                .toInstant()
//                .atZone(ZoneId.systemDefault()) // Specify the correct timezone
                    .toLocalDate();
            return localDate;
        } else {
            return null;
        }
    }
    //endregion
}
