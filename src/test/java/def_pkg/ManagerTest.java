package def_pkg;

import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManagerTest {
    private Connection conn;
    private Manager manager;
    private Client testClient;

    @BeforeAll
    void setupConnection() {
        manager = new Manager("Abdelrahman");
        testClient = new Client("Abdelrahman", "Sallam", "Mostafa", "mama", "30410180105717", "18/10/2004", "01129908336", "sallam@gmail.com", "cairo");
        String url = "jdbc:mysql://localhost:3306/bank_schema";
        String username = "root";
        String password = "";
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            fail("Unable to connect to the database. " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    void getName() {
        assertEquals("Abdelrahman", manager.getName());
    }

    @Nested
    @Order(2)
    @DisplayName("Create Account Test")
    class create_Account{
        @Test
        @Order(1)
        @DisplayName("Create New Bank Account For New Client")
        void createBankAccount () throws SQLException {
            int result = manager.createAccount(conn, testClient, "Saving");
            assertEquals(0, result);

            Client createdClient = Client.getByCNIC(conn,testClient.getCNIC());
            assertNotNull(createdClient);
            assertEquals("Abdelrahman",createdClient.getFName());
            assertEquals("Sallam",createdClient.getLName());
            assertEquals("Mostafa",createdClient.getFatherName());
            assertEquals("mama",createdClient.getMotherName());
            assertEquals("01129908336",createdClient.getPhone());
            assertEquals("sallam@gmail.com",createdClient.getEmail());



            Bank_Account acc = Bank_Account.getByClientId(conn, testClient.getClientID());
            assertNotNull(acc);

            assertEquals("Saving", acc.getType());
            assertEquals("0", acc.getBalance());
            assertEquals("1",acc.getStatus());

        }

        @Test
        @Order(2)
        @DisplayName("Create Bank Account For Existing Client")
        void createBankAccountForSameClient() throws SQLException {
            int result1 = manager.createAccount(conn, testClient, "Saving");
            assertEquals(0, result1);
            int result2 = manager.createAccount(conn, testClient, "Saving");
            assertEquals(1, result2);
        }
    }


    @Test
    @Order(3)
    void verifyAccountOwnership() throws SQLException {
        manager.createAccount(conn, testClient, "Saving");
        Bank_Account acc = Bank_Account.getByClientId(conn, testClient.getClientID());
        assertNotNull(acc);
        boolean bool = manager.verifyAccountOwnership(conn, Integer.parseInt(acc.getAccountNum()),testClient.getCNIC());
        assertTrue(bool);
        bool = manager.verifyAccountOwnership(conn, Integer.parseInt(acc.getAccountNum()),testClient.getClientID());
        assertFalse(bool);
        bool = manager.verifyAccountOwnership(conn, Integer.parseInt(acc.getStatus()),testClient.getCNIC());
        assertFalse(bool);
    }



    @Nested
    @Order(4)
    @DisplayName("block and unblock account test")
    class Block_Unblock_Account {
        @Test
        @Order(1)
        @DisplayName("Block an existing account")
        void blockAccount() throws SQLException {
            manager.createAccount(conn, testClient, "Saving");
            Bank_Account acc = Bank_Account.getByClientId(conn, testClient.getClientID());

            assert acc != null;
            int blockResult = manager.blockAccount(conn, acc, testClient.getCNIC());
            assertEquals(0, blockResult);

            assertEquals("2", acc.getStatus());
        }

        @Test
        @Order(2)
        @DisplayName("Block then unblock")
        void UnblockAccount() throws SQLException {
            manager.createAccount(conn, testClient, "Saving");
            Bank_Account acc = Bank_Account.getByClientId(conn, testClient.getClientID());
            assert acc != null;
            int blockResult = manager.blockAccount(conn, acc, testClient.getCNIC());
            assertEquals(0, blockResult);
            assertEquals("2", acc.getStatus());

            int unblockResult = manager.unblockAccount(conn, acc, testClient.getCNIC());
            assertEquals(0, unblockResult);
            assertEquals("1", acc.getStatus());
        }

        @Test
        @Order(3)
        @DisplayName("Block with miss matching cnic")
        void blockAccountWithWrongCNIC() throws SQLException {
            manager.createAccount(conn, testClient, "Saving");
            Bank_Account acc = Bank_Account.getByClientId(conn, testClient.getClientID());
            assert acc != null;
            int blockResult = manager.blockAccount(conn, acc, testClient.getClientID());
            assertEquals(-1, blockResult);
            assertEquals("1", acc.getStatus());
        }

        @Test
        @Order(4)
        @DisplayName("unblock with miss matching cnic")
        void unblockAccountWithWrongCNIC() throws SQLException {
            manager.createAccount(conn, testClient, "Saving");
            Bank_Account acc = Bank_Account.getByClientId(conn, testClient.getClientID());
            assert acc != null;
            int blockResult = manager.blockAccount(conn, acc, testClient.getCNIC());
            assertEquals(0, blockResult);
            assertEquals("2", acc.getStatus());

            int unblockResult = manager.unblockAccount(conn, acc, testClient.getClientID());
            assertEquals(-1, unblockResult);
            assertEquals("2", acc.getStatus());

        }

    }


    @Nested
    @Order(5)
    @DisplayName("Get Client Info Test")
    class Get_Client_Info {
        @Test
        @Order(1)
        @DisplayName("Get test client info")
        void getClientInfo() throws SQLException {
            manager.createAccount(conn, testClient, "Saving");
            Bank_Account acc = Bank_Account.getByClientId(conn, testClient.getClientID());
            assertNotNull(acc);

            Client result = manager.getClientInfo(conn, acc.getAccountNum());
            assertNotNull(result);
            assertEquals(testClient.getCNIC(), result.getCNIC());
        }

        @Test
        @Order(2)
        @DisplayName("send a non exiting account number")
        void WrongAccountNumber() throws SQLException {
            Client client = manager.getClientInfo(conn, "5563672");
            assertNull(client);
        }
    }

    @Test
    @Order(6)
    @DisplayName("Update Client Info")
    void updateClientInfo() throws SQLException {
        manager.createAccount(conn, testClient, "Saving");
        Bank_Account acc = Bank_Account.getByClientId(conn, testClient.getClientID());

        assert acc != null;
        Client client = manager.getClientInfo(conn, acc.getAccountNum());

        manager.updateClientInfo(conn, client.getClientID(), "01000000000", "updated@email.com", "new address");

        Client updated = manager.getClientInfo(conn, acc.getAccountNum());
        assertEquals("01000000000", updated.getPhone());
        assertEquals("updated@email.com", updated.getEmail());
        assertEquals("new address", updated.getAddress());
    }

    @Test
    @Order(7)
    void getTotalAccounts() throws SQLException {
        int count = manager.getTotalAccounts(conn,manager);
        manager.createAccount(conn, testClient, "Saving");
        assertEquals(count+1, manager.getTotalAccounts(conn,manager));
    }

    @Test
    @Order(8)
    void getTotalEmployees() throws SQLException {
        assertEquals(1, manager.getTotalEmployees(conn,manager));
    }

//    @Nested
//    @Order(9)
//    @DisplayName("White box testing")
//    class White_Box {
//        @Test
//        @Order(1)
//        @DisplayName("database connection cut in get total employees")
//        void closeConnection() throws SQLException {
//            assertThrows(SQLException.class, () -> {
//                conn.close();
//                manager.createAccount(conn, testClient, "Saving");
//            });
//        }
//
//
//
//    }



    @AfterEach
    void tearDown() throws SQLException {
        String sql = "DELETE FROM bank_account where client_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, testClient.getClientID());
            pstmt.executeUpdate();
        }
        String sql2 = "DELETE FROM client where client_id=?";
        try (PreparedStatement pstmt2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
            pstmt2.setString(1, testClient.getClientID());
            pstmt2.executeUpdate();
        }

    }

}





