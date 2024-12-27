package service.application;

import service.domain.User;

import java.util.List;

/** Outbound port
 * Repository object, to persist users
 */
public interface UserDatabase {
    boolean contains(User user);
    void addUser(User user);
    List<User> getUsers();
}
