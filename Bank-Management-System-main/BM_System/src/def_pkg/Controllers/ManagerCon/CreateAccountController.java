package def_pkg.Controllers.ManagerCon;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import def_pkg.Manager;
import java.sql.Connection;

public class CreateAccountController {
    @FXML private TextField clientIdField;
    @FXML private ComboBox<String> accountTypeCombo;
    @FXML private TextField initialDepositField;
    @FXML private Button createButton;
    @FXML private Button backButton;

    private Manager manager;

    public void setManagerData(Manager manager) {
        System.out.println("Manager is: " + manager.getName());
        this.manager = manager;
    }

    public void initialize(Connection conn) {

    }

    @FXML
    private void handleCreate() {
        // Implement account creation logic
    }

    @FXML
    private void handleBack() {
        // Navigate back to dashboard
    }
}