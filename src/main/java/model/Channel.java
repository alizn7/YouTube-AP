package model;

import java.util.ArrayList;
import java.util.List;

public class Channel {
    private int channelId;
    private String name;
    private String description;
    private String cover;
    private String ownerName;
    private List<Playlist> playlists;
    private List<User> followers;

    public Channel(String name, String ownerName) {
        this.channelId = generateUniqueId();
        this.name = name;
        this.ownerName = ownerName;
        this.playlists = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.playlists.add(new Playlist("allContents"));
    }

    private static int counter = 0;
    private int generateUniqueId() {
        return (int) (Math.random() * 1000) + (counter++);
    }

    public void addContent(Content content) {
        Playlist allContents = playlists.get(0);
        allContents.addContent(content);
    }

    public int getChannelId() { return channelId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public List<Playlist> getPlaylists() { return playlists; }
    public void setPlaylists(List<Playlist> playlists) { this.playlists = playlists; }
    public void addPlaylist(Playlist playlist) { this.playlists.add(playlist); }
    public List<User> getFollowers() { return followers; }
    public void setFollowers(List<User> followers) { this.followers = followers; }
    public void addFollower(User user) { this.followers.add(user); }
    public int getFollowersCount() { return followers.size(); }

    public void removeFollower(User user) {
        this.followers.remove(user);
    }
}