package view;

import controller.ContentController;
import controller.PlaylistController;
import controller.ReportController;
import model.Comment;
import model.Content;
import model.Playlist;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class ContentPlayerController extends AbstractController {
    private static final String HOME_FXML = "/fxml/Home.fxml";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";
    private static final String PLACEHOLDER_IMAGE = "/images/placeholder.png";

    @FXML private MediaView mediaView;
    @FXML private Button backButton;
    @FXML private Button playPauseButton;
    @FXML private Slider volumeSlider;
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label viewsLabel;
    @FXML private Label likesLabel;
    @FXML private Button likeButton;
    @FXML private Button commentButton;
    @FXML private Button reportButton;
    @FXML private Button addToPlaylistButton;
    @FXML private TextArea commentField;
    @FXML private VBox commentBox;
    @FXML private Label errorLabel;
    @FXML private ImageView coverImage;

    private ContentController contentController = new ContentController();
    private PlaylistController playlistController = new PlaylistController();
    private ReportController reportController = new ReportController();
    private User currentUser;
    private Content currentContent;
    private MediaPlayer mediaPlayer;

    @Override
    public void setData(Object data) {
        if (data instanceof Content && currentUser != null) {
            this.currentContent = (Content) data;
            loadContent();
        } else {
            showMessage(errorLabel, "Invalid content data or user not logged in!", true);
            switchScene(LOGIN_FXML, null);
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void loadContent() {
        titleLabel.setText(currentContent.getTitle());
        descriptionLabel.setText(currentContent.getDescription());
        viewsLabel.setText(currentContent.getViews() + " views");
        likesLabel.setText(currentContent.getLikes() + " likes");

        try {
            String mediaLink = currentContent.getFileLink();
            if (mediaLink != null && !mediaLink.isEmpty()) {
                Media media = new Media(mediaLink);
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.setAutoPlay(true);
                volumeSlider.valueProperty().addListener((obs, old, newVal) -> {
                    if (mediaPlayer != null) {
                        mediaPlayer.setVolume(newVal.doubleValue());
                    }
                });
                mediaView.setVisible(true);
                coverImage.setVisible(false);
            } else {
                throw new IllegalArgumentException("No media file available");
            }
        } catch (Exception e) {
            try {
                String coverLink = currentContent.getCover();
                if (coverLink != null && !coverLink.isEmpty()) {
                    coverImage.setImage(new Image(coverLink));
                } else {
                    coverImage.setImage(new Image(getClass().getResourceAsStream(PLACEHOLDER_IMAGE)));
                }
                coverImage.setVisible(true);
                mediaView.setVisible(false);
                playPauseButton.setDisable(true);
            } catch (Exception ex) {
                showMessage(errorLabel, "Error loading media or cover: " + ex.getMessage(), true);
            }
        }
        loadComments();
        String result = contentController.play(currentUser, currentContent.getId());
        showMessage(errorLabel, result, !result.contains("successfully"));
    }

    private void loadComments() {
        commentBox.getChildren().clear();
        for (Comment comment : currentContent.getComments()) {
            VBox commentCard = new VBox(5);
            commentCard.setStyle("-fx-background-color: #2c2f33; -fx-background-radius: 8; -fx-padding: 10;");
            Label usernameLabel = new Label(comment.getCommenterUsername());
            usernameLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 12px;");
            Label textLabel = new Label(comment.getText());
            textLabel.setStyle("-fx-text-fill: #adb5bd; -fx-font-size: 12px;");
            commentCard.getChildren().addAll(usernameLabel, textLabel);
            commentBox.getChildren().add(commentCard);
        }
    }

    @FXML
    private void handleBack() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        switchScene(HOME_FXML, currentUser);
    }

    @FXML
    private void handlePlayPause() {
        if (mediaPlayer == null) {
            showMessage(errorLabel, "No media loaded!", true);
            return;
        }
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseButton.setText("Play");
        } else {
            mediaPlayer.play();
            playPauseButton.setText("Pause");
        }
    }

    @FXML
    private void handleLike() {
        String result = contentController.like(currentUser, currentContent.getId());
        showMessage(errorLabel, result, !result.contains("successfully"));
        likesLabel.setText(currentContent.getLikes() + " likes");
    }

    @FXML
    private void handleComment() {
        String commentText = commentField.getText().trim();
        if (commentText.isEmpty()) {
            showMessage(errorLabel, "Comment cannot be empty!", true);
            return;
        }
        String result = contentController.addComment(currentUser, currentContent.getId(), commentText);
        showMessage(errorLabel, result, !result.contains("successfully"));
        if (result.contains("successfully")) {
            commentField.clear();
            loadComments();
        }
    }

    @FXML
    private void handleReport() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Report Content");
        dialog.setHeaderText("Enter reason for report:");
        dialog.setContentText("Reason:");
        dialog.showAndWait().ifPresent(reason -> {
            String result = String.valueOf(reportController.report(currentUser, currentContent.getId(), reason));
            showMessage(errorLabel, result, !result.contains("successfully"));
        });
    }

    @FXML
    private void handleAddToPlaylist() {
        ChoiceDialog<Playlist> dialog = new ChoiceDialog<>(null, currentUser.getPlaylists());
        dialog.setTitle("Add to Playlist");
        dialog.setHeaderText("Select a playlist:");
        dialog.setContentText("Playlist:");
        dialog.showAndWait().ifPresent(playlist -> {
            String result = playlistController.addToPlaylist(currentUser, playlist.getId(), currentContent.getId());
            showMessage(errorLabel, result, !result.contains("successfully"));
        });
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