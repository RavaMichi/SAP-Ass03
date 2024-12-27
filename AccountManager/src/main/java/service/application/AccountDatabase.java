package service.application;

import service.domain.*;

import java.util.List;
import java.util.Optional;

/** Outbound port
 * Repository object. Allows to persist and query users.
 */
public interface AccountDatabase {
    Optional<User> get(String username);
    List<User> getAll();
    void add(User user);
    void updateCredit(User user, int credit);
}
