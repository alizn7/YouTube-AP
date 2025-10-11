package model;

import java.util.Date;

public class NormalVideo extends Video {
    private VideoQuality quality;
    private VideoFormat format;

    public NormalVideo(String title, boolean isExclusive, String description, String duration,
                       Date releaseDate, Category category, String fileLink, String cover,
                       String contentOwnerUsername, String subtitles, VideoQuality quality, VideoFormat format) {
        super(title, isExclusive, description, duration, releaseDate, category, fileLink, cover, contentOwnerUsername, subtitles);
        this.quality = quality;
        this.format = format;
    }

    public VideoQuality getQuality() {
        return quality;
    }

    public void setQuality(VideoQuality quality) {
        this.quality = quality;
    }

    public VideoFormat getFormat() {
        return format;
    }

    public void setFormat(VideoFormat format) {
        this.format = format;
    }
}