package view;

import controller.ContentController;
import model.Content;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeController extends AbstractController {
    private static final String CONTENT_PLAYER_FXML = "/fxml/ContentPlayer.fxml";
    private static final String PLACEHOLDER_IMAGE = "/images/placeholder.png";

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Label searchResultLabel;
    @FXML private GridPane contentGrid;

    private ContentController contentController = new ContentController();
    private User currentUser;

    @FXML
    private void initialize() {
        contentGrid.getChildren().clear();
    }

    @Override
    public void setData(Object data) {
        this.currentUser = (User) data;
        loadRecommendedContent();
    }

    private void loadRecommendedContent() {
        contentGrid.getChildren().clear();
        var contents = contentController.getSuggestions(currentUser);
        if (contents.isEmpty()) {
            searchResultLabel.setText("No recommended content available!");
            return;
        }
        searchResultLabel.setText("");
        int row = 0, col = 0;
        for (Content content : contents) {
            VBox contentCard = createContentCard(content);
            contentGrid.add(contentCard, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        contentGrid.getChildren().clear();
        if (query.isEmpty()) {
            searchResultLabel.setText("Please enter a search query!");
            loadRecommendedContent();
            return;
        }
        var contents = contentController.search(query);
        if (contents.isEmpty()) {
            searchResultLabel.setText("No results found for: " + query);
            return;
        }
        searchResultLabel.setText("Found " + contents.size() + " results");
        int row = 0, col = 0;
        for (Content content : contents) {
            VBox contentCard = createContentCard(content);
            contentGrid.add(contentCard, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
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

        card.getChildren().addAll(coverImage, titleLabel, creatorLabel);
        card.setOnMouseClicked(e -> switchScene(CONTENT_PLAYER_FXML, content));
        return card;
    }

    public void switchScene(String fxml, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) searchButton.getScene().getWindow();
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