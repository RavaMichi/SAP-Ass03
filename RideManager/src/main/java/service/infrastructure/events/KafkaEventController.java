package service.infrastructure.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.jackson.databind.JacksonDatabindMapper;
import io.micronaut.json.JsonMapper;
import io.micronaut.serde.annotation.Serdeable;
import service.application.EventController;
import service.domain.Ride;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    private ObjectMapper objectMapper = new ObjectMapper();

    private final List<Consumer<String>> bikeAddedConsumers = new ArrayList<>();
    private final List<Consumer<Ride>> rideStartedConsumers = new ArrayList<>();
    private final List<Consumer<String>> userAddedConsumers = new ArrayList<>();
    private final List<BiConsumer<String, Integer>> userUpdatedConsumers = new ArrayList<>();
    private final List<BiConsumer<Ride, Date>> rideEndedConsumers = new ArrayList<>();

    public KafkaEventController(KafkaEventClient eventClient) {
        this.eventClient = eventClient;
    }

    @Topic("user-topic")
    public void receiveUserEvent(@KafkaKey String event, String value) {
        try {
            switch (event) {
                case "ADDED" -> {
                    userAddedConsumers.forEach(c -> c.accept(value));
                }
                case "UPDATED" -> {
                    List<?> users = jsonMapper.readValue(value, List.class);
                    User u1 = (User)users.getFirst();
                    User u2 = (User)users.getLast();
                    userUpdatedConsumers.forEach(c -> c.accept(u1.username(), u2.credits()));
                }
            }
        } catch (IOException ignored) {
            System.out.println("Event ignored: " + event + value);
        }
    }
    @Topic("bike-topic")
    public void receiveBikeEvent(@KafkaKey String event, String value) {
        if (event.equals("ADDED")) {
            try {
                String bikeId = objectMapper.readTree(value).get("id").asText();
                bikeAddedConsumers.forEach(c -> c.accept(bikeId));
            } catch (JsonProcessingException ignored) {}
        }
    }
    @Topic("ride-topic")
    public void receiveRideEvent(@KafkaKey String event, String value) {
        try {
            switch (event) {
                case "STARTED" -> {
                    Ride ride = jsonMapper.readValue(value, Ride.class);
                    rideStartedConsumers.forEach(c -> c.accept(ride));
                }
                case "ENDED" -> {
                    List<?> users = jsonMapper.readValue(value, List.class);
                    Ride r = (Ride)users.getFirst();
                    Date d = (Date)users.getLast();
                    rideEndedConsumers.forEach(c -> c.accept(r, d));
                }
            }
        } catch (IOException ignored) {
            System.out.println("Event ignored: " + event + value);
        }
    }

    @Override
    public void whenBikeAdded(Consumer<String> handler) {
        bikeAddedConsumers.add(handler);
    }

    @Override
    public void sendBikeAdded(String id) {
        // no impl
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
    public void whenUserUpdated(BiConsumer<String, Integer> handler) {
        userUpdatedConsumers.add(handler);
    }

    @Override
    public void sendUserUpdated(String id, int startCredits, int endCredits) {
        try {
            List<User> users = List.of(new User(id, startCredits), new User(id, endCredits));
            eventClient.sendUserEvent("UPDATED", jsonMapper.writeValueAsString(users));
        } catch (IOException ignored) {}
    }

    @Override
    public void whenRideStarted(Consumer<Ride> handler) {
        rideStartedConsumers.add(handler);
    }

    @Override
    public void sendRideStarted(Ride ride) {
        try {
            eventClient.sendRideEvent("STARTED", jsonMapper.writeValueAsString(ride));
        } catch (IOException ignored) {}
    }

    @Override
    public void whenRideEnded(BiConsumer<Ride, Date> handler) {
        rideEndedConsumers.add(handler);
    }

    @Override
    public void sendRideEnded(Ride ride, Date end) {
        try {
            List<?> items = List.of(ride, end);
            eventClient.sendRideEvent("ENDED", jsonMapper.writeValueAsString(items));
        } catch (IOException ignored) {}
    }

    @Serdeable
    private record User(String username, int credits) {}
}
