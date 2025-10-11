package controller;

import model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AdminController {
    private DataBase db = DataBase.getInstance();
    private String lastMessage;

    public String getLastMessage() {
        return lastMessage;
    }

    public Admin login(String username, String password) {
        for (Account account : db.getAccounts()) {
            if (account.getUsername().equals(username) && account.getPassword().equals(password) && account instanceof Admin) {
                lastMessage = "Admin logged in successfully";
                return (Admin) account;
            }
        }
        lastMessage = "Invalid admin credentials";
        return null;
    }

    public Admin getAccountInfo(Admin admin) {
        if (admin == null) {
            lastMessage = "No admin provided!";
            return null;
        }
        lastMessage = "Admin info retrieved";
        return admin;
    }

    public List<Channel> viewPopularChannels() {
        List<Channel> channels = new ArrayList<>();
        for (Account account : db.getAccounts()) {
            if (account instanceof User && ((User) account).getChannel() != null) {
                channels.add(((User) account).getChannel());
            }
        }
        channels.sort(Comparator.comparingInt(Channel::getFollowersCount).reversed());
        int limit = Math.min(5, channels.size());
        lastMessage = "Top " + limit + " popular channels retrieved";
        return channels.subList(0, limit);
    }

    public List<Content> viewPopularContents() {
        List<Content> contents = new ArrayList<>(db.getContents());
        contents.sort(Comparator.comparingInt(Content::getLikes).reversed());
        int limit = Math.min(5, contents.size());
        lastMessage = "Top " + limit + " popular contents retrieved";
        return contents.subList(0, limit);
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        for (Account account : db.getAccounts()) {
            if (account instanceof User) users.add((User) account);
        }
        lastMessage = "All users retrieved";
        return users;
    }

    public List<Content> getContents() {
        lastMessage = "All contents retrieved";
        return db.getContents();
    }

    public List<Report> getReports() {
        lastMessage = "All reports retrieved";
        return db.getReports();
    }

    public String manageReport(int reportId, String action) {
        Report report = findReportById(reportId);
        if (report == null) {
            return "Report not found!";
        }
        if (action.equals("C")) {
            db.removeContent(report.getReportedContentId());
            db.banUser(report.getReportedUserUsername());
            db.getReports().remove(report);
            return "Report approved, content removed, user banned";
        } else if (action.equals("R")) {
            db.getReports().remove(report);
            return "Report rejected";
        }
        return "Invalid action!";
    }

    public String unbanUser(String username) {
        db.unbanUser(username);
        return "User unbanned successfully";
    }

    public String deleteComment(int contentId, String commenterUsername, String commentText) {
        Content content = findContentById(contentId);
        if (content == null) return "Content not found!";
        boolean removed = content.getComments().removeIf(comment ->
                comment.getCommenterUsername().equals(commenterUsername) && comment.getText().equals(commentText));
        if (removed) return "Comment deleted successfully";
        return "Comment not found!";
    }

    private Report findReportById(int reportId) {
        for (Report report : db.getReports()) {
            if (report.getId() == reportId) return report;
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