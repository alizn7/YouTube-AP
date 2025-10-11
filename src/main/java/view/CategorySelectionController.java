package view;

import controller.UserController;
import javafx.scene.media.MediaView;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectionController extends AbstractController {
    @FXML private MediaView mediaView;
    @FXML private ToggleButton musicButton;
    @FXML private ToggleButton gameButton;
    @FXML private ToggleButton podcastButton;
    @FXML private ToggleButton newsButton;
    @FXML private ToggleButton liveButton;
    @FXML private ToggleButton societyButton;
    @FXML private ToggleButton historyButton;
    @FXML private Button continueButton;
    @FXML private Label errorLabel;
    @FXML private Label counterLabel;

    private UserController userController = new UserController();
    private User currentUser;
    private MediaPlayer mediaPlayer;
    private List<ToggleButton> categoryButtons;
    private List<String> selectedCategories;

    @Override
    public void setData(Object data) {
        this.currentUser = (User) data;
        initializeVideo();
        initializeButtons();
    }

    private void initializeVideo() {
        try {
            String videoPath = getClass().getResource("/videos/category_background.mp4").toExternalForm();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setMute(true);
        } catch (Exception e) {
            errorLabel.setText("Error loading video: " + e.getMessage());
        }
    }

    private void initializeButtons() {
        categoryButtons = List.of(musicButton, gameButton, podcastButton, newsButton, liveButton, societyButton, historyButton);
        selectedCategories = new ArrayList<>();

        for (ToggleButton button : categoryButtons) {
            button.setOnAction(event -> handleCategorySelection(button));
        }
        updateCounter();
    }

    private void handleCategorySelection(ToggleButton button) {
        String category = button.getText().toUpperCase();
        if (button.isSelected()) {
            if (selectedCategories.size() >= 4) {
                button.setSelected(false);
                errorLabel.setText("You can select up to 4 categories!");
                return;
            }
            selectedCategories.add(category);

            button.setStyle("-fx-background-color: #7e1efc; -fx-text-fill: white; -fx-font-size: 14px;" +
                    "-fx-background-radius: 10; -fx-border-color: #7e1efc; -fx-border-width: 1.5;" +
                    "-fx-border-radius: 10;");
        } else {
            selectedCategories.remove(category);

            button.setStyle("-fx-background-color: #212529; -fx-text-fill: #ffffff; -fx-font-size: 14px;" +
                    "-fx-background-radius: 10; -fx-border-color: #007BFF; -fx-border-width: 1.5;" +
                    "-fx-border-radius: 10;");
        }
        errorLabel.setText("");
        updateCounter();
    }

    private void updateCounter() {
        counterLabel.setText("Selected: " + selectedCategories.size() + "/4");
    }

    @FXML
    private void handleContinue() {
        if (selectedCategories.isEmpty()) {
            errorLabel.setText("Please select at least one category!");
            return;
        }

        String categoriesString = String.join(",", selectedCategories);
        String result = userController.setFavouriteCategories(currentUser, categoriesString);
        if (!result.equals("Favorite categories updated")) {
            errorLabel.setText(result);
            return;
        }

        stopVideo();
        switchScene("/fxml/Home.fxml", currentUser);
    }

    private void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void switchScene(String fxml, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(scene);

            if (data != null && loader.getController() instanceof BaseController) {
                ((BaseController) loader.getController()).setData(data);
            }

            stage.show();
        } catch (Exception e) {
           System.out.println("Error switching scene: " + e.getMessage());
        }
    }
}