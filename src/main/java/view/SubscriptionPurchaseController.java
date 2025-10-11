package view;

import controller.UserController;
import model.SubscriptionPlan;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SubscriptionPurchaseController extends AbstractController {
    private static final String LIBRARY_FXML = "/fxml/Library.fxml";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";

    @FXML private Button backButton;
    @FXML private VBox planBox;
    @FXML private Label errorLabel;

    private UserController userController = new UserController();
    private User currentUser;

    @FXML
    private void initialize() {
        loadPlans();
    }

    @Override
    public void setData(Object data) {
        if (data instanceof User) {
            this.currentUser = (User) data;
        } else {
            showMessage(errorLabel, "Invalid user data provided!", true);
            switchScene(LOGIN_FXML, null);
        }
    }

    private void loadPlans() {
        planBox.getChildren().clear();
        for (SubscriptionPlan plan : SubscriptionPlan.values()) {
            HBox planCard = new HBox(10);
            planCard.setStyle("-fx-background-color: #2c2f33; -fx-background-radius: 8; -fx-padding: 10;");
            Label planLabel = new Label(plan.name() + " Plan - $" + plan.getPrice());
            planLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ffffff;");
            Button purchaseButton = new Button("Purchase");
            purchaseButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8;");
            purchaseButton.setOnAction(e -> purchasePlan(plan));
            planCard.getChildren().addAll(planLabel, purchaseButton);
            planBox.getChildren().add(planCard);
        }
    }

    private void purchasePlan(SubscriptionPlan plan) {
        if (currentUser == null) {
            showMessage(errorLabel, "User not logged in! Please log in again.", true);
            switchScene(LOGIN_FXML, null);
            return;
        }
        String result = String.valueOf(userController.getPremium(currentUser, String.valueOf(plan)));
        showMessage(errorLabel, result, !result.contains("successfully"));
        if (result.contains("successfully")) {
            switchScene(LIBRARY_FXML, currentUser);
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

    protected void showMessage(Label label, String message, boolean isError) {
        label.setText(message);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (isError ? "#ff6b6b" : "#28a745") + ";");
    }
}