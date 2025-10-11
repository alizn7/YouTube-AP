package model;

import java.util.Date;

public class Comment {
    private String commenterUsername;
    private String text;
    private Date commentDate;

    public Comment(String commenterUsername, String text, Date commentDate) {
        this.commenterUsername = commenterUsername;
        this.text = text;
        this.commentDate = commentDate;
    }

    public String getCommenterUsername() {
        return commenterUsername;
    }

    public void setCommenterUsername(String commenterUsername) {
        this.commenterUsername = commenterUsername;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }


}
