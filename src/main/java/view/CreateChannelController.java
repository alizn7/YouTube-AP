package view;

import controller.ChannelController;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateChannelController extends AbstractController {
    private static final String CHANNEL_FXML = "/fxml/Channel.fxml";
    private static final String LIBRARY_FXML = "/fxml/Library.fxml";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";

    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private Button createButton;
    @FXML private Button backButton;
    @FXML private Label errorLabel;
    private ChannelController channelController = new ChannelController();
    private User currentUser;

    @Override
    public void setData(Object data) {
        if (data instanceof User) {
            this.currentUser = (User) data;
        } else {
            showMessage(errorLabel, "Invalid user data or not logged in!", true);
            switchScene(LOGIN_FXML, null);
        }
    }

    @FXML
    private void handleCreate() {
        if (currentUser == null) {
            showMessage(errorLabel, "User not logged in!", true);
            switchScene(LOGIN_FXML, null);
            return;
        }

        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        if (name.isEmpty() || name.length() > 100) {
            showMessage(errorLabel, "Channel name must be between 1 and 100 characters!", true);
            return;
        }
        if (description.isEmpty() || description.length() > 500) {
            showMessage(errorLabel, "Description must be between 1 and 500 characters!", true);
            return;
        }

        String result = String.valueOf(channelController.createChannel(currentUser, name, description));
        showMessage(errorLabel, result, !result.contains("successfully"));
        if (result.contains("successfully")) {
            switchScene(CHANNEL_FXML, currentUser);
        }
    }

    @FXML
    private void handleBack() {
        switchScene(LIBRARY_FXML, currentUser);
    }

    public void switchScene(String fxml, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) createButton.getScene().getWindow();
            stage.setScene(scene);
            if (data != null && loader.getController() instanceof BaseController) {
                ((BaseController) loader.getController()).setData(data);
            }
            stage.show();
        } catch (java.io.IOException e) {
            showMessage(errorLabel, "Failed to load page: Resource not found (" + fxml + ")", true);
        } catch (Exception e) {
            showMessage(errorLabel, "Error loading page: " + e.getMessage(), true);
        }
    }

    public void showMessage(Label label, String message, boolean isError) {
        label.setText(message);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (isError ? "#ff6b6b" : "#28a745") + ";");
    }
}