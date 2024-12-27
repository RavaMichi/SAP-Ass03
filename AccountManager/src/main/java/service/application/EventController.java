package service.application;

import service.domain.User;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Used to manage user account events
 */
public interface EventController {
    void whenUserAdded(Consumer<String> handler);
    void sendUserAdded(String username);

    void whenUserUpdated(BiConsumer<User, User> handler);
    void sendUserUpdated(User old, User newer);
}
