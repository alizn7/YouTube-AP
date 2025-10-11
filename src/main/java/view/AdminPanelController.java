package view;

import controller.AdminController;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Account;
import model.Content;
import model.Report;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.stream.Collectors;

public class AdminPanelController extends AbstractController {
    private static final String LOGIN_FXML = "/fxml/Login.fxml";
    private static final String REPORT_MANAGEMENT_FXML = "/fxml/ReportManagement.fxml";

    @FXML private BarChart<String, Number> statsChart;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, Boolean> isBannedColumn;
    @FXML private TableView<Content> contentTable;
    @FXML private TableColumn<Content, Integer> contentIdColumn;
    @FXML private TableColumn<Content, String> contentTitleColumn;
    @FXML private TableColumn<Content, String> creatorColumn;
    @FXML private TableView<Report> reportTable;
    @FXML private TableColumn<Report, Integer> reportIdColumn;
    @FXML private TableColumn<Report, String> reportedUserColumn;
    @FXML private TableColumn<Report, String> reasonColumn;
    @FXML private Button manageReportsButton;
    @FXML private Label errorLabel;

    private AdminController adminController = new AdminController();
    private Account currentAccount;

    @Override
    public void setData(Object data) {
        if (!(data instanceof Account && data instanceof model.Admin)) {
            showMessage(errorLabel, "Access denied: Admin privileges required!", true);
            switchScene(LOGIN_FXML, null);
            return;
        }
        this.currentAccount = (Account) data;
        loadStats();
        loadUsersAndContents();
        loadReports();
    }

    private void loadStats() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Users", adminController.getUsers().size()));
        series.getData().add(new XYChart.Data<>("Contents", adminController.getContents().size()));
        series.getData().add(new XYChart.Data<>("Reports", adminController.getReports().size()));
        statsChart.getData().clear();
        statsChart.getData().add(series);
    }

    private void loadUsersAndContents() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        isBannedColumn.setCellValueFactory(new PropertyValueFactory<>("banned"));
        userTable.getItems().setAll(adminController.getUsers().stream()
                .filter(account -> account instanceof User)
                .map(account -> (User) account)
                .collect(Collectors.toList()));

        contentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        contentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        creatorColumn.setCellValueFactory(new PropertyValueFactory<>("creatorUsername"));
        contentTable.getItems().setAll(adminController.getContents());
    }

    private void loadReports() {
        reportIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        reportedUserColumn.setCellValueFactory(new PropertyValueFactory<>("reportedUsername"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reportTable.getItems().setAll(adminController.getReports());
    }

    @FXML
    private void handleManageReports() {
        switchScene(REPORT_MANAGEMENT_FXML, currentAccount);
    }

    public void switchScene(String fxml, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) manageReportsButton.getScene().getWindow();
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