package def_pkg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BM_System extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("../GUI_Pages/LoginSignup/Login.fxml"));
		primaryStage.setTitle("Banking System");
		primaryStage.setScene(new Scene(root, 1000, 800));
		primaryStage.setResizable(true);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}