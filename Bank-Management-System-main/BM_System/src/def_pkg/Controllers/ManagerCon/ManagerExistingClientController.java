package def_pkg.Controllers.ManagerCon;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import def_pkg.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class ManagerExistingClientController {
    private Manager manager;
    @FXML private TextField cnicField;
    @FXML private ComboBox<String> accountTypeCombo;
    @FXML Button createButton;
    @FXML Button backButton;
    @FXML private Label messageLabel;

    public void setManager(Manager manager) {
        this.manager = manager;
    }
    // Method to initialize the controller
    @FXML
    public void initialize() {
        // Populate the account type ComboBox with options
        accountTypeCombo.getItems().addAll("Savings", "Current");
        accountTypeCombo.setPromptText("Select Account Type");
        createButton.setOnAction(e-> handleCreateAccount());
        backButton.setOnAction(e-> handleback());
    }

    // Action method for the "Create Account" button
    @FXML private void handleCreateAccount() {
        String cnic = cnicField.getText();
        String accountType = accountTypeCombo.getValue();

        // Clear previous messages
        messageLabel.setText("");
        messageLabel.setStyle("-fx-text-fill: red;");

        // Validate input
        if (cnic == null || cnic.trim().isEmpty()) {
            messageLabel.setText("CNIC field cannot be empty.");
            messageLabel.setStyle("-fx-text-fill: white;-fx-background-color: red;-fx-font-weight: bold;");
            return;
        }

        if (accountType == null) {
            messageLabel.setText("Please select an account type.");
            messageLabel.setStyle("-fx-text-fill: white;-fx-background-color: red;-fx-font-weight: bold;");
            return;
        }

        // Simulate account creation logic
        boolean accountCreated = createAccountForClient(cnic, accountType);

        if (accountCreated) {
            messageLabel.setStyle("-fx-text-fill: white;-fx-background-color: green;-fx-font-weight: bold;");
            messageLabel.setText("Account successfully created for CNIC: " + cnic);
        } else {
            messageLabel.setText("Client with coressponding CNIC does not exist!");
            messageLabel.setStyle("-fx-text-fill: white;-fx-background-color: red;-fx-font-weight: bold;");
        }
    }

    // Simulated method to handle account creation logic (replace with actual implementation)
    private boolean createAccountForClient(String cnic, String accountType) {
        try(DB_handler db = new DB_handler()){
            Connection conn = db.getConnection();
            if(Client.getByCNIC(conn,cnic)==null){
                return false;
            }
            else {
                manager.createAccount(conn, Objects.requireNonNull(Client.getByCNIC(conn, cnic)), accountType);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @FXML
    private void handleback(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../GUI_Pages/Manager/ManagerDashboard.fxml"));
            Parent root = loader.load();
            ManagerDashboardController controller = loader.getController();
            controller.setManagerData(manager);
            Scene scene = new Scene(root);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
