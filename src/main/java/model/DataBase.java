package model;

import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private static DataBase instance;
    private List<Account> accounts;
    private List<Content> contents;
    private List<Report> reports;

    private DataBase() {
        this.accounts = new ArrayList<>();
        this.contents = new ArrayList<>();
        this.reports = new ArrayList<>();
        Admin admin = Admin.getInstance("Admin", "123", "Ali", "khafan", "admin@example.com", "09120000000", null);
        this.accounts.add(admin);
    }

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public void removeContent(int contentId) {
        contents.removeIf(content -> content.getId() == contentId);
    }

    public void removeReport(int reportId) {
        reports.removeIf(report -> report.getId() == reportId);
    }

    public void banUser(String username) {
        for (Account account : accounts) {
            if (account instanceof User && account.getUsername().equals(username)) {
                ((User) account).setBanned(true);
                break;
            }
        }
    }

    public void unbanUser(String username) {
        for (Account account : accounts) {
            if (account instanceof User && account.getUsername().equals(username)) {
                ((User) account).setBanned(false);
                break;
            }
        }
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public List<Content> getContents() {
        return contents;
    }

    public void addContent(Content content) {
        this.contents.add(content);
    }

    public List<Report> getReports() {
        return reports;
    }

    public void addReport(Report report) {
        this.reports.add(report);
    }
}