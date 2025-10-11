package model;

public class Admin extends Account {
    private static Admin instance;

    private Admin(String username, String password, String firstName, String lastName, String email, String phoneNumber, String profilePicture) {
        super(username, password, firstName, lastName, email, phoneNumber, profilePicture);
    }

    public static Admin getInstance(String username, String password, String firstName, String lastName, String email, String phoneNumber, String profilePicture) {
        if (instance == null) {
            instance = new Admin(username, password, firstName, lastName, email, phoneNumber, profilePicture);
        }
        return instance;
    }
}