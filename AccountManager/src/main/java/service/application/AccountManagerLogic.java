package service.application;

import jakarta.inject.Singleton;
import service.domain.AccountOperationException;
import service.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of AccountManager
 */
@Singleton
public class AccountManagerLogic implements AccountManager{

    private final AccountDatabase accountDatabase;
    private final List<AccountManagerListener> listeners = new ArrayList<>();

    public AccountManagerLogic(AccountDatabase accountDatabase) {
        this.accountDatabase = accountDatabase;
    }

    @Override
    public Optional<User> getUser(String username) {
        return accountDatabase.get(username);
    }

    @Override
    public List<User> getAllUsers() {
        return accountDatabase.getAll();
    }

    @Override
    public void addUser(String username) throws AccountOperationException {
        if (getUser(username).isPresent()) {
            throw new AccountOperationException("User " + username + " already exists");
        } else {
            User newUser = new User(username, 0);
            accountDatabase.add(newUser);
            this.listeners.forEach(l -> l.onUserAdded(newUser));
        }
    }

    @Override
    public void addCreditToUser(String username, int amount) throws AccountOperationException {
        User u = getUser(username).orElseThrow(() -> new AccountOperationException("User " + username + " does not exist"));
        int prevCredits = u.getCredits();
        int newCredits = amount + prevCredits;
        accountDatabase.updateCredit(u, newCredits);
        this.listeners.forEach(l -> l.onUserCreditSet(u, prevCredits, newCredits));
    }

    @Override
    public void removeCreditFromUser(String username, int amount) throws AccountOperationException {
        addCreditToUser(username, -amount);
    }

    @Override
    public void addListener(AccountManagerListener listener) {
        this.listeners.add(listener);
    }
}
