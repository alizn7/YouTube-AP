package controller;

import model.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class UserController {
    private DataBase db = DataBase.getInstance();
    private String lastMessage;
    private static final Pattern WEAK_PASSWORD = Pattern.compile("^.{8,}$");
    private static final Pattern MEDIUM_PASSWORD = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");
    private static final Pattern STRONG_PASSWORD = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^09\\d{9}$");

    public String getLastMessage() {
        return lastMessage;
    }

    private String checkPasswordStrength(String password) {
        if (STRONG_PASSWORD.matcher(password).matches()) {
            return "Strong";
        } else if (MEDIUM_PASSWORD.matcher(password).matches()) {
            return "Medium";
        } else if (WEAK_PASSWORD.matcher(password).matches()) {
            return "Weak";
        } else {
            return "Invalid (less than 8 characters)";
        }
    }

    public User signUp(String username, String password, String firstName, String lastName, String email, String phoneNumber, String profilePictureLink) {
        for (Account account : db.getAccounts()) {
            if (account.getUsername().equals(username)) {
                lastMessage = "Username already exists!";
                return null;
            }
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            lastMessage = "Invalid email format!";
            return null;
        }
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            lastMessage = "Invalid phone number format! Must be 11 digits starting with 09";
            return null;
        }
        String strength = checkPasswordStrength(password);
        if (strength.equals("Invalid (less than 8 characters)")) {
            lastMessage = "Password must be at least 8 characters!";
            return null;
        }
        NormalUser user = new NormalUser(username, password, firstName, lastName, email, phoneNumber, profilePictureLink);
        db.addAccount(user);
        user.increaseScore(10);
        lastMessage = "User signed up successfully. Password strength: " + strength;
        return user;
    }

    public String setFavouriteCategories(User user, String favouriteCategories) {
        List<Category> categories = new ArrayList<>();
        String[] cats = favouriteCategories.split(",");
        for (String cat : cats) {
            String trimmedCat = cat.trim().toUpperCase();
            if (!isValidCategory(trimmedCat)) {
                return "Invalid category name: " + trimmedCat;
            }
            categories.add(Category.valueOf(trimmedCat));
        }
        user.setFavoriteCategories(categories);
        return "Favorite categories updated";
    }

    private boolean isValidCategory(String category) {
        for (Category c : Category.values()) {
            if (c.name().equals(category)) return true;
        }
        return false;
    }

    public User login(String username, String password) {
        for (Account account : db.getAccounts()) {
            if (account instanceof User && account.getUsername().equals(username) && account.getPassword().equals(password) && !((User) account).isBanned()) {
                lastMessage = "Logged in successfully";
                return (User) account;
            }
        }
        lastMessage = "Invalid username or password, or user is banned";
        return null;
    }

    public User getAccountInfo(User user) {
        lastMessage = "Account info retrieved. Score: " + user.getScore();
        return user;
    }

    public String editUserInfo(User user, String type, String newValue) {
        if (user.isBanned()) return "User is banned!";
        if (type.equals("N")) {
            String[] names = newValue.split(" ");
            user.setFirstName(names[0]);
            if (names.length > 1) user.setLastName(names[1]);
            return "Name updated successfully";
        } else if (type.equals("P")) {
            String strength = checkPasswordStrength(newValue);
            if (strength.equals("Invalid (less than 8 characters)")) {
                return "New password must be at least 8 characters!";
            }
            user.setPassword(newValue);
            return "Password updated successfully";
        } else if (type.equals("E")) {
            if (!EMAIL_PATTERN.matcher(newValue).matches()) {
                return "Invalid email format!";
            }
            user.setEmail(newValue);
            return "Email updated successfully";
        } else if (type.equals("T")) {
            if (!PHONE_PATTERN.matcher(newValue).matches()) {
                return "Invalid phone number format!";
            }
            user.setPhoneNumber(newValue);
            return "Phone number updated successfully";
        } else if (type.equals("C")) {
            user.setProfilePicture(newValue);
            return "Profile picture updated successfully";
        }
        return "Invalid edit type!";
    }

    public String increaseCredit(User user, double value) {
        if (!user.isBanned()) {
            user.increaseBalance(value);
            user.increaseScore(15);
            return "Credit increased by " + value + ". New score: " + user.getScore();
        }
        return "User is banned!";
    }

    public String banUser(String username) {
        for (Account account : db.getAccounts()) {
            if (account instanceof User && account.getUsername().equals(username)) {
                db.banUser(username);
                lastMessage = "User " + username + " banned successfully";
                return lastMessage;
            }
        }
        lastMessage = "User " + username + " not found!";
        return lastMessage;
    }

    public PremiumUser getPremium(User user, String packageType) {
        if (user.isBanned()) {
            lastMessage = "User is banned!";
            return null;
        }
        SubscriptionPlan plan = null;
        for (SubscriptionPlan sp : SubscriptionPlan.values()) {
            if (sp.name().equalsIgnoreCase(packageType)) {
                plan = sp;
                break;
            }
        }
        if (plan == null) {
            lastMessage = "Invalid package type!";
            return null;
        }
        double price = plan.getPrice();
        String discountMessage = "";
        if (user.getScore() >= 1000) {
            price *= 0.5;
            discountMessage = "50% discount applied due to 1000+ score!";
        }
        if (user.getScore() >= 2500) {
            price = 0;
            discountMessage = "Free premium due to 2500+ score!";
        }
        if (user.getBalance() >= price) {
            user.setBalance(user.getBalance() - price);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, plan.getDays());
            PremiumUser premiumUser = new PremiumUser(user.getUsername(), user.getPassword(), user.getFirstName(),
                    user.getLastName(), user.getEmail(), user.getPhoneNumber(),
                    user.getProfilePicture(), cal.getTime());
            premiumUser.setFavoriteCategories(user.getFavoriteCategories());
            premiumUser.setPlaylists(user.getPlaylists());
            premiumUser.setChannel(user.getChannel());
            premiumUser.setSubscriptions(user.getSubscriptions());
            premiumUser.setBalance(user.getBalance());
            premiumUser.setScore(user.getScore());
            db.getAccounts().remove(user);
            db.addAccount(premiumUser);
            premiumUser.increaseScore(25);
            lastMessage = "Upgraded to premium successfully. " + discountMessage;
            return premiumUser;
        }
        lastMessage = "Insufficient balance! Required: " + price;
        return null;
    }

    public String logout(User user) {
        return "User logged out successfully";
    }
}