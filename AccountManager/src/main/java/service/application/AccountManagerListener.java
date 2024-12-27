package service.application;

import service.domain.*;

/**
 * Interface for listening to Account manager events (add user, set user credits)
 */
public interface AccountManagerListener {
    void onUserAdded(User user);
    void onUserCreditSet(User user, int oldAmount, int newAmount);
}
