package finalexamloginproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author user
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button registerButton;
    @FXML
    private Button loginButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button deleteUserButton;
    @FXML
    private Label loginMessage;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/javafx_demo";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Hide logout and delete user buttons initially
        logoutButton.setVisible(false);
        deleteUserButton.setVisible(false);
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Check for empty username or password
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            loginMessage.setText("Invalid input. Please enter both username and password.");
            loginMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        // Check if the username already exists
        if (usernameExists(usernameField.getText())) {
            loginMessage.setText("Username already exists. Please change the username.");
            loginMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        // Implement registration logic
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            statement.setString(1, usernameField.getText());
            statement.setString(2, passwordField.getText());
            statement.executeUpdate();

            // Show login page after successful registration
            showLoginPage();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
// Check if the username already exists in the database

    private boolean usernameExists(String username) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=?")) {
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // If resultSet has any rows, username already exists
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        // Implement login logic
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {
            statement.setString(1, usernameField.getText());
            statement.setString(2, passwordField.getText());

            // Execute the query and handle the result
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Successful login
                loginMessage.setText("Login successful");
                // Show the logout and delete buttons
                logoutButton.setVisible(true);
                deleteUserButton.setVisible(true);
                // Hide the login and register buttons
                loginButton.setVisible(false);
                registerButton.setVisible(false);
            } else {
                // Invalid login
                loginMessage.setText("Invalid username/password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Show login page after logout
        showLoginPage();
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        // Implement delete user logic
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE username=?")) {
            statement.setString(1, usernameField.getText());

            // Execute the update
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                // User deleted successfully
                loginMessage.setText("User deleted successfully");
                // Show the login and register buttons
                loginButton.setVisible(true);
                registerButton.setVisible(true);
                // Hide the logout and delete buttons
                logoutButton.setVisible(false);
                deleteUserButton.setVisible(false);
            } else {
                // User not found or deletion failed
                loginMessage.setText("User not found or deletion failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showLoginPage() {
        // Clear fields and reset login message
        usernameField.clear();
        passwordField.clear();
        loginMessage.setText("");
        loginMessage.setStyle("-fx-text-fill: black;");

        // Hide logout and delete user buttons
        logoutButton.setVisible(false);
        deleteUserButton.setVisible(false);

        // Show registration and login buttons
        registerButton.setVisible(true);
        loginButton.setVisible(true);
    }

    private void showLoggedInButtons() {
        // Hide registration and login buttons
        registerButton.setVisible(false);
        loginButton.setVisible(false);

        // Show logout and delete user buttons
        logoutButton.setVisible(true);
        deleteUserButton.setVisible(true);
    }
}
