package def_pkg;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientTest {
    private Connection conn;
    Client c = new Client("Omar","Dardir", "Ahmed",
            "Sameha", "30501300106716", "30,01,2005", "01025012367",
            "omarahmed3001@gmail.com", "24, Doughnut St, City of Stars, La La Land");

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


    @Test
    void getById() throws SQLException {
        Client fetched = Client.getById(conn, "10001");
        assertEquals("Beshee8", fetched.getFName());
        assertEquals("Demorgan", fetched.getLName());
        assertEquals("Bashir", fetched.getFatherName());
        assertEquals("Set el 7abayb", fetched.getMotherName());
        assertEquals("305012600106744", fetched.getCNIC());
        assertEquals("2005-01-26", fetched.getDOB());
        assertEquals("01027827193", fetched.getPhone());
        assertEquals("Bashi8@gmail.com", fetched.getEmail());
        assertEquals("In our hearts", fetched.getAddress());

    }
    @Test
    void save() throws SQLException {
        c.save(conn);
        Client fetched = Client.getById(conn, c.getClientID());
        assertEquals("Omar", fetched.getFName());
        assertEquals("Dardir", fetched.getLName());
        assertEquals("Ahmed", fetched.getFatherName());
        assertEquals("Sameha", fetched.getMotherName());
        assertEquals("30501300106716", fetched.getCNIC());
        assertEquals("2005-01-30", fetched.getDOB());
        assertEquals("01025012367", fetched.getPhone());
        assertEquals("omarahmed3001@gmail.com", fetched.getEmail());
        assertEquals("24, Doughnut St, City of Stars, La La Land", fetched.getAddress());Client.getById(conn,c.getClientID());



    }

    @Test
    void getAccNumByCNIC() throws SQLException {
        assertEquals("500001", c.getAccNumByCNIC(conn,"305012600106744"));    }

    @Test
    void getByCNIC() {

    }

    @Test
    void transferMoney() {

    }





}