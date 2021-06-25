package todo.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private static DBConnect instance = null;
    private Connection con ;

    private DBConnect() throws DBException {// Kein new DBConnect
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost/java2", "root", "");
        } catch (SQLException e) {
           throw new DBException("Kein new DBConnect");
        }
    }

    public static DBConnect getInstance() throws DBException { //DBConnect.getInstance()

        if(null == instance) {
            instance = new DBConnect();
        }
        return instance;
    }

    public Connection connection() {
        return con;
    }
}
