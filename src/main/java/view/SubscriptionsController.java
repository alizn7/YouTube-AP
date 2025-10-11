package view;

import controller.ChannelController;
import controller.UserController;
import javafx.scene.control.Button;
import model.Channel;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SubscriptionsController extends AbstractController {
    private static final String CHANNEL_INFO_FXML = "/fxml/ChannelInfo.fxml";
    private static final String HOME_FXML = "/fxml/Home.fxml";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";

    @FXML private Button backButton;
    @FXML private VBox channelBox;
    @FXML private Label errorLabel;

    private UserController userController = new UserController();
    private ChannelController channelController = new ChannelController();
    private User currentUser;

    @Override
    public void setData(Object data) {
        if (data instanceof User) {
            this.currentUser = (User) data;
            loadSubscriptions();
        } else {
            showMessage(errorLabel, "No user logged in!", true);
            switchScene(LOGIN_FXML, null);
        }
    }

    private void loadSubscriptions() {
        channelBox.getChildren().clear();
        if (currentUser == null) {
            showMessage(errorLabel, "No user logged in!", true);
            return;
        }
        if (currentUser.getSubscriptions().isEmpty()) {
            showMessage(errorLabel, "No subscribed channels!", true);
            return;
        }
        for (Channel channel : currentUser.getSubscriptions()) {
            HBox channelCard = new HBox(10);
            channelCard.setStyle("-fx-background-color: #2c2f33; -fx-background-radius: 8; -fx-padding: 10;");
            Label nameLabel = new Label(channel.getName());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ffffff;");
            Label followersLabel = new Label(channel.getFollowersCount() + " Followers");
            followersLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #adb5bd;");
            channelCard.getChildren().addAll(nameLabel, followersLabel);
            channelCard.setOnMouseClicked(e -> switchScene(CHANNEL_INFO_FXML, channel));
            channelBox.getChildren().add(channelCard);
        }
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