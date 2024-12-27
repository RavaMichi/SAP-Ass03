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
        System.out.println("Received user event: " + event + value);
        if (event.equals("ADDED")) {
            userAddedConsumers.forEach(c -> c.accept(value));
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
}
