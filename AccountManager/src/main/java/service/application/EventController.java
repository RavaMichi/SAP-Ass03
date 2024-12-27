package service.application;

import service.domain.User;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Used to manage user account events
 */
public interface EventController {
    void whenUserAdded(Consumer<User> handler);
    void sendUserAdded(User user);

    void whenUserUpdated(BiConsumer<User, User> handler);
    void sendUserUpdated(User old, User newer);
}
