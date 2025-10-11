package view;

import controller.ContentController;
import controller.UserController;
import model.Content;
import model.DataBase;
import model.Report;
import model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ReportManagementController extends AbstractController {
    private static final String ADMIN_PANEL_FXML = "/fxml/AdminPanel.fxml";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";

    @FXML private Button backButton;
    @FXML private VBox reportBox;
    @FXML private Label errorLabel;

    private ContentController contentController = new ContentController();
    private UserController userController = new UserController();
    private User currentUser;

    @Override
    public void setData(Object data) {
        if (data instanceof User) {
            this.currentUser = (User) data;
            loadReports();
        } else {
            showMessage(errorLabel, "Invalid user data or not logged in!", true);
            switchScene(LOGIN_FXML, null, backButton, errorLabel);
        }
    }

    private void loadReports() {
        reportBox.getChildren().clear();
        var reports = DataBase.getInstance().getReports();
        if (reports.isEmpty()) {
            showMessage(errorLabel, "No reports found!", true);
            return;
        }
        for (Report report : reports) {
            HBox reportCard = createReportCard(report);
            reportBox.getChildren().add(reportCard);
        }
    }

    private HBox createReportCard(Report report) {
        HBox card = new HBox(10);
        card.setStyle("-fx-background-color: #2c2f33; -fx-background-radius: 8; -fx-padding: 10;");
        Content content = contentController.getContentById(report.getReportedContentId());
        String contentTitle = content != null ? content.getTitle() : "Content ID: " + report.getReportedContentId() + " (Not Found)";
        Label contentLabel = new Label("Content: " + contentTitle);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        Label reasonLabel = new Label("Reason: " + report.getDescription());
        reasonLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #adb5bd;");
        Button deleteButton = new Button("Delete Content");
        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8;");
        deleteButton.setDisable(content == null);
        deleteButton.setOnAction(e -> handleDelete(report.getReportedContentId()));
        Button banButton = new Button("Ban User");
        banButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 8;");
        banButton.setOnAction(e -> handleBan(report.getReportedUserUsername()));
        Button dismissButton = new Button("Dismiss Report");
        dismissButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 8;");
        dismissButton.setOnAction(e -> handleDismiss(report.getId()));
        card.getChildren().addAll(contentLabel, reasonLabel, deleteButton, banButton, dismissButton);
        return card;
    }

    private void handleDelete(int contentId) {
        Content content = contentController.getContentById(contentId);
        if (content == null) {
            showMessage(errorLabel, "Content not found!", true);
            return;
        }
        DataBase.getInstance().removeContent(contentId);
        showMessage(errorLabel, "Content deleted successfully!", false);
        loadReports();
    }

    private void handleBan(String username) {
        String result = userController.banUser(username);
        showMessage(errorLabel, result, !result.contains("successfully"));
        loadReports();
    }

    private void handleDismiss(int reportId) {
        DataBase.getInstance().removeReport(reportId);
        showMessage(errorLabel, "Report dismissed successfully!", false);
        loadReports();
    }

    @FXML
    private void handleBack() {
        switchScene(ADMIN_PANEL_FXML, currentUser, backButton, errorLabel);
    }
}