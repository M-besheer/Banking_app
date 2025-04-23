package def_pkg;


import def_pkg.Login_Account;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class Login_AccountTest {

    static Connection conn;

    @BeforeAll
    static void connectToDB() {
        try {
            // Replace with your actual DB credentials
            String url = "jdbc:mysql://localhost:3306/bank_schema";
            String user = "root";
            String password = "";
            conn = DriverManager.getConnection(url, user, password);
            assertNotNull(conn);
        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

    @AfterAll
    static void closeDB() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
    //getters
    @Test
    void getLoginId() {
        Login_Account account = new Login_Account("123", "medhat", "pass", "C");
        assertEquals("123", account.getLoginId());
    }

    @Test
    void getType() {
        Login_Account client = new Login_Account("101", "client test", "pass", "C");
        Login_Account manager = new Login_Account("102", "manager test", "pass", "M");
        Login_Account unknown = new Login_Account("103", "unknown test", "pass", "X");

        assertEquals("Client", client.getType());
        assertEquals("Manager", manager.getType());
        assertEquals("Unknown", unknown.getType());
    }

    //signup
    @Test
    void signUp() {
        try {
            String username = "medhat";
            String pass = "pass";
            String accNum = "500000"; //existing

            int result = Login_Account.signUp(conn, username, pass, pass, accNum);
            assertEquals(0, result);
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }


    //signin
    @Test
    void signInFailure_no_account() {
        try {
            // Attempt to sign in with incorrect credentials
            Login_Account account = Login_Account.signIn(conn, "invalidUser", "wrongPass");

            // Expecting null since the credentials are invalid
            assertNull(account, "Expected null for invalid credentials but got an account");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }
    @Test
    void signInFailure_wrong_password() {
        try {
            // Attempt to sign in with incorrect credentials
            Login_Account account = Login_Account.signIn(conn, "medhat", "wrongPass");

            // Expecting null since the credentials are invalid
            assertNull(account, "Expected null for invalid credentials but got an account");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }


    @Test
    void signInsuccess() {
        try {

            Login_Account account = Login_Account.signIn(conn, "medhat", "pass");
            assertNotNull(account);
            assertEquals("medhat", account.getUsername());  // Ensure ID or Username
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }
    //verify
    @Test
    void verifyAccountFailure_wrongCNIC() {
        try {
            // Attempt to verify with invalid account number and/or CNIC
            boolean result = Login_Account.verifyAccount(conn, "500000", "00000-0000000-0");

            // Expecting false since the details are incorrect
            assertFalse(result, "Expected verification to fail with invalid credentials");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void verifyAccountFailure_wrongACCnUM() {
        try {
            // Attempt to verify with invalid account number and/or CNIC
            boolean result = Login_Account.verifyAccount(conn, "700000", "67153-7853257-8");

            // Expecting false since the details are incorrect
            assertFalse(result, "Expected verification to fail with invalid credentials");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    void verifyAccountFailure_both() {
        try {
            // Attempt to verify with invalid account number and/or CNIC
            boolean result = Login_Account.verifyAccount(conn, "700000", "67153");

            // Expecting false since the details are incorrect
            assertFalse(result, "Expected verification to fail with invalid credentials");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }


    @Test
    void verifyAccount_success() {
        try {
            boolean result = Login_Account.verifyAccount(conn, "500000", "67153-7853257-8");
            assertTrue(result); // assuming correct data
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    //get employee
    @Test
    void getEmployeeNameSuccess() {
        try {
            String loginId = "60000"; // Provide an existing login_id from the employee table
            String name = Login_Account.getEmployeeName(conn, loginId);
            assertNotNull(name);
            assertFalse(name.isEmpty());
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }
    @Test
    void getEmployeeNameFailure() {
        try {
            String invalidLoginId = "99999"; // Use a login_id that doesn't exist
            String name = Login_Account.getEmployeeName(conn, invalidLoginId);

            // Expecting an empty string if the login ID doesn't exist
            assertTrue(name == null || name.isEmpty(), "Expected null or empty for non-existent login ID");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }


}
