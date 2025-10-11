package view;

import controller.ContentController;
import model.Content;
import model.Playlist;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PlaylistContentController extends AbstractController {
    private static final String CONTENT_PLAYER_FXML = "/fxml/ContentPlayer.fxml";
    private static final String LIBRARY_FXML = "/fxml/Library.fxml";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";
    private static final String PLACEHOLDER_IMAGE = "/images/placeholder.png";

    @FXML private Button backButton;
    @FXML private Label playlistNameLabel;
    @FXML private GridPane contentGrid;
    @FXML private Label errorLabel;

    private ContentController contentController = new ContentController();
    private User currentUser;
    private Playlist currentPlaylist;

    @Override
    public void setData(Object data) {
        if (data instanceof Playlist && currentUser != null) {
            this.currentPlaylist = (Playlist) data;
            loadPlaylistContent();
        } else {
            showMessage(errorLabel, "Invalid playlist data or user not logged in!", true);
            switchScene(LOGIN_FXML, null);
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void loadPlaylistContent() {
        playlistNameLabel.setText(currentPlaylist.getName());
        contentGrid.getChildren().clear();
        if (currentPlaylist.getContents().isEmpty()) {
            showMessage(errorLabel, "No content in this playlist!", true);
            return;
        }
        int row = 0, col = 0;
        for (Content content : currentPlaylist.getContents()) {
            if (content.canUserAccess(currentUser)) {
                VBox contentCard = createContentCard(content);
                contentGrid.add(contentCard, col, row);
                col++;
                if (col > 2) {
                    col = 0;
                    row++;
                }
            }
        }
        if (contentGrid.getChildren().isEmpty()) {
            showMessage(errorLabel, "No accessible content in this playlist!", true);
        }
    }

    private VBox createContentCard(Content content) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: #2c2f33; -fx-background-radius: 8; -fx-padding: 10;");

        ImageView coverImage = new ImageView();
        coverImage.setFitHeight(100);
        coverImage.setFitWidth(150);
        try {
            String coverLink = content.getCover();
            if (coverLink != null && !coverLink.isEmpty()) {
                coverImage.setImage(new Image(coverLink));
            } else {
                coverImage.setImage(new Image(getClass().getResourceAsStream(PLACEHOLDER_IMAGE)));
            }
        } catch (Exception e) {
            coverImage.setImage(new Image(getClass().getResourceAsStream(PLACEHOLDER_IMAGE)));
        }

        Label titleLabel = new Label(content.getTitle());
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        Label creatorLabel = new Label(content.getContentOwnerUsername());
        creatorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #adb5bd;");
        Label typeLabel = new Label(content.getTitle());
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #adb5bd;");
        card.getChildren().addAll(coverImage, titleLabel, creatorLabel, typeLabel);
        card.setOnMouseClicked(e -> switchScene(CONTENT_PLAYER_FXML, content));
        return card;
    }

    @FXML
    private void handleBack() {
        switchScene(LIBRARY_FXML, currentUser);
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