package def_pkg;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;


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

    private static Bank_Account bankAccount;
    private static Client client;

    @BeforeEach
    public void setUp() {
        // Setup test data
        client = new Client("50", "John", "Doe", "Michael", "Sarah",
                "12345-6789012-3", "1990-01-01", "0123456789",
                "john@example.com", "123 Street");

        bankAccount = new Bank_Account("5", "9", "L001", "Saving",
                "1000", "Active", "2024-01-01");
    }
    @AfterAll
    public static void tearDown() {
        // Clean up references
        bankAccount = null;
        client = null;
    }

    @Test
    public void testAccountCreation() {
        // Sample test case
        assert bankAccount != null;
    }



    @DisplayName("loginid getter")
    @Test
    void getLoginId() {
        Login_Account account = new Login_Account("123", "medhat", "pass", "C");
        assertEquals("123", account.getLoginId());
    }

    @DisplayName("gettype getter")
    @Test
    void getType() {
        Login_Account client = new Login_Account("101", "client test", "pass", "C");
        Login_Account manager = new Login_Account("102", "manager test", "pass", "M");
        Login_Account unknown = new Login_Account("103", "unknown test", "pass", "X");

        assertEquals("Client", client.getType());
        assertEquals("Manager", manager.getType());
        assertEquals("Unknown", unknown.getType());
    }



    @DisplayName("signup with mismatched passwords")
    @Test
    void signUpFailPasswordMismatch() {
        try {
            String username = "medhat";
            String pass1 = "pass";
            String pass2 = "different_pass"; // passwords do not match
            String accNum = "5"; // existing bank_account

            int result = Login_Account.signUp(conn, username, pass1, pass2, accNum);

            // Assert that the result is -1 when passwords do not match
            assertEquals(-1, result, "Signup should fail when passwords do not match");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }



    @DisplayName("signup with valid client accNm")
    @Test
    void signUpSuccess() {
        try {

            String username = "medhat";
            String pass = "pass";
            String accNum = "5"; // existing bank_account

            int result = Login_Account.signUp(conn, username, pass, pass, accNum);

            // Assert success (0) for valid signup
            assertEquals(0, result, "Signup should succeed with a valid account number");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @DisplayName("signup with already linked client accNm")
    @Test
    void signUpAccountAlreadyLinked() {
        try {
            String username = "medhat";
            String pass = "pass";
            String accNum = "500000"; // existing bank_account already linked to a login

            int result = Login_Account.signUp(conn, username, pass, pass, accNum);

            // Assert that the result is -2 when the account is already linked
            assertEquals(-2, result, "Signup should fail if account is already linked to a login");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }


    @DisplayName("signin with non-existent account")
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

    @DisplayName("signin with wrong password")
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

    @DisplayName("signin with correct credentials")
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


    @DisplayName("verify AccountFailure wrong CNIC")
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

    @DisplayName("verify AccountFailure wrong accNum")
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
    @DisplayName("verify AccountFailure wrong CNIC and accNum")
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


    @DisplayName("verify Account Success")
    @Test
    void verifyAccount_success() {
        try {
            boolean result = Login_Account.verifyAccount(conn, "500000", "7853257");
            assertTrue(result); // assuming correct data
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @DisplayName("Get employee name Success")
    @Test
    void getEmployeeNameSuccess() {
        try {
            String loginId = "60000"; //  existing login_id from the employee table
            String name = Login_Account.getEmployeeName(conn, loginId);
            assertNotNull(name);
            assertFalse(name.isEmpty());
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @DisplayName("Get employee name failure invalid loginid")
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
