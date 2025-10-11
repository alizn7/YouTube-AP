package view;

import controller.UserController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Account;
import model.Admin;
import model.User;

public class SidebarController extends AbstractController {
    private static final String HOME_FXML = "/fxml/Home.fxml";
    private static final String SUBSCRIPTIONS_FXML = "/fxml/Subscription.fxml";
    private static final String CHANNEL_FXML = "/fxml/Channel.fxml";
    private static final String CREATE_CHANNEL_FXML = "/fxml/CreateChannel.fxml";
    private static final String LIBRARY_FXML = "/fxml/Library.fxml";
    private static final String ADMIN_PANEL_FXML = "/fxml/AdminPanel.fxml";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";

    @FXML private Button homeButton;
    @FXML private Button subscriptionsButton;
    @FXML private Button channelButton;
    @FXML private Button libraryButton;
    @FXML private Button adminButton;
    @FXML private Button logoutButton;

    private UserController userController = new UserController();
    private Account currentAccount;

    @FXML
    private void initialize() {
        // Set initial state
        subscriptionsButton.setVisible(false);
        channelButton.setVisible(false);
        libraryButton.setVisible(false);
        adminButton.setVisible(false);
        logoutButton.setVisible(false);
    }

    @Override
    public void setData(Object data) {
        this.currentAccount = (Account) data;
        if (currentAccount == null) {
            subscriptionsButton.setVisible(false);
            channelButton.setVisible(false);
            libraryButton.setVisible(false);
            adminButton.setVisible(false);
            logoutButton.setVisible(false);
        } else if (currentAccount instanceof Admin) {
            adminButton.setVisible(true);
            subscriptionsButton.setVisible(false);
            channelButton.setVisible(false);
            libraryButton.setVisible(false);
            logoutButton.setVisible(true);
        } else {
            subscriptionsButton.setVisible(true);
            channelButton.setVisible(true);
            libraryButton.setVisible(true);
            adminButton.setVisible(false);
            logoutButton.setVisible(true);
        }
    }

    @FXML
    private void handleHome() {
        switchScene(HOME_FXML, currentAccount);
    }

    @FXML
    private void handleSubscriptions() {

        switchScene(SUBSCRIPTIONS_FXML, currentAccount);
    }

    @FXML
    private void handleChannel() {
        if (currentAccount instanceof User && ((User) currentAccount).getChannel() == null) {
            switchScene(CREATE_CHANNEL_FXML, currentAccount);
        } else {
            switchScene(CHANNEL_FXML, currentAccount);
        }
    }

    @FXML
    private void handleLibrary() {
        switchScene(LIBRARY_FXML, currentAccount);
    }

    @FXML
    private void handleAdmin() {
        if (currentAccount instanceof Admin) {
            switchScene(ADMIN_PANEL_FXML, currentAccount);
        } else {
            showErrorAlert("Access denied: Admin privileges required!");
        }
    }

    @FXML
    private void handleLogout() {
        switchScene(LOGIN_FXML, null);
    }

    public void switchScene(String fxml, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(scene);

            if (data != null && loader.getController() instanceof BaseController) {
                ((BaseController) loader.getController()).setData(data);
            }

            stage.show();
        } catch (java.io.IOException e) {
            showErrorAlert("Failed to load page: Resource not found (" + fxml + ")");
        } catch (Exception e) {
            showErrorAlert("Error switching scene: " + e.getMessage());
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}