package def_pkg;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class Bank_AccountTest {

    private Connection conn;
    private Manager TestManager;
    private Client TestClient;

    @BeforeEach
    public void create_Bank_Account() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/bank_schema";
        String username = "root";
        String password = "";
        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established successfully!");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to connect to the database. " + e.getMessage());
        }

        TestManager = new Manager();
        TestClient = new Client("Test_f_name", "Test_l_name", "Test_father_name",
                "Test_mother_name", "12345", "1,2,2000", "01000000000",
                "test@gmail.com", "testAddress");
        TestManager.createAccount(conn, TestClient, "Current");

    }

    @Test
    void getByClientId() throws SQLException {
//        String clientID= TestClient.getClientID();
//        System.out.println("Client ID: " + clientID);
        Bank_Account bank_accountTest=Bank_Account.getByClientId(conn, TestClient.getClientID());

        assertEquals(Bank_Account.getByClientId(conn, TestClient.getClientID()).getAccountNum(),
                bank_accountTest.getAccountNum());
        assertEquals(TestClient.getClientID(), bank_accountTest.getClientId());
        assertEquals(null, bank_accountTest.getLoginId());
        assertEquals("Current", bank_accountTest.getType());
        assertEquals("0", bank_accountTest.getBalance());
        assertEquals("1", bank_accountTest.getStatus());
//        assertEquals("2025-04-15", bank_accountTest.getOpeningDate());
    }


    @Test
    void getByAccountNumber() throws SQLException {
        Bank_Account bank_accountTest;
        bank_accountTest = Bank_Account.getByAccountNumber
                (conn, Bank_Account.getByClientId(conn, TestClient.getClientID()).getAccountNum());

        assertEquals(Bank_Account.getByClientId(conn, TestClient.getClientID()).getAccountNum(),
                bank_accountTest.getAccountNum());
        assertEquals(TestClient.getClientID(), bank_accountTest.getClientId());
        assertEquals(null, bank_accountTest.getLoginId());
        assertEquals("Current", bank_accountTest.getType());
        assertEquals("0", bank_accountTest.getBalance());
        assertEquals("1", bank_accountTest.getStatus());
//        assertEquals("2025-04-15", bank_accountTest.getOpeningDate());
    }


    @Test
    void updateBalance() {
    }

    @Test
    void testGetClientId() {
    }

    @Test
    void closeAccount() { //Useless ahh funcion, delete it..................
    }

    @Test
    void getCNIC() {
    }

    @Test
    void getByLoginId() {
    }

    @AfterEach
    void tearDown() throws SQLException {
        String sql = "DELETE FROM bank_account where client_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, TestClient.getClientID());
            pstmt.executeUpdate();
        }
        String sql2 = "DELETE FROM client where client_id=?";
        try (PreparedStatement pstmt2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
            pstmt2.setString(1, TestClient.getClientID());
            pstmt2.executeUpdate();
        }
    }

}