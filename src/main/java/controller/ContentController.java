package controller;

import model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ContentController {
    private DataBase db = DataBase.getInstance();
    private String lastMessage;

    public String getLastMessage() {
        return lastMessage;
    }

    public String play(User user, int contentId) {
        if (user.isBanned()) return "User is banned!";
        Content content = findContentById(contentId);
        if (content == null) return "Content not found!";
        if (!content.canUserAccess(user)) return "Content is exclusive, upgrade to premium!";
        content.incrementViews();
        for (Account account : db.getAccounts()) {
            if (account instanceof User && account.getUsername().equals(content.getContentOwnerUsername())) {
                ((User) account).increaseScore(1);
            }
        }
        Playlist watchLater = user.getPlaylists().stream().filter(p -> p.getName().equals("Watch Later")).findFirst().orElse(null);
        if (watchLater != null && !watchLater.getContents().contains(content)) {
            user.addContentToPlaylist(watchLater, content);
        }
        return "Content played successfully";
    }

    public String like(User user, int contentId) {
        if (user.isBanned()) return "User is banned!";
        Content content = findContentById(contentId);
        if (content == null) return "Content not found!";
        content.incrementLikes();
        for (Account account : db.getAccounts()) {
            if (account instanceof User && account.getUsername().equals(content.getContentOwnerUsername())) {
                ((User) account).increaseScore(2);
            }
        }
        Playlist liked = user.getPlaylists().stream().filter(p -> p.getName().equals("Liked")).findFirst().orElse(null);
        if (liked != null && !liked.getContents().contains(content)) {
            user.addContentToPlaylist(liked, content);
        }
        return "Content liked successfully";
    }

    public String addComment(User user, int contentId, String comment) {
        if (user.isBanned()) return "User is banned!";
        Content content = findContentById(contentId);
        if (content == null) return "Content not found!";
        content.addComment(new Comment(user.getUsername(), comment, new Date()));
        user.increaseScore(1);
        return "Comment added successfully";
    }

    public List<Content> search(String query) {
        List<Content> results = new ArrayList<>();
        for (Content content : db.getContents()) {
            if (content.getTitle().toLowerCase().contains(query.toLowerCase()) && !results.contains(content)) {
                results.add(content);
            }
        }
        for (Account account : db.getAccounts()) {
            if (account instanceof User && ((User) account).getChannel() != null) {
                Channel channel = ((User) account).getChannel();
                if (channel.getName().toLowerCase().contains(query.toLowerCase())) {
                    for (Content content : channel.getPlaylists().get(0).getContents()) {
                        if (!results.contains(content)) {
                            results.add(content);
                        }
                    }
                }
            }
        }
        lastMessage = "Search completed with " + results.size() + " results";
        return results;
    }

    public List<Content> getSuggestions(User user) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return new ArrayList<>();
        }
        List<Content> suggestions = new ArrayList<>();
        for (Content content : db.getContents()) {
            if (!content.canUserAccess(user)) continue;
            int score = 0;
            if (user.getFavoriteCategories().contains(content.getCategory())) score += 3;
            if (user.getSubscriptions().stream().anyMatch(ch -> ch.getOwnerName().equals(content.getContentOwnerUsername())))
                score += 2;
            if (user.getPlaylists().stream().flatMap(p -> p.getContents().stream()).anyMatch(c -> c.getId() == content.getId() && c.getLikes() > 0))
                score += 1;
            if (score > 0) suggestions.add(content);
        }
        suggestions.sort(Comparator.comparingInt(Content::getLikes).reversed());
        int limit = Math.min(10, suggestions.size());
        lastMessage = "Top " + limit + " suggestions retrieved";
        return suggestions.subList(0, limit);
    }

    public List<Content> sort(String type) {
        List<Content> sorted = new ArrayList<>(db.getContents());
        if (type.equals("L")) {
            sorted.sort(Comparator.comparingInt(Content::getLikes).reversed());
            lastMessage = "Sorted by likes";
        } else if (type.equals("V")) {
            sorted.sort(Comparator.comparingInt(Content::getViews).reversed());
            lastMessage = "Sorted by views";
        } else {
            lastMessage = "Invalid sort type, returning unsorted";
        }
        return sorted;
    }

    public List<Content> filter(String type, String filterBy) {
        List<Content> filtered = new ArrayList<>();
        if (type.equals("V")) {
            for (Content content : db.getContents()) {
                if (content instanceof Video) filtered.add(content);
            }
            lastMessage = "Filtered by video";
        } else if (type.equals("P")) {
            for (Content content : db.getContents()) {
                if (content instanceof Podcast) filtered.add(content);
            }
            lastMessage = "Filtered by podcast";
        } else if (type.equals("C")) {
            Category category = null;
            for (Category c : Category.values()) {
                if (c.name().equalsIgnoreCase(filterBy)) {
                    category = c;
                    break;
                }
            }
            if (category != null) {
                for (Content content : db.getContents()) {
                    if (content.getCategory() == category) filtered.add(content);
                }
                lastMessage = "Filtered by category: " + filterBy;
            } else {
                lastMessage = "Invalid category: " + filterBy;
            }
        } else if (type.equals("D")) {
            Date date = Utils.parseDate(filterBy);
            for (Content content : db.getContents()) {
                if (content.getReleaseDate().equals(date)) filtered.add(content);
            }
            lastMessage = "Filtered by date: " + filterBy;
        } else if (type.equals("DR")) { // Date Range
            String[] dates = filterBy.split(",");
            if (dates.length != 2) {
                lastMessage = "Invalid date range format!";
                return filtered;
            }
            Date startDate = Utils.parseDate(dates[0]);
            Date endDate = Utils.parseDate(dates[1]);
            for (Content content : db.getContents()) {
                if (!content.getReleaseDate().before(startDate) && !content.getReleaseDate().after(endDate)) {
                    filtered.add(content);
                }
            }
            lastMessage = "Filtered by date range: " + filterBy;
        } else {
            lastMessage = "Invalid filter type";
        }
        return filtered;
    }

    private Content findContentById(int contentId) {
        for (Content content : db.getContents()) {
            if (content.getId() == contentId) return content;
        }
        return null;
    }

    public Content getContentById(int reportedContentId) {
        return findContentById(reportedContentId);
    }

    public String addToPlaylist(User user, int playlistId, int contentId) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return lastMessage;
        }
        if (user.getChannel() == null) {
            lastMessage = "User has no channel!";
            return lastMessage;
        }
        Playlist playlist = user.getChannel().getPlaylists().stream()
                .filter(p -> p.getId() == playlistId)
                .findFirst()
                .orElse(null);
        if (playlist == null) {
            lastMessage = "Playlist not found!";
            return lastMessage;
        }
        Content content = findContentById(contentId);
        if (content == null) {
            lastMessage = "Content not found!";
            return lastMessage;
        }
        if (playlist.getContents().contains(content)) {
            lastMessage = "Content already in playlist!";
            return lastMessage;
        }
        playlist.getContents().add(content);
        lastMessage = "Content added to playlist successfully";
        return lastMessage;
    }
}