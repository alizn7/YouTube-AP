package model;

import java.util.Date;

public class PremiumUser extends User {
    private Date subscriptionEndDate;

    public PremiumUser(String username, String password, String firstName, String lastName, String email, String phoneNumber,
                       String profilePicture, Date subscriptionEndDate) {
        super(username, password, firstName, lastName, email, phoneNumber, profilePicture);
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public Date getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(Date subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

    @Override
    public void createPlaylist(String name) {
        playlists.add(new Playlist(name));
    }

    @Override
    public void addContentToPlaylist(Playlist playlist, Content content) {
        if (playlists.contains(playlist)) {
            playlist.addContent(content);
        }
    }
}