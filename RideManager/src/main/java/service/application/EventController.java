package service.application;

import service.domain.Ride;

import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface EventController {
    void whenBikeAdded(Consumer<String> handler);
    void sendBikeAdded(String id);
    void sendBikeConnected(String id);
    void sendBikeDisconnected(String id);
    void whenUserAdded(Consumer<String> handler);
    void sendUserAdded(String id);
    void whenUserUpdated(BiConsumer<String, Integer> handler);
    void sendUserUpdated(String id, int startCredits, int endCredits);

    void whenRideStarted(Consumer<Ride> handler);
    void sendRideStarted(Ride ride);
    void whenRideEnded(BiConsumer<Ride, Date> handler);
    void sendRideEnded(Ride ride, Date end);
}
