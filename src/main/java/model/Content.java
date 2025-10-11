package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class Content {
    protected int id;
    protected String title;
    protected boolean isExclusive;
    protected String description;
    protected String duration;
    protected int views;
    protected int likes;
    protected Date releaseDate;
    protected Category category;
    protected String fileLink;
    protected String cover;
    protected String contentOwnerUsername;
    protected List<Comment> comments;

    public Content(String title, boolean isExclusive, String description, String duration,
                   Date releaseDate, Category category, String fileLink, String cover, String contentOwnerUsername) {
        this.id = generateUniqueId();
        this.title = title;
        this.isExclusive = isExclusive;
        this.description = description;
        this.duration = duration;
        this.views = 0;
        this.likes = 0;
        this.releaseDate = releaseDate;
        this.category = category;
        this.fileLink = fileLink;
        this.cover = cover;
        this.contentOwnerUsername = contentOwnerUsername;
        this.comments = new ArrayList<>();
        DataBase.getInstance().addContent(this);
    }

    private int generateUniqueId() {
        return Math.abs(UUID.randomUUID().hashCode());
    }

    public boolean canUserAccess(User user) {
        return !isExclusive || (user instanceof PremiumUser);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isExclusive() {
        return isExclusive;
    }

    public void setExclusive(boolean isExclusive) {
        this.isExclusive = isExclusive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void incrementViews() {
        this.views++;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void incrementLikes() {
        this.likes++;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getContentOwnerUsername() {
        return contentOwnerUsername;
    }

    public void setContentOwnerUsername(String contentOwnerUsername) {
        this.contentOwnerUsername = contentOwnerUsername;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public int parseDurationToSeconds() {
        try {
            String[] parts = duration.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            if (seconds >= 60) throw new IllegalArgumentException("Seconds must be less than 60");
            return minutes * 60 + seconds;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid duration format: " + duration);
        }
    }

}