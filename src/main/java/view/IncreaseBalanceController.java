package view;

import controller.UserController;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class IncreaseBalanceController extends AbstractController {
    private static final String LIBRARY_FXML = "/fxml/Library.fxml";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";

    @FXML private TextField amountField;
    @FXML private Button increaseButton;
    @FXML private Button backButton;
    @FXML private Label errorLabel;
    private UserController userController = new UserController();
    private User currentUser;

    @Override
    public void setData(Object data) {
                  switchScene(LOGIN_FXML, null);
        }


    @FXML
    private void handleIncrease() {

        String amountText = amountField.getText().trim();
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showMessage(errorLabel, "Amount must be positive!", true);
                return;
            }
            if (amount > 10000) {
                showMessage(errorLabel, "Amount cannot exceed 10000!", true);
                return;
            }
            String result = userController.increaseCredit(currentUser, amount);
            showMessage(errorLabel, result, !result.contains("successfully"));
            if (result.contains("successfully")) {
                switchScene(LIBRARY_FXML, currentUser);
            }
        } catch (NumberFormatException e) {
            showMessage(errorLabel, "Please enter a valid number!", true);
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
            Stage stage = (Stage) increaseButton.getScene().getWindow();
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