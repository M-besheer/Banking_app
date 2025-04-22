package def_pkg.Controllers.ManagerCon;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import def_pkg.Manager;
import java.sql.Connection;

public class BlockUnblockController {
    @FXML private TextField accountNumberField;
    @FXML private ComboBox<String> actionCombo;
    @FXML private Button submitButton;
    @FXML private Button backButton;

    private Manager manager;

    public void setManagerData(Manager manager) {
        System.out.println("Manager is: " + manager.getName());
        this.manager = manager;
    }
    public void initialize() {
        actionCombo.getItems().addAll("Block", "Unblock");
    }

    @FXML
    private void handleSubmit() {
        // Implement block/unblock logic
    }

    @FXML
    private void handleBack() {
        // Navigate back to dashboard
    }
}