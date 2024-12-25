package Function;

import DB.DBconnect;

import java.sql.Connection;

public class DBconnectTest {
    public static void main(String[] args) {

        System.out.println("Testing database connection...");

        Connection connection = DBconnect.connect();

        // Kiểm tra kết nối
        if (connection != null) {
            System.out.println("Test passed: Successfully connected to the database!");
        } else {
            System.out.println("Test failed: Unable to connect to the database.");
        }
    }
}
