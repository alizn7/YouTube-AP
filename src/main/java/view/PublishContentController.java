package view;

import controller.ChannelController;
import controller.ContentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import model.Category;
import model.Content;
import model.Playlist;
import model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PublishContentController extends AbstractController {
    private static final String CHANNEL_FXML = "/fxml/Channel.fxml";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> videoTypeCombo;
    @FXML private ComboBox<String> playlistCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private CheckBox exclusiveCheck;
    @FXML private TextField durationField;
    @FXML private Button uploadCoverButton;
    @FXML private Label coverLabel;
    @FXML private Button uploadButton;
    @FXML private Label fileLabel;
    @FXML private Button publishButton;
    @FXML private Button backButton;
    @FXML private Label errorLabel;
    @FXML private HBox normalVideoFields;
    @FXML private HBox shortVideoFields;
    @FXML private HBox liveStreamFields;
    @FXML private TextField subtitleField;
    @FXML private ComboBox<String> qualityCombo;
    @FXML private ComboBox<String> formatCombo;
    @FXML private TextField subtitleFieldShort;
    @FXML private TextField audioTitleField;
    @FXML private TextField subtitleFieldLive;
    @FXML private TextField scheduledDateField;

    private User currentUser;
    private File contentFile;
    private File coverFile;
    private ChannelController channelController = new ChannelController();
    private ContentController contentController = new ContentController();

    @Override
    public void setData(Object data) {
        if (data instanceof User) {
            this.currentUser = (User) data;
            initializeCombos();
        } else {
            showMessage(errorLabel, "Invalid user data or not logged in!", true);
            switchScene(LOGIN_FXML, null);
        }
    }

    private void initializeCombos() {
        typeCombo.setItems(FXCollections.observableArrayList("Video", "Podcast"));
        videoTypeCombo.setItems(FXCollections.observableArrayList("Normal", "Short", "Live"));
        categoryCombo.setItems(FXCollections.observableArrayList("Game", "Podcast", "News", "Live", "Society", "History", "Music"));
        qualityCombo.setItems(FXCollections.observableArrayList("480p", "720p", "1080p"));
        formatCombo.setItems(FXCollections.observableArrayList("MP4", "AVI"));
        loadPlaylists();

        typeCombo.setOnAction(e -> {
            boolean isVideo = "Video".equals(typeCombo.getValue());
            videoTypeCombo.setVisible(isVideo);
            videoTypeCombo.setManaged(isVideo);
            resetVideoFields();
        });

        videoTypeCombo.setOnAction(e -> {
            String videoType = videoTypeCombo.getValue();
            normalVideoFields.setVisible("Normal".equals(videoType));
            normalVideoFields.setManaged("Normal".equals(videoType));
            shortVideoFields.setVisible("Short".equals(videoType));
            shortVideoFields.setManaged("Short".equals(videoType));
            liveStreamFields.setVisible("Live".equals(videoType));
            liveStreamFields.setManaged("Live".equals(videoType));
        });
    }

    private void loadPlaylists() {
        if (currentUser.getChannel() != null && !currentUser.getChannel().getPlaylists().isEmpty()) {
            playlistCombo.setItems(FXCollections.observableArrayList(
                    currentUser.getChannel().getPlaylists().stream()
                            .map(Playlist::getName)
                            .toList()
            ));
        } else {
            playlistCombo.setDisable(true);
            showMessage(errorLabel, "No playlists available. Please create one first.", true);
        }
    }

    private void resetVideoFields() {
        normalVideoFields.setVisible(false);
        normalVideoFields.setManaged(false);
        shortVideoFields.setVisible(false);
        shortVideoFields.setManaged(false);
        liveStreamFields.setVisible(false);
        liveStreamFields.setManaged(false);
        subtitleField.clear();
        qualityCombo.setValue(null);
        formatCombo.setValue(null);
        subtitleFieldShort.clear();
        audioTitleField.clear();
        subtitleFieldLive.clear();
        scheduledDateField.clear();
    }

    @FXML
    private void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Content File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Media Files", "*.mp4", "*.mp3")
        );
        contentFile = fileChooser.showOpenDialog(null);
        if (contentFile != null && (contentFile.getName().endsWith(".mp4") || contentFile.getName().endsWith(".mp3"))) {
            fileLabel.setText(contentFile.getName());
        } else {
            showMessage(errorLabel, "Please select a valid .mp4 or .mp3 file!", true);
            contentFile = null;
        }
    }

    @FXML
    private void handleUploadCover() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Cover Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        coverFile = fileChooser.showOpenDialog(null);
        if (coverFile != null) {
            coverLabel.setText(coverFile.getName());
        } else {
            showMessage(errorLabel, "No cover selected!", true);
        }
    }

    @FXML
    private void handlePublish() {
        if (currentUser == null || currentUser.getChannel() == null) {
            showMessage(errorLabel, "User not logged in or no channel found!", true);
            switchScene(LOGIN_FXML, null);
            return;
        }

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = typeCombo.getValue();
        String videoType = videoTypeCombo.getValue();
        String playlistName = playlistCombo.getValue();
        String category = categoryCombo.getValue();
        String duration = durationField.getText().trim();
        String isExclusive = String.valueOf(exclusiveCheck.isSelected());

        if (title.isEmpty() || title.length() > 100) {
            showMessage(errorLabel, "Title must be between 1 and 100 characters!", true);
            return;
        }
        if (description.isEmpty() || description.length() > 500) {
            showMessage(errorLabel, "Description must be between 1 and 500 characters!", true);
            return;
        }
        if (type == null) {
            showMessage(errorLabel, "Please select a content type!", true);
            return;
        }
        if (contentFile == null || !contentFile.exists()) {
            showMessage(errorLabel, "Please upload a valid media file!", true);
            return;
        }
        if (category == null) {
            showMessage(errorLabel, "Please select a category!", true);
            return;
        }
        if (duration.isEmpty() || !duration.matches("^\\d{1,2}:\\d{2}$")) {
            showMessage(errorLabel, "Duration must be in mm:ss format!", true);
            return;
        }
        try {
            String[] parts = duration.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            if (minutes > 60 || seconds > 59) {
                showMessage(errorLabel, "Duration must be realistic (max 60 minutes)!", true);
                return;
            }
        } catch (NumberFormatException e) {
            showMessage(errorLabel, "Invalid duration format!", true);
            return;
        }
        if (playlistName == null) {
            showMessage(errorLabel, "Please select a playlist!", true);
            return;
        }

        Playlist targetPlaylist = currentUser.getChannel().getPlaylists().stream()
                .filter(p -> p.getName().equals(playlistName))
                .findFirst()
                .orElse(null);
        if (targetPlaylist == null) {
            showMessage(errorLabel, "Selected playlist not found!", true);
            return;
        }

        String cover = coverFile != null ? coverFile.toURI().toString() : "default_cover.png";
        Content content = null;
        String result = "";
        try {
            if ("Video".equals(type)) {
                if (videoType == null) {
                    showMessage(errorLabel, "Please select a video type!", true);
                    return;
                }
                String subtitle = "";
                if ("Normal".equals(videoType)) {
                    subtitle = subtitleField.getText().trim();
                    String quality = qualityCombo.getValue();
                    String format = formatCombo.getValue();
                    if (quality == null || format == null) {
                        showMessage(errorLabel, "Please select quality and format!", true);
                        return;
                    }
                    content = channelController.publishNormalVideo(currentUser, isExclusive, title, description, duration,
                            String.valueOf(Category.valueOf(category.toUpperCase())), contentFile.toURI().toString(), cover, subtitle, quality, format);
                    result = channelController.getLastMessage();
                } else if ("Short".equals(videoType)) {
                    subtitle = subtitleFieldShort.getText().trim();
                    String audioTitle = audioTitleField.getText().trim();
                    if (audioTitle.isEmpty()) {
                        showMessage(errorLabel, "Please enter an audio title!", true);
                        return;
                    }
                    content = channelController.publishShortVideo(currentUser, isExclusive, title, description, duration,
                            String.valueOf(Category.valueOf(category.toUpperCase())), contentFile.toURI().toString(), cover, subtitle, audioTitle);
                    result = channelController.getLastMessage();
                } else if ("Live".equals(videoType)) {
                    subtitle = subtitleFieldLive.getText().trim();
                    String scheduledDate = scheduledDateField.getText().trim();
                    if (!scheduledDate.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                        showMessage(errorLabel, "Scheduled date must be in yyyy-MM-dd format!", true);
                        return;
                    }
                    try {
                        LocalDate date = LocalDate.parse(scheduledDate, DateTimeFormatter.ISO_LOCAL_DATE);
                        if (date.isBefore(LocalDate.now())) {
                            showMessage(errorLabel, "Scheduled date must be in the future!", true);
                            return;
                        }
                    } catch (DateTimeParseException e) {
                        showMessage(errorLabel, "Invalid date format!", true);
                        return;
                    }
                    content = channelController.publishLiveStream(currentUser, isExclusive, title, description, duration,
                            String.valueOf(Category.valueOf(category.toUpperCase())), contentFile.toURI().toString(), cover, subtitle, scheduledDate);
                    result = channelController.getLastMessage();
                }
            } else if ("Podcast".equals(type)) {
                String podcaster = currentUser.getUsername();
                content = channelController.publishPodcast(currentUser, isExclusive, title, description, duration,
                        String.valueOf(Category.valueOf(category.toUpperCase())), contentFile.toURI().toString(), cover, podcaster);
                result = channelController.getLastMessage();
            }
        } catch (IllegalArgumentException e) {
            showMessage(errorLabel, "Invalid category: " + category, true);
            return;
        }

        showMessage(errorLabel, result, content == null);
        if (content != null) {
            String addResult = contentController.addToPlaylist(currentUser, targetPlaylist.getId(), content.getId());
            showMessage(errorLabel, addResult, !addResult.contains("successfully"));
            if (addResult.contains("successfully")) {
                clearFields();
                switchScene(CHANNEL_FXML, currentUser);
            }
        }
    }

    @FXML
    private void handleBack() {
        switchScene(CHANNEL_FXML, currentUser);
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        typeCombo.setValue(null);
        videoTypeCombo.setValue(null);
        playlistCombo.setValue(null);
        categoryCombo.setValue(null);
        exclusiveCheck.setSelected(false);
        durationField.clear();
        fileLabel.setText("No file selected");
        coverLabel.setText("No cover selected");
        subtitleField.clear();
        qualityCombo.setValue(null);
        formatCombo.setValue(null);
        subtitleFieldShort.clear();
        audioTitleField.clear();
        subtitleFieldLive.clear();
        scheduledDateField.clear();
        contentFile = null;
        coverFile = null;
        resetVideoFields();
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