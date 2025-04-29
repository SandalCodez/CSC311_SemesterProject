package viewmodel;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.UserSession;

/**
 * Controller for user registration (signup) functionality
 */
public class SignUpController {

    @FXML
    private GridPane rootpane;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    /**
     * Initialize method called automatically when the view is loaded
     */
    public void initialize() {
        // Add fade-in animation if desired
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), rootpane);
        fadeIn.setFromValue(0.3);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    /**
     * Handle signup button click
     */
    @FXML
    public void handleSignUp(ActionEvent actionEvent) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match");
            return;
        }

        // Create user session with default privileges
        UserSession.getInstance(username, password);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully!");

        // Navigate to login screen
        navigateToLogin(actionEvent);
    }

    /**
     * Navigate back to login screen (same as goBack method in original FXML)
     */
    @FXML
    public void navigateToLogin(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);//1100, 750
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not navigate to login screen: " + e.getMessage());
        }
    }

    /**
     * Legacy method name from original FXML - simply redirects to handleSignUp
     */
    @FXML
    public void createNewAccount(ActionEvent actionEvent) {
        handleSignUp(actionEvent);
    }

    /**
     * Legacy method name from original FXML - simply redirects to navigateToLogin
     */
    @FXML
    public void goBack(ActionEvent actionEvent) {
        navigateToLogin(actionEvent);
    }

    /**
     * Utility method to show alerts
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
