//package viewmodel;
//
//import javafx.animation.FadeTransition;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.geometry.Side;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.image.Image;
//import javafx.scene.layout.*;
//import javafx.stage.Stage;
//import javafx.util.Duration;
//
//
//
//public class LoginController {
//
//
//    @FXML
//    private GridPane rootpane;
//    public void initialize() {
//        rootpane.setBackground(new Background(
//                        createImage("https://edencoding.com/wp-content/uploads/2021/03/layer_06_1920x1080.png"),
//                        null,
//                        null,
//                        null,
//                        null,
//                        null
//                )
//        );
//
//
//        rootpane.setOpacity(0);
//        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(10), rootpane);
//        fadeOut2.setFromValue(0);
//        fadeOut2.setToValue(1);
//        fadeOut2.play();
//    }
//    private static BackgroundImage createImage(String url) {
//        return new BackgroundImage(
//                new Image(url),
//                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
//                new BackgroundPosition(Side.LEFT, 0, true, Side.BOTTOM, 0, true),
//                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true));
//    }
//    @FXML
//    public void login(ActionEvent actionEvent) {
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("/view/db_interface_gui.fxml"));
//            Scene scene = new Scene(root, 900, 600);
//            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
//            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
//            window.setScene(scene);
//            window.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void signUp(ActionEvent actionEvent) {
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("/view/signUp.fxml"));
//            Scene scene = new Scene(root, 900, 600);
//            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
//            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
//            window.setScene(scene);
//            window.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
package viewmodel;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.UserSession;

import java.util.prefs.Preferences;

public class LoginController {
    @FXML
    private GridPane rootpane;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    public void initialize() {
        // Set background if needed - using existing background code
        rootpane.setBackground(new Background(
                createImage("https://edencoding.com/wp-content/uploads/2021/03/layer_06_1920x1080.png"),
                null,
                null,
                null,
                null,
                null
        ));

        // Add fade-in animation
        rootpane.setOpacity(0);
        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(1.5), rootpane);
        fadeOut2.setFromValue(0);
        fadeOut2.setToValue(1);
        fadeOut2.play();

        // Check for stored credentials (optional auto-fill)
        checkStoredCredentials();
    }

    private void checkStoredCredentials() {
        // Try to load any previously stored credentials
        UserSession storedSession = UserSession.loadFromPreferences();

        // If found, you could auto-fill the username field (but not password for security)
        if (storedSession != null) {
            usernameTextField.setText(storedSession.getUserName());
            // Don't auto-fill password for security
        }
    }

    private static BackgroundImage createImage(String url) {
        return new BackgroundImage(
                new Image(url),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, true, Side.BOTTOM, 0, true),
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true));
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        String username = usernameTextField.getText().trim();
        String password = passwordField.getText();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username and password are required");
            return;
        }

        // Check credentials using preferences system
        Preferences userPreferences = Preferences.userRoot().node("app/usersession");
        String storedUsername = userPreferences.get("USERNAME", null);
        String storedPassword = userPreferences.get("PASSWORD", null);

        if (storedUsername != null && storedPassword != null &&
                username.equals(storedUsername) && password.equals(storedPassword)) {

            // Create session with stored privileges
            String privileges = userPreferences.get("PRIVILEGES", "NONE");
            UserSession.getInstance(username, password, privileges);

            // Navigate to main screen
            navigateToMainScreen(actionEvent);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password");
        }
    }

    private void navigateToMainScreen(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/db_interface_gui.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load main interface: " + e.getMessage());
        }
    }

    @FXML
    public void signUp(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/signUp.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load signup page: " + e.getMessage());
        }
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
