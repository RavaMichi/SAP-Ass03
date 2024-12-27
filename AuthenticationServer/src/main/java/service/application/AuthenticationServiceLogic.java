package service.application;

import jakarta.inject.Singleton;
import service.domain.AuthenticationException;
import service.domain.User;

/**
 * Adapter for Authentication service. Uses a database and a token generator
 */
@Singleton
public class AuthenticationServiceLogic implements AuthenticationService {

    public static final User admin = new User("admin", "admin");

    private final TokenGenerator tokenGenerator;
    private final UserDatabase userDatabase;
    private EventController eventController;

    public AuthenticationServiceLogic(TokenGenerator tokenGenerator, UserDatabase userDatabase, EventController eventController) {
        this.tokenGenerator = tokenGenerator;
        this.userDatabase = userDatabase;
        this.eventController = eventController;
    }

    @Override
    public String generateToken(String username, String password) throws AuthenticationException {
        return tokenGenerator.generate(new User(username, password));
    }

    @Override
    public String authenticate(String username, String password) throws AuthenticationException {
        User user = new User(username, password);
        if (!user.equals(admin) && !userDatabase.contains(user)) {
            throw new AuthenticationException("Invalid username or password");
        } else {
            return generateToken(username, password);
        }
    }

    @Override
    public String register(String username, String password) throws AuthenticationException {
        if (username == admin.username() || userDatabase.getUsers().stream().anyMatch(u -> u.username().equals(username))) {
            throw new AuthenticationException("User already registered");
        }
        userDatabase.addUser(new User(username, password));
        // dispatch event
        eventController.sendUserAdded(username);
        return authenticate(username, password);
    }
}
