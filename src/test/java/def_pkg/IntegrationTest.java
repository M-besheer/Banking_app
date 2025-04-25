package def_pkg;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Login_Account manager_login_acc = Login_Account.signIn(conn, "Abdelrahman20", "Sallam").getKey();
        assertEquals("60000", manager_login_acc.getLoginId());
        assertEquals("Abdelrahman20", manager_login_acc.getUsername());
        assertEquals("Manager", manager_login_acc.getType());
        Manager manager = new Manager(Login_Account.getEmployeeName(conn, manager_login_acc.getLoginId()));

        Client client1 = new Client("Andrew", "Basilly", "Ramy",
                "Mom", "30501316106716", "30/06/2004", "01026065412",
                "andresramesbaseles@gmail.com", "Almaza");
        manager.createAccount(conn, client1, "C");
        Client fetchClient = Client.getByCNIC(conn, "30501316106716");
        assertEquals("Andrew",fetchClient.getFName());
        assertEquals("Almaza",fetchClient.getAddress());

        Bank_Account fetchBankAccount = Bank_Account.getByClientId(conn,fetchClient.getClientID());

        assertTrue(manager.verifyAccountOwnership(conn,Integer.parseInt(fetchBankAccount.getAccountNum()),"30501316106716"));

        Login_Account.signUp(conn, "besela", "dodo", "dodo", fetchBankAccount.getAccountNum());

        // scenariooo 2
        Login_Account Login = Login_Account.signIn(conn, "besela", "dodo").getKey();
        assertEquals("besela", Login.getUsername());
        client1.depositMoney(conn,fetchBankAccount.getAccountNum(),2000);
        client1.transferMoney(conn,"500000",500);


    }

}

