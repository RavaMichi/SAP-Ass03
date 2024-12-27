package service.infrastructure.events;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.jackson.databind.JacksonDatabindMapper;
import io.micronaut.json.JsonMapper;
import service.application.EventController;
import service.domain.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Adapter for Event Controller. Uses Apache Kafka as message broker
 */
@KafkaListener(offsetReset = OffsetReset.EARLIEST)
public class KafkaEventController implements EventController {

    private KafkaEventClient eventClient;
    private JsonMapper jsonMapper = new JacksonDatabindMapper();

    private final List<Consumer<String>> userAddedConsumers = new ArrayList<>();
    private final List<BiConsumer<User, User>> userUpdatedConsumers = new ArrayList<>();

    @Topic("user-topic")
    public void receiveBikeEvent(@KafkaKey String event, String value) {
        try {
            System.out.println("Received user event: " + event + value);
            switch (event) {
                case "ADDED" -> {
                    userAddedConsumers.forEach(c -> c.accept(value));
                }
                case "UPDATED" -> {
                    List<?> users = jsonMapper.readValue(value, List.class);
                    userUpdatedConsumers.forEach(c -> c.accept((User)users.getFirst(), (User)users.get(1)));
                }
            }
        } catch (IOException ignored) {
            System.out.println("Event ignored: " + event + value);
        }
    }

    @Override
    public void whenUserAdded(Consumer<String> handler) {
        this.userAddedConsumers.add(handler);
    }

    @Override
    public void sendUserAdded(String username) {
        eventClient.sendUserEvent("ADDED", username);
    }

    @Override
    public void whenUserUpdated(BiConsumer<User, User> handler) {
        this.userUpdatedConsumers.add(handler);
    }

    @Override
    public void sendUserUpdated(User old, User newer) {
        try {
            List<User> users = List.of(old, newer);
            eventClient.sendUserEvent("UPDATED", jsonMapper.writeValueAsString(users));
        } catch (IOException ignored) {}
    }
}
