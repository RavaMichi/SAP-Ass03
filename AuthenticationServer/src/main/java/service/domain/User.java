package service.domain;

import java.util.Objects;

/**
 * Entity object. Represents a user of the system
 * @param username
 * @param password
 */
public record User(String username, String password) implements java.io.Serializable {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
