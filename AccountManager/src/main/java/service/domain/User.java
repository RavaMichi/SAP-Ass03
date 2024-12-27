package service.domain;

/**
 * Entity object. Represents a user, with proper account and credits
 */
public class User implements java.io.Serializable {
    private final String username;
    private int credits;

    public User(String username, int credits) {
        this.username = username;
        this.credits = credits;
    }
    public String getUsername() {
        return username;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
