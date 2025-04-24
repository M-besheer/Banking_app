package def_pkg;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTest {
    private Connection conn;

    @BeforeEach
    void setUp() {
        conn = establishConnection();


    }

    private Connection establishConnection() {
        String url = "jdbc:mysql://localhost:3306/bank_schema";
        String username = "root";
        String password = "";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established successfully!");
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to connect to the database. " + e.getMessage());
        }
    }
    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
    //scenario 1
    @Test
    @Order(1)
    void Test1() throws SQLException {
    Login_Account manager = Login_Account.signIn(conn,"Abdelrahman20","Sallam");
    assertEquals("60000",manager.getLoginId());
    assertEquals("Abdelrahman20",manager.getUsername());
    assertEquals("M",manager.getType());


    }

}
