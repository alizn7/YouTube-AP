package controller;

import model.*;

public class ReportController {
    private DataBase db = DataBase.getInstance();
    private String lastMessage;

    public String getLastMessage() {
        return lastMessage;
    }

    public Report report(User user, int contentId, String explanation) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return null;
        }
        Content content = findContentById(contentId);
        if (content == null) {
            lastMessage = "Content not found!";
            return null;
        }
        Report report = new Report(user, contentId, content.getContentOwnerUsername(), explanation);
        db.addReport(report);
        lastMessage = "Report submitted successfully";
        return report;
    }

    private Content findContentById(int contentId) {
        for (Content content : db.getContents()) {
            if (content.getId() == contentId) return content;
        }
        return null;
    }
}