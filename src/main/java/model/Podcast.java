package model;

import java.util.Date;

public class Podcast extends Content {
    private String podcaster;

    public Podcast(String title, boolean isExclusive, String description, String duration,
                   Date releaseDate, Category category, String fileLink, String cover,
                   String contentOwnerUsername, String podcaster) {
        super(title, isExclusive, description, duration, releaseDate, category, fileLink, cover, contentOwnerUsername);
        this.podcaster = podcaster;
    }

    public String getPodcaster() { return podcaster; }
    public void setPodcaster(String podcaster) { this.podcaster = podcaster; }
}