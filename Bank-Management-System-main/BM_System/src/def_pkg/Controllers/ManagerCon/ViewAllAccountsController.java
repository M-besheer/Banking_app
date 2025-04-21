package def_pkg.Controllers.ManagerCon;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import def_pkg.Manager;
import java.sql.Connection;

public class ViewAllAccountsController {
    @FXML private TableView<?> accountsTable;
    @FXML private Button backButton;

    private Manager manager;
    private Connection conn;

    public void initialize(Connection conn) {
        this.conn = conn;
       // this.manager = new Manager(conn);
        loadAllAccounts();
    }

    private void loadAllAccounts() {
        // Implement account loading logic
    }

    @FXML
    private void handleBack() {
        // Navigate back to dashboard
    }
}