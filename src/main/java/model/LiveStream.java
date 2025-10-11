package model;

import java.util.Date;

public class LiveStream extends Video {
    private int onlineViewers;
    private Date scheduledDate;

    public LiveStream(String title, boolean isExclusive, String description, String duration,
                      Date releaseDate, Category category, String fileLink, String cover,
                      String contentOwnerUsername, String subtitles, Date scheduledDate) {
        super(title, isExclusive, description, duration, releaseDate, category, fileLink, cover, contentOwnerUsername, subtitles);
        this.onlineViewers = 0;
        this.scheduledDate = scheduledDate;
    }

    public int getOnlineViewers() {
        return onlineViewers;
    }

    public void setOnlineViewers(int onlineViewers) {
        this.onlineViewers = onlineViewers;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}