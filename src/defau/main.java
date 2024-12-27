package defau;

import Controller.MainController;
import java.sql.Connection;
import java.sql.DriverManager;

public class main {

    public static void main(String[] args) {
        try {
            // Establish a connection to the database
            String url = "jdbc:mysql://localhost:3306/amira";
            String username = "root";
            String password = "";
            Connection connection = DriverManager.getConnection(url, username, password);
            MainController mainController = new MainController(connection);
            mainController.startApplication();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database.");
        }
    }
}
