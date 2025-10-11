package model;

import java.util.Date;

public abstract class Video extends Content {
    protected String subtitles;

    public Video(String title, boolean isExclusive, String description, String duration,
                 Date releaseDate, Category category, String fileLink, String cover,
                 String contentOwnerUsername, String subtitles) {
        super(title, isExclusive, description, duration, releaseDate, category, fileLink, cover, contentOwnerUsername);
        this.subtitles = subtitles;
    }

    public String getSubtitles() { return subtitles; }
    public void setSubtitles(String subtitles) { this.subtitles = subtitles; }
}