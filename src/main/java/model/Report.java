package model;

public class Report {
    private int id;
    private User reporter;
    private int reportedContentId;
    private String reportedUserUsername;
    private String description;

    public Report(User reporter, int reportedContentId,
                  String reportedUserUsername, String description) {
        this.id = generateUniqueId();
        this.reporter = reporter;
        this.reportedContentId = reportedContentId;
        this.reportedUserUsername = reportedUserUsername;
        this.description = description;
    }

    private int generateUniqueId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }


    public int getId() {
        return id;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public int getReportedContentId() {
        return reportedContentId;
    }

    public void setReportedContentId(int reportedContentId) {
        this.reportedContentId = reportedContentId;
    }

    public String getReportedUserUsername() {
        return reportedUserUsername;
    }

    public void setReportedUserUsername(String reportedUserUsername) {
        this.reportedUserUsername = reportedUserUsername;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
