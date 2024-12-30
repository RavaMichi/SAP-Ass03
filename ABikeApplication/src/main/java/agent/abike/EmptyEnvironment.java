package agent.abike;

import agent.core.Environment;
import agent.core.Road;
import agent.core.V2d;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Environment implementation of empty system, with just stations.
 * Uses Kafka messages to get new stations.
 */
@KafkaListener(offsetReset = OffsetReset.EARLIEST)
public class EmptyEnvironment implements Environment {

    private ObjectMapper objectMapper = new ObjectMapper();
    private List<V2d> stations = new ArrayList<>();

    @Override
    public List<Road> getAllRoads() {
        return List.of();
    }

    @Override
    public List<Road> getRoadsInRadius(V2d center, double radius) {
        return List.of();
    }

    @Override
    public List<V2d> getStationPositions() {
        return this.stations;
    }

    @Topic("station-topic")
    public void receiveStationEvent(@KafkaKey String event, String value) {
        try {
            switch (event) {
                case "ADDED" -> {
                    var pos = objectMapper.readTree(value).get("position");
                    V2d station = objectMapper.treeToValue(pos, V2d.class);
                    stations.add(station);
                }
            }
        } catch (IOException ignored) {
            System.out.println("Event ignored: " + event + value);
        }
    }
}
