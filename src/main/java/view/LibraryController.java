package view;

import controller.PlaylistController;
import controller.UserController;
import model.Playlist;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibraryController extends AbstractController {
    private static final String PLAYLIST_CONTENT_FXML = "/fxml/PlaylistContent.fxml";
    private static final String SUBSCRIPTION_PURCHASE_FXML = "/fxml/SubscriptionPurchase.fxml";
    private static final String INCREASE_BALANCE_FXML = "/fxml/IncreaseBalance.fxml";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";
    private static final String HOME_FXML = "/fxml/Home.fxml";

    @FXML private Button backButton;
    @FXML private Label usernameLabel;
    @FXML private Label balanceLabel;
    @FXML private VBox playlistBox;
    @FXML private Button createPlaylistButton;
    @FXML private Button buyPremiumButton;
    @FXML private Button increaseBalanceButton;
    @FXML private Button logoutButton;
    @FXML private Label errorLabel;

    private UserController userController = new UserController();
    private PlaylistController playlistController = new PlaylistController();
    private User currentUser;

    @Override
    public void setData(Object data) {
        if (data instanceof User) {
            this.currentUser = (User) data;
            loadUserInfo();
            loadPlaylists();
        } else {
            showMessage(errorLabel, "Invalid user data provided!", true);
            switchScene(LOGIN_FXML, null);
        }
    }

    private void loadUserInfo() {
        if (currentUser == null) {
            showMessage(errorLabel, "User not logged in!", true);
            return;
        }
        usernameLabel.setText(currentUser.getUsername());
        balanceLabel.setText("Balance: " + currentUser.getBalance());
    }

    private void loadPlaylists() {
        playlistBox.getChildren().clear();
        if (currentUser == null || currentUser.getPlaylists().isEmpty()) {
            showMessage(errorLabel, "No playlists found!", true);
            return;
        }
        for (Playlist playlist : currentUser.getPlaylists()) {
            VBox playlistCard = new VBox(5);
            playlistCard.setStyle("-fx-background-color: #2c2f33; -fx-background-radius: 8; -fx-padding: 10;");
            Label nameLabel = new Label(playlist.getName());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
            Label countLabel = new Label(playlist.getContents().size() + " items");
            countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #adb5bd;");
            playlistCard.getChildren().addAll(nameLabel, countLabel);
            playlistCard.setOnMouseClicked(e -> switchScene(PLAYLIST_CONTENT_FXML, playlist));
            playlistBox.getChildren().add(playlistCard);
        }
    }

    @FXML
    private void handleCreatePlaylist() {
        if (currentUser == null) {
            showMessage(errorLabel, "User not logged in!", true);
            switchScene(LOGIN_FXML, null);
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Playlist");
        dialog.setHeaderText("Enter playlist name:");
        dialog.setContentText("Name:");
        dialog.showAndWait().ifPresent(name -> {
            String result = String.valueOf(playlistController.createPlaylist(currentUser, name));
            showMessage(errorLabel, result, !result.contains("successfully"));
            loadPlaylists();
        });
    }

    @FXML
    private void handleBuyPremium() {
        if (currentUser == null) {
            showMessage(errorLabel, "User not logged in!", true);
            switchScene(LOGIN_FXML, null);
            return;
        }
        switchScene(SUBSCRIPTION_PURCHASE_FXML, currentUser);
    }

    @FXML
    private void handleIncreaseBalance() {
        if (currentUser == null) {
            showMessage(errorLabel, "User not logged in!", true);
            switchScene(LOGIN_FXML, null);
            return;
        }
        switchScene(INCREASE_BALANCE_FXML, currentUser);
    }

    @FXML
    private void handleLogout() {
        if (currentUser != null) {
            userController.logout(currentUser);
        }
        switchScene(LOGIN_FXML, null);
    }

    @FXML
    private void handleBack() {
        switchScene(HOME_FXML, currentUser);
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