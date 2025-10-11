package view;

import controller.ChannelController;
import model.Channel;
import model.Playlist;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChannelInfoController extends AbstractController {
    private static final String PLAYLIST_CONTENT_FXML = "/fxml/PlaylistContent.fxml";
    private static final String SUBSCRIPTIONS_FXML = "/fxml/Subscriptions.fxml";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";

    @FXML private Button backButton;
    @FXML private Label channelNameLabel;
    @FXML private Label followersLabel;
    @FXML private Label descriptionLabel;
    @FXML private Button followButton;
    @FXML private VBox playlistBox;
    @FXML private Label errorLabel;

    private ChannelController channelController = new ChannelController();
    private User currentUser;
    private Channel currentChannel;

    @Override
    public void setData(Object data) {
        if (data instanceof Channel && currentUser != null) {
            this.currentChannel = (Channel) data;
            loadChannelInfo();
        } else {
            showMessage(errorLabel, "Invalid channel data or user not logged in!", true);
            switchScene(LOGIN_FXML, null);
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void loadChannelInfo() {
        if (currentChannel == null) {
            showMessage(errorLabel, "Channel not found!", true);
            return;
        }
        channelNameLabel.setText(currentChannel.getName());
        followersLabel.setText(currentChannel.getFollowersCount() + " Followers");
        descriptionLabel.setText(currentChannel.getDescription() != null ? currentChannel.getDescription() : "No description");
        updateFollowButton();
        loadPlaylists();
    }

    private void updateFollowButton() {
        boolean isSubscribed = currentUser.getSubscriptions().contains(currentChannel);
        followButton.setText(isSubscribed ? "Unfollow" : "Follow");
    }

    private void loadPlaylists() {
        playlistBox.getChildren().clear();
        if (currentChannel.getPlaylists().isEmpty()) {
            showMessage(errorLabel, "No playlists found!", true);
            return;
        }
        for (Playlist playlist : currentChannel.getPlaylists()) {
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
    private void handleFollow() {
        if (currentUser == null) {
            showMessage(errorLabel, "Please log in to follow!", true);
            switchScene(LOGIN_FXML, null);
            return;
        }
        String result;
        if (currentUser.getSubscriptions().contains(currentChannel)) {
            result = channelController.unsubscribe(currentUser, currentChannel.getChannelId());
        } else {
            result = channelController.subscribe(currentUser, currentChannel.getChannelId());
        }
        showMessage(errorLabel, result, !result.contains("successfully"));
        updateFollowButton();
        followersLabel.setText(currentChannel.getFollowersCount() + " Followers");
    }

    @FXML
    private void handleBack() {
        switchScene(SUBSCRIPTIONS_FXML, currentUser);
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