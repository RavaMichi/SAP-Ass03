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
    private EventController eventController;

    public AccountManagerLogic(AccountDatabase accountDatabase, EventController eventController) {
        this.accountDatabase = accountDatabase;
        this.eventController = eventController;

        // User Added event comes from the Authentication Service
        // on new user, add him with default credits
        eventController.whenUserAdded(this::addUser);
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
        }
    }

    @Override
    public void addCreditToUser(String username, int amount) throws AccountOperationException {
        User u = getUser(username).orElseThrow(() -> new AccountOperationException("User " + username + " does not exist"));
        int prevCredits = u.getCredits();
        int newCredits = amount + prevCredits;
        accountDatabase.updateCredit(u, newCredits);
        // dispatch event
        eventController.sendUserUpdated(u, getUser(username).get());
    }

    @Override
    public void removeCreditFromUser(String username, int amount) throws AccountOperationException {
        addCreditToUser(username, -amount);
    }
}
