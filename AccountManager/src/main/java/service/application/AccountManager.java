package service.application;

import service.domain.*;

import java.util.List;
import java.util.Optional;

/** Inbound port
 * Service object. Represents the user management system.
 */
public interface AccountManager {
    Optional<User> getUser(String username);
    List<User> getAllUsers();
    void addUser(String username) throws AccountOperationException;
    void addCreditToUser(String username, int amount) throws AccountOperationException;
    void removeCreditFromUser(String username, int amount) throws AccountOperationException;

    void addListener(AccountManagerListener listener);
}
