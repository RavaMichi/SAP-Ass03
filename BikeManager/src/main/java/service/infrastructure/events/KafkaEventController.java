package service.infrastructure.events;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.jackson.databind.JacksonDatabindMapper;
import io.micronaut.json.JsonMapper;
import service.application.EventController;
import service.domain.EBike;
import service.domain.Station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Adapter for Event Controller. Uses Apache Kafka as message broker
 */
@KafkaListener(offsetReset = OffsetReset.LATEST)
public class KafkaEventController implements EventController {

    private KafkaEventClient eventClient;
    private JsonMapper jsonMapper = new JacksonDatabindMapper();

    private final List<Consumer<EBike>> bikeAddedConsumers = new ArrayList<>();
    private final List<BiConsumer<EBike, EBike>> bikeUpdatedConsumers = new ArrayList<>();
    private final List<Consumer<EBike>> bikeCalledConsumers = new ArrayList<>();
    private final List<Consumer<Station>> stationAddedConsumers = new ArrayList<>();

    public KafkaEventController(KafkaEventClient eventClient) {
        this.eventClient = eventClient;
    }

    @Topic("bike-topic")
    public void receiveBikeEvent(@KafkaKey String event, String value) {
        try {
            switch (event) {
                case "ADDED" -> {
                    EBike bike = jsonMapper.readValue(value, EBike.class);
                    bikeAddedConsumers.forEach(c -> c.accept(bike));
                }
                case "UPDATED" -> {
                    List<EBike> bikes = jsonMapper.readValue(value, List.class);
                    bikeUpdatedConsumers.forEach(c -> c.accept(bikes.getFirst(), bikes.get(1)));
                }
                case "CALLED" -> {
                    EBike bike = jsonMapper.readValue(value, EBike.class);
                    bikeCalledConsumers.forEach(c -> c.accept(bike));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Topic("station-topic")
    public void receiveStationEvent(@KafkaKey String event, String value) {
        try {
            Station station = jsonMapper.readValue(value, Station.class);
            switch (event) {
                case "ADDED" -> stationAddedConsumers.forEach(c -> c.accept(station));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void whenBikeAdded(Consumer<EBike> handler) {
        this.bikeAddedConsumers.add(handler);
    }

    @Override
    public void sendBikeAdded(EBike bike) {
        try {
            eventClient.sendBikeEvent("ADDED", jsonMapper.writeValueAsString(bike));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void whenBikeUpdated(BiConsumer<EBike, EBike> handler) {
        this.bikeUpdatedConsumers.add(handler);
    }


    @Override
    public void sendBikeUpdated(EBike old, EBike newer) {
        try {
            List<EBike> pair = List.of(old, newer);
            eventClient.sendBikeEvent("UPDATED", jsonMapper.writeValueAsString(pair));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void whenBikeCalled(Consumer<EBike> handler) {
        this.bikeCalledConsumers.add(handler);
    }


    @Override
    public void sendBikeCalled(EBike bike) {
        try {
            eventClient.sendBikeEvent("CALLED", jsonMapper.writeValueAsString(bike));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void whenStationAdded(Consumer<Station> handler) {
        this.stationAddedConsumers.add(handler);
    }

    @Override
    public void sendStationAdded(Station station) {
        try {
            eventClient.sendStationEvent("ADDED", jsonMapper.writeValueAsString(station));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
