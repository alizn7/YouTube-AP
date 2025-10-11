package view;

import controller.AdminController;
import controller.UserController;
import javafx.fxml.Initializable;
import model.Admin;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable, BaseController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private MediaView mediaView;
    private UserController userController = new UserController();
    private AdminController adminController = new AdminController();
    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            String videoPath = Objects.requireNonNull(getClass().getResource("/videos/background_video.mp4")).toExternalForm();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setAutoPlay(true);
        } catch (Exception e) {
            System.err.println("Error loading background video: " + e.getMessage());
            errorLabel.setText("Failed to load background video.");
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        User user = userController.login(username, password);
        if (user != null) {
            stopVideo();
            switchScene("/fxml/Home.fxml", user);
        } else {
            Admin admin = adminController.login(username, password);
            if (admin != null) {
                stopVideo();
                switchScene("/fxml/AdminPanel.fxml", admin);
            } else {
                errorLabel.setText(userController.getLastMessage());
            }
        }
    }

    @FXML
    private void goToSignup() {
        stopVideo();
        switchScene("/fxml/Signup.fxml", null);
    }

    private void switchScene(String fxml, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);

            if (data != null && loader.getController() instanceof BaseController) {
                ((BaseController) loader.getController()).setData(data);
            }

            stage.show();
        } catch (Exception e) {
            System.err.println("Error switching scene: " + e.getMessage());
            errorLabel.setText("Error loading page.");
        }
    }

    private void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    @Override
    public void setData(Object data) {
    }
}