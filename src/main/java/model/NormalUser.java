package model;

public class NormalUser extends User {
    private static final int MAX_PLAYLISTS = 3;
    private static final int MAX_CONTENTS_PER_PLAYLIST = 10;

    public NormalUser(String username, String password, String firstName, String lastName, String email, String phoneNumber, String profilePicture) {
        super(username, password, firstName, lastName, email, phoneNumber, profilePicture);
    }

    @Override
    public void createPlaylist(String name) {
        long userCreatedPlaylists = 0;
        for (Playlist p : playlists) {
            if (!p.getName().equals("Liked") && !p.getName().equals("Watch Later")) {
                userCreatedPlaylists++;
            }
        }
        if (userCreatedPlaylists >= MAX_PLAYLISTS) {
            throw new IllegalStateException("Normal users can only create up to " + MAX_PLAYLISTS + " playlists excluding defaults");
        }
        playlists.add(new Playlist(name));
    }
    @Override
    public void addContentToPlaylist(Playlist playlist, Content content) {
        if (!playlists.contains(playlist)) {
            throw new IllegalArgumentException("Playlist does not belong to this user");
        }
        if (playlist.getContents().size() >= MAX_CONTENTS_PER_PLAYLIST) {
            throw new IllegalStateException("Normal users can only add up to " + MAX_CONTENTS_PER_PLAYLIST + " contents per playlist");
        }
        playlist.addContent(content);
    }

    public static int getMaxPlaylists() {
        return MAX_PLAYLISTS;
    }

    public static int getMaxContentsPerPlaylist() {
        return MAX_CONTENTS_PER_PLAYLIST;
    }
}