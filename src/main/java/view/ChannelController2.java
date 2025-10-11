package view;

import controller.ChannelController;
import model.Playlist;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChannelController2 extends AbstractController {
    private static final String PUBLISH_CONTENT_FXML = "/fxml/PublishContent.fxml";
    private static final String PLAYLIST_CONTENT_FXML = "/fxml/PlaylistContent.fxml";
    private static final String HOME_FXML = "/fxml/Home.fxml";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";

    @FXML private Button backButton;
    @FXML private Label channelNameLabel;
    @FXML private Label followersLabel;
    @FXML private TextArea descriptionField;
    @FXML private VBox playlistBox;
    @FXML private Button editButton;
    @FXML private Button publishButton;
    @FXML private Label errorLabel;

    private ChannelController channelController = new ChannelController();
    private User currentUser;

    @Override
    public void setData(Object data) {
        if (data instanceof User) {
            this.currentUser = (User) data;
            loadChannelInfo();
        } else {
            showMessage(errorLabel, "Invalid user data provided!", true);
            switchScene(LOGIN_FXML, null);
        }
    }

    private void loadChannelInfo() {
        if (currentUser == null) {
            showMessage(errorLabel, "User not logged in!", true);
            switchScene(LOGIN_FXML, null);
            return;
        }
        if (currentUser.getChannel() == null) {
            showMessage(errorLabel, "No channel found for this user!", true);
            return;
        }
        channelNameLabel.setText(currentUser.getChannel().getName());
        followersLabel.setText(currentUser.getChannel().getFollowersCount() + " Followers");
        String description = currentUser.getChannel().getDescription();
        descriptionField.setText(description != null ? description : "No description available");
        loadPlaylists();
    }

    private void loadPlaylists() {
        playlistBox.getChildren().clear();
        if (currentUser == null || currentUser.getChannel() == null || currentUser.getChannel().getPlaylists().isEmpty()) {
            showMessage(errorLabel, "No playlists found!", true);
            return;
        }
        for (Playlist playlist : currentUser.getChannel().getPlaylists()) {
            VBox playlistCard = new VBox(5);
            playlistCard.setStyle("-fx-background-color: #2c2f33; -fx-background-radius: 8; -fx-padding: 10;");
            Label nameLabel = new Label(playlist.getName());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
            Label countLabel = new Label(playlist.getContents().size() + " items");
            countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #adb5bd;");
            playlistCard.getChildren().addAll(nameLabel, countLabel);
            playlistCard.setOnMouseClicked(e -> {
                PlaylistContentController controller = switchSceneWithController(PLAYLIST_CONTENT_FXML, playlist);
                if (controller != null) {
                    controller.setCurrentUser(currentUser);
                }
            });
            playlistBox.getChildren().add(playlistCard);
        }
    }

    @FXML
    private void handleEdit() {

        String newDescription = descriptionField.getText().trim();
        if (newDescription.isEmpty()) {
            showMessage(errorLabel, "Description cannot be empty!", true);
            return;
        }
        String result = channelController.editChannel(currentUser, currentUser.getChannel().getName(), newDescription);
        showMessage(errorLabel, result, !result.contains("successfully"));
        loadChannelInfo();
    }

    @FXML
    private void handlePublish() {
        switchScene(PUBLISH_CONTENT_FXML, currentUser);
    }

    @FXML
    private void handleBack() {
        switchScene(HOME_FXML, currentUser);
    }

    private PlaylistContentController switchSceneWithController(String fxml, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            PlaylistContentController controller = loader.getController();
            if (data != null && controller instanceof BaseController) {
                ((BaseController) controller).setData(data);
            }
            stage.show();
            return controller;
        } catch (java.io.IOException e) {
            showMessage(errorLabel, "Failed to load page: Resource not found (" + fxml + ")", true);
            return null;
        } catch (Exception e) {
            showMessage(errorLabel, "Error loading page: " + e.getMessage(), true);
            return null;
        }
    }

    public void switchScene(String fxml, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) backButton.getScene().getWindow();
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