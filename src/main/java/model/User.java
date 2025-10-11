package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class User extends Account {
    protected double balance;
    protected List<Playlist> playlists;
    protected Channel channel;
    protected List<Channel> subscriptions;
    protected List<Category> favoriteCategories;
    protected boolean isBanned;
    protected int score;

    public User(String username, String password, String firstName, String lastName, String email, String phoneNumber, String profilePicture) {
        super(username, password, firstName, lastName, email, phoneNumber, profilePicture);
        this.balance = 0.0;
        this.playlists = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
        this.favoriteCategories = new ArrayList<>();
        this.channel = null;
        this.playlists.add(new Playlist("Liked"));
        this.playlists.add(new Playlist("Watch Later"));
        this.isBanned = false;
        this.score = 0;
    }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public void increaseBalance(double amount) { this.balance += amount; }
    public List<Playlist> getPlaylists() { return playlists; }
    public void setPlaylists(List<Playlist> playlists) { this.playlists = playlists; }
    public Channel getChannel() { return channel; }
    public void setChannel(Channel channel) { this.channel = channel; }
    public List<Channel> getSubscriptions() { return subscriptions; }
    public void setSubscriptions(List<Channel> subscriptions) { this.subscriptions = subscriptions; }
    public void addSubscription(Channel channel) { this.subscriptions.add(channel); channel.addFollower(this); }
    public void removeSubscription(Channel channel) {
        this.subscriptions.remove(channel);
        channel.removeFollower(this);
    }
    public List<Category> getFavoriteCategories() { return favoriteCategories; }
    public void setFavoriteCategories(List<Category> favoriteCategories) {
        if (favoriteCategories.size() > 4) throw new IllegalArgumentException("Max 4 favorite categories allowed");
        this.favoriteCategories = favoriteCategories;
    }
    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean isBanned) { this.isBanned = isBanned; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public void increaseScore(int points) { this.score += points; }
    public void decreaseScore(int points) {
        this.score = Math.max(0, this.score - points);
    }

    public abstract void createPlaylist(String name);
    public abstract void addContentToPlaylist(Playlist playlist, Content content);
}