package view;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class AbstractController implements BaseController, Initializable {
    private static final String LOGIN_FXML = "/fxml/Login.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    protected void showMessage(Label errorLabel, String message, boolean isError) {
        if (errorLabel == null) return;
        errorLabel.setText(message);
        errorLabel.setStyle(isError ? "-fx-text-fill: #ff6b6b;" : "-fx-text-fill: #28a745;");
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> errorLabel.setText(""));
        pause.play();
    }

    protected void switchScene(String fxml, Object data, Control control, Label errorLabel) {
        if (control == null) {
            showMessage(errorLabel, "Error: Control is null!", true);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) control.getScene().getWindow();
            stage.setScene(scene);
            if (data != null && loader.getController() instanceof BaseController) {
                ((BaseController) loader.getController()).setData(data);
            }
            stage.show();
        } catch (IOException e) {
            showMessage(errorLabel, "Failed to load page: Resource not found (" + fxml + ")", true);
            switchSceneToLogin(control, errorLabel);
        } catch (Exception e) {
            showMessage(errorLabel, "Error loading page: " + e.getMessage(), true);
            switchSceneToLogin(control, errorLabel);
        }
    }

    protected void switchScene(String fxml, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(scene);
                if (data != null && loader.getController() instanceof BaseController) {
                    ((BaseController) loader.getController()).setData(data);
                }
                stage.show();
            }
        } catch (IOException e) {
            showErrorWithoutControl("Failed to load page: Resource not found (" + fxml + ")");
            switchSceneToLogin(null, null);
        } catch (Exception e) {
            showErrorWithoutControl("Error loading page: " + e.getMessage());
            switchSceneToLogin(null, null);
        }
    }

    private Stage getCurrentStage() {
        return null;
    }

    private void switchSceneToLogin(Control control, Label errorLabel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_FXML));
            Scene scene = new Scene(loader.load());
            Stage stage = control != null ? (Stage) control.getScene().getWindow() : new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showMessage(errorLabel, "Critical error: Cannot load login page!", true);
        }
    }

    private void showErrorWithoutControl(String message) {
        System.err.println(message);
    }
}