package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connexion {
    private static final String URL="jdbc:mysql://localhost:3306/amira";
    private static final String USER="root";
    private static final String PASSWORD="";
    static Connection conn=null;
    public static Connection getConnexion() {
        try {
            conn= DriverManager.getConnection(URL,USER,PASSWORD);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return conn;
    }

}