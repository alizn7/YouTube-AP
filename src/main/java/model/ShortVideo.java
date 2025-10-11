package model;

import java.util.Date;

public class ShortVideo extends Video {
    private String audioTitle;

    public ShortVideo(String title, boolean isExclusive, String description, String duration,
                      Date releaseDate, Category category, String fileLink, String cover,
                      String contentOwnerUsername, String subtitles, String audioTitle) {
        super(title, isExclusive, description, duration, releaseDate, category, fileLink, cover, contentOwnerUsername, subtitles);
        int seconds = parseDuration(duration);
        if (seconds > 30) throw new IllegalArgumentException("ShortVideo duration must be less than 30 seconds");
        this.audioTitle = audioTitle;
    }

    private int parseDuration(String duration) {
        String[] parts = duration.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
    }

    public String getAudioTitle() { return audioTitle; }
    public void setAudioTitle(String audioTitle) { this.audioTitle = audioTitle; }
}