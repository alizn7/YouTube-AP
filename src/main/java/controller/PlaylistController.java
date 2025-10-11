package controller;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class PlaylistController {
    private DataBase db = DataBase.getInstance();
    private String lastMessage;

    public String getLastMessage() {
        return lastMessage;
    }

    public Playlist createPlaylist(User user, String type) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return null;
        }
        Playlist playlist = new Playlist(type);
        if (type.equals("U")) {
            user.createPlaylist(type);
            user.increaseScore(2);
            lastMessage = "User playlist created";
            return user.getPlaylists().getLast();
        } else if (type.equals("C")) {
            if (user.getChannel() == null) {
                lastMessage = "User must create a channel first!";
                return null;
            }
            user.getChannel().addPlaylist(playlist);
            user.increaseScore(2);
            lastMessage = "Channel playlist created";
            return playlist;
        }
        lastMessage = "Invalid playlist type!";
        return null;
    }

    public String addToPlaylist(User user, int playlistId, int contentId) {
        if (user.isBanned()) {
            return "User is banned!";
        }
        Playlist playlist = findPlaylist(user, playlistId);
        if (playlist == null) {
            return "Playlist not found!";
        }
        Content content = findContentById(contentId);
        if (content == null) {
            return "Content not found!";
        }
        if (playlist.getContents().contains(content)) {
            return "Content already exists in playlist!";
        }
        user.addContentToPlaylist(playlist, content);
        user.increaseScore(1);
        return "Content added to playlist";
    }

    public List<Playlist> showPlaylists(User user) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return new ArrayList<>();
        }
        lastMessage = "Playlists retrieved";
        return user.getPlaylists();
    }

    private Playlist findPlaylist(User user, int playlistId) {
        for (Playlist playlist : user.getPlaylists()) {
            if (playlist.getId() == playlistId) return playlist;
        }
        if (user.getChannel() != null) {
            for (Playlist playlist : user.getChannel().getPlaylists()) {
                if (playlist.getId() == playlistId) return playlist;
            }
        }
        return null;
    }

    private Content findContentById(int contentId) {
        for (Content content : db.getContents()) {
            if (content.getId() == contentId) return content;
        }
        return null;
    }
}