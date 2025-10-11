package controller;

import model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChannelController {
    private DataBase db = DataBase.getInstance();
    private String lastMessage;

    public String getLastMessage() {
        return lastMessage;
    }

    public Channel createChannel(User user, String channelName, String description) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return null;
        }
        if (user.getChannel() != null) {
            lastMessage = "User already has a channel!";
            return null;
        }
        Channel channel = new Channel(channelName, user.getUsername());
        channel.setDescription(description);
        channel.setCover("cover");
        user.setChannel(channel);
        user.increaseScore(10);
        lastMessage = "Channel created successfully";
        return channel;
    }

    public Channel viewChannel(User user) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return null;
        }
        if (user.getChannel() == null) {
            lastMessage = "User has no channel!";
            return null;
        }
        lastMessage = "Channel retrieved successfully";
        return user.getChannel();
    }

    public Content publishPodcast(User user, String isExclusive, String title, String description, String duration,
                                  String category, String contentLink, String cover, String podcaster) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return null;
        }
        if (user.getChannel() == null) {
            lastMessage = "User must create a channel first!";
            return null;
        }
        boolean exclusive = isExclusive.equals("Y");
        Category cat = Category.valueOf(category.toUpperCase());
        Content content = new Podcast(title, exclusive, description, duration, new Date(), cat, contentLink, cover, user.getUsername(), podcaster);
        user.getChannel().addContent(content);
        user.increaseScore(5);
        lastMessage = "Podcast published successfully. Content ID: " + content.getId();
        return content;
    }

    public Content publishNormalVideo(User user, String isExclusive, String title, String description, String duration,
                                      String category, String contentLink, String cover, String subtitle, String quality, String format) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return null;
        }
        if (user.getChannel() == null) {
            lastMessage = "User must create a channel first!";
            return null;
        }
        boolean exclusive = isExclusive.equals("Y");
        Category cat = Category.valueOf(category.toUpperCase());
        VideoQuality q = VideoQuality.valueOf("Q" + quality.replace("p", ""));
        VideoFormat f = VideoFormat.valueOf(format.toUpperCase());
        Content content = new NormalVideo(title, exclusive, description, duration, new Date(), cat, contentLink, cover, user.getUsername(), subtitle, q, f);
        user.getChannel().addContent(content);
        user.increaseScore(5);
        lastMessage = "Normal video published successfully. Content ID: " + content.getId();
        return content;
    }

    public Content publishShortVideo(User user, String isExclusive, String title, String description, String duration,
                                     String category, String contentLink, String cover, String subtitle, String audioTitle) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return null;
        }
        if (user.getChannel() == null) {
            lastMessage = "User must create a channel first!";
            return null;
        }
        boolean exclusive = isExclusive.equals("Y");
        Category cat = Category.valueOf(category.toUpperCase());
        Content content = new ShortVideo(title, exclusive, description, duration, new Date(), cat, contentLink, cover, user.getUsername(), subtitle, audioTitle);
        user.getChannel().addContent(content);
        user.increaseScore(5);
        lastMessage = "Short video published successfully. Content ID: " + content.getId();
        return content;
    }

    public Content publishLiveStream(User user, String isExclusive, String title, String description, String duration,
                                     String category, String contentLink, String cover, String subtitle, String scheduledDate) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return null;
        }
        if (user.getChannel() == null) {
            lastMessage = "User must create a channel first!";
            return null;
        }
        boolean exclusive = isExclusive.equals("Y");
        Category cat = Category.valueOf(category.toUpperCase());
        Date schedDate = Utils.parseDate(scheduledDate);
        Content content = new LiveStream(title, exclusive, description, duration, new Date(), cat, contentLink, cover, user.getUsername(), subtitle, schedDate);
        user.getChannel().addContent(content);
        user.increaseScore(10);
        lastMessage = "Live stream published successfully. Content ID: " + content.getId();
        return content;
    }

    public List<Content> showChannelContent(User user) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return new ArrayList<>();
        }
        if (user.getChannel() == null) {
            lastMessage = "User has no channel!";
            return new ArrayList<>();
        }
        lastMessage = "Channel content retrieved";
        return user.getChannel().getPlaylists().get(0).getContents();
    }

    public List<User> showChannelSubscribers(User user) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return new ArrayList<>();
        }
        if (user.getChannel() == null) {
            lastMessage = "User has no channel!";
            return new ArrayList<>();
        }
        lastMessage = "Channel subscribers retrieved";
        return user.getChannel().getFollowers();
    }

    public String editChannel(User user, String type, String newValue) {
        if (user.isBanned()) return "User is banned!";
        if (user.getChannel() == null) return "User has no channel!";
        if (type.equals("N")) {
            user.getChannel().setName(newValue);
            return "Channel name updated";
        } else if (type.equals("D")) {
            user.getChannel().setDescription(newValue);
            return "Channel description updated";
        } else if (type.equals("C")) {
            user.getChannel().setCover(newValue);
            return "Channel cover updated";
        }
        return "Invalid edit type!";
    }

    public String subscribe(User user, int channelId) {
        if (user.isBanned()) return "User is banned!";
        Channel channel = findChannelById(channelId);
        if (channel == null) return "Channel not found!";
        user.addSubscription(channel);
        for (Account account : db.getAccounts()) {
            if (account instanceof User && account.getUsername().equals(channel.getOwnerName())) {
                ((User) account).increaseScore(3);
            }
        }
        lastMessage = "Subscribed successfully";
        return lastMessage;
    }

    public String unsubscribe(User user, int channelId) {
        if (user.isBanned()) return "User is banned!";
        Channel channel = findChannelById(channelId);
        if (channel == null) return "Channel not found!";
        user.removeSubscription(channel);
        for (Account account : db.getAccounts()) {
            if (account instanceof User && account.getUsername().equals(channel.getOwnerName())) {
                ((User) account).decreaseScore(3);
            }
        }
        lastMessage = "Unsubscribed successfully";
        return lastMessage;
    }

    public List<Channel> showSubscriptions(User user) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return new ArrayList<>();
        }
        lastMessage = "Subscriptions retrieved";
        return user.getSubscriptions();
    }

    public List<Channel> showChannels() {
        List<Channel> channels = new ArrayList<>();
        for (Account account : db.getAccounts()) {
            if (account instanceof User && ((User) account).getChannel() != null) {
                channels.add(((User) account).getChannel());
            }
        }
        lastMessage = "All channels retrieved";
        return channels;
    }

    public Channel showChannel(int channelId) {
        Channel channel = findChannelById(channelId);
        if (channel == null) {
            lastMessage = "Channel not found!";
            return null;
        }
        lastMessage = "Channel retrieved";
        return channel;
    }

    private Channel findChannelById(int channelId) {
        for (Account account : db.getAccounts()) {
            if (account instanceof User && ((User) account).getChannel() != null && ((User) account).getChannel().getChannelId() == channelId) {
                return ((User) account).getChannel();
            }
        }
        return null;
    }
}