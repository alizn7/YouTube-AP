module com.example.finalyoutubeproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;


    opens view to javafx.fxml;
    exports view;
}