package def_pkg.Controllers.ManagerCon;

import def_pkg.DB_handler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import def_pkg.Manager;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ManagerDashboardController {
    private Manager manager;

    @FXML private Label welcomeLabel;
    @FXML private Label totalAccountsLabel;
    @FXML private Label totalEmployeesLabel;

    @FXML private Button signOutButton;
    @FXML private Button viewAllAccountsBtn;
    @FXML private Button viewAllAccountsBtn2;
    @FXML private Button CreateAccountBtn;
    @FXML private Button CloseAccountBtn;
    @FXML private Button blockUnblockBtn;
    @FXML private Button blockUnblockBtn2;
    @FXML private Button UpdateAccountBtn;

    public void setManagerData(Manager manager) {
        System.out.println("Manager is: " + manager.getName());
        this.manager = manager;
        updateUI(manager); // This will safely call loadDashboardData
    }
    public void updateUI(Manager manager){
        welcomeLabel.setText("Welcome, " + this.manager.getName());
        loadDashboardData(manager);
    }
    @FXML
    public void initialize() {
//        this.manager = new Manager(conn);

        CreateAccountBtn.setOnAction(e->openCreateAccountpage());
        viewAllAccountsBtn.setOnAction(e-> openViewAccountspage());
        viewAllAccountsBtn2.setOnAction(e-> openViewAccountspage());
        CloseAccountBtn.setOnAction(e->openCloseAccountpage());
        blockUnblockBtn.setOnAction(e->openBlockAccountpage());
        blockUnblockBtn2.setOnAction(e->openBlockAccountpage());
        UpdateAccountBtn.setOnAction(e->openUpdateAccountpage());

    }



    private void loadDashboardData(Manager manager) {
        if (manager == null) {
            System.err.println("Manager is not set!");
            return;
        }

        try (DB_handler db = new DB_handler()) {
            Connection conn = db.getConnection();
            welcomeLabel.setText("Welcome, " + this.manager.getName());
            int totalAccounts = manager.getTotalAccounts(conn,manager); // Corrected call
            System.out.println("Total Accounts: " + totalAccounts);
             totalAccountsLabel.setText(String.valueOf(totalAccounts));
             totalEmployeesLabel.setText(String.valueOf(manager.getTotalEmployees(conn,manager)));

            } catch (SQLException e) {
            showAlert("Database Error", "Failed to load dashboard data: " + e.getMessage());
        }
    }
    private void openCreateAccountpage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../GUI_Pages/Client/ClientDeposit.fxml"));
            Parent root = loader.load();

            CreateAccountController  controller = loader.getController();
            controller.setManagerData(manager);
            Stage stage = (Stage) CreateAccountBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open account info");
        }
    }
    private void openUpdateAccountpage() {
    }

    private void openBlockAccountpage() {
    }

    private void openCloseAccountpage() {
    }

    private void openViewAccountspage() {
    }




//    @FXML
//    private void handleViewAllAccounts() {
//        try {
//            // Implementation to view all accounts
//            manager.viewAllAccounts();
//        } catch (SQLException e) {
//            showAlert("Error", "Failed to view accounts: " + e.getMessage());
//        }
//    }


//
//    @FXML
//    private void handleGenerateReports() {
//        try {
//            // Implementation for report generation
//            manager.generateFinancialReports();
//        } catch (SQLException e) {
//            showAlert("Error", "Failed to generate reports: " + e.getMessage());
//        }
//    }

//    @FXML
//    private void handleSignOut() {
//        // Implementation for sign out
//        try {
//            if (conn != null) {
//                conn.close();
//            }
//            // Navigate to login screen
//        } catch (SQLException e) {
//            showAlert("Error", "Failed to sign out: " + e.getMessage());
//        }
//    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}