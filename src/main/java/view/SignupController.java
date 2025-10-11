package view;

import controller.UserController;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.BaseController;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class SignupController implements Initializable, BaseController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Label errorLabel;
    @FXML private Label fileLabel;
    @FXML private MediaView mediaView;
    private UserController userController = new UserController();
    private MediaPlayer mediaPlayer;
    private File profilePicture;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            String videoPath = getClass().getResource("/videos/signup_background.mp4").toExternalForm();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setMute(true);
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error loading background video.");
        }
    }

    @FXML
    private void handleSignup() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String profilePicturePath = profilePicture != null ? profilePicture.getPath() : null;

        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            errorLabel.setText("Please fill in all required fields.");
            return;
        }

        User newUser = userController.signUp(username, password, firstName, lastName, email, phone, profilePicturePath);
        if (newUser != null) {
            stopVideo();
            switchScene("/fxml/CategorySelection.fxml", newUser);
        } else {
            errorLabel.setText(userController.getLastMessage());
        }
    }

    @FXML
    private void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        profilePicture = fileChooser.showOpenDialog(null);
        if (profilePicture != null) {
            fileLabel.setText(profilePicture.getName());
        }
    }

    @FXML
    private void goToLogin() {
        stopVideo();
        switchScene("/fxml/Login.fxml", null);
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
            e.printStackTrace();
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