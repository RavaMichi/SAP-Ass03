package agent.abike;

import agent.core.AbstractAgent;
import agent.core.AgentAction;
import agent.core.Environment;
import agent.core.V2d;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.jackson.databind.JacksonDatabindMapper;
import io.micronaut.json.JsonMapper;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.*;

/**
 * Autonomous EBike implementation
 */
@KafkaListener(offsetReset = OffsetReset.EARLIEST)
public class ABikeAgent extends AbstractAgent {

    private JsonMapper objectMapper = new JacksonDatabindMapper();
    private ABikeKafkaClient client;

    private final String id;
    private int battery;
    private V2d position;
    private final double speed;

    private List<V2d> stations;
    private V2d targetPosition;

    private enum ABikeState {
        IDLE,
        CALLED,
        CONNECTED,
        NEED_RECHARGE
    }
    private ABikeState state = ABikeState.IDLE;

    private BikeUpdateMessage lastState;

    @Inject
    public ABikeAgent(Environment environment, ABikeKafkaClient client) {
        this("abike", new V2d(0,0), environment, 1000, client);
    }

    public ABikeAgent(String id, V2d startingPosition, Environment environment, long pollingRate, ABikeKafkaClient client) {
        super(environment, pollingRate);
        this.id = id;
        this.position = startingPosition;
        this.battery = 100;
        this.speed = 1;
        this.client = client;

        lastState = new BikeUpdateMessage(id, battery, position);
        this.start();
    }

    @Override
    protected void sense(Environment env) {
        switch (state) {
            case IDLE, CALLED, CONNECTED:
                break;
            case NEED_RECHARGE:
                stations = env.getStationPositions();
                break;
        }
    }

    @Override
    protected List<AgentAction> decide() {
        List<AgentAction> actions = new ArrayList<>();
        switch (state) {
            case IDLE:
                // battery check
                if (battery <= 5) {
                    log("Low battery: going to recharge");
                    actions.add(() -> state = ABikeState.NEED_RECHARGE);
                }
                break;
            case CALLED:
                // battery check
                if (battery <= 5) {
                    log("Low battery: going to recharge");
                    actions.add(() -> state = ABikeState.NEED_RECHARGE);
                }
                // moving to target
                actions.add(() -> {
                    double distance = position.distance(targetPosition);
                    if (distance < 0.1) {
                        log("Target reached");
                        state = ABikeState.IDLE;
                    } else {
                        moveTo(targetPosition);
                        battery -= 1;
                    }
                });
                break;
            case NEED_RECHARGE:
                // moving to nearest station
                actions.add(() -> {
                    var st = nearestStation().orElse(new V2d(0,0));
                    double distance = position.distance(st);
                    if (distance < 0.1) {
                        log("Recharged");
                        battery = 100;
                        state = ABikeState.IDLE;
                    } else {
                        moveTo(st);
                    }
                });
                break;
            default:
                break;
        }
        // send update message
        actions.add(() -> {
            log("Sending update");
            var bikeInfo = new BikeUpdateMessage(id, battery, position);
            client.sendBikeUpdate("UPDATED", "[" + lastState.toJson() + "," + bikeInfo.toJson() + "]");
            lastState = bikeInfo;
        });
        return actions;
    }


    private void moveTo(V2d t) {
        double distance = position.distance(t);
        double step = speed * pollingRate / 1000;
        if (distance < step) {
            position = t;
        } else {
            V2d dir = t.sub(position).div(distance);
            position = position.sub(dir);
        }
    }
    private Optional<V2d> nearestStation() {
        V2d nearestStation = null;
        double minDist = Double.MAX_VALUE;
        for (V2d station : stations) {
            double distance = position.distance(station);
            if (distance < minDist) {
                minDist = distance;
                nearestStation = station;
            }
        }
        return Optional.ofNullable(nearestStation);
    }
    private void log(final String msg) {
        System.out.println("BIKE[" + id + "] " + msg);
    }

    @Topic("bike-topic")
    public void receiveBikeEvent(@KafkaKey String event, String value) {
        try {
            switch (event) {

                case "CALLED" -> {
                    String splitter = "zzzzzz";
                    List<String> pair = Arrays.stream(value.substring(1, value.length()-1)
                            .replaceAll("[\\n ]", "")
                            .replace("},{", "}" + splitter + "{")
                            .split(splitter))
                            .toList();
                    String id = (objectMapper.readValue(pair.getFirst(), BikeUpdateMessage.class)).id();
                    V2d target = objectMapper.readValue(pair.getLast(), V2d.class);
                    if (Objects.equals(id, this.id) && state == ABikeState.IDLE) {
                        // get called
                        log("Called");
                        targetPosition = target;
                        state = ABikeState.CALLED;
                    }
                }
                case "CONNECTED" -> {
                    if (Objects.equals(value, this.id)) {
                        // bike is used by a user
                        log("Connected");
                        state = ABikeState.CONNECTED;
                    }
                }
                case "DISCONNECTED" -> {
                    if (Objects.equals(value, this.id)) {
                        // bike is not used anymore by a user
                        log("Disconnected");
                        state = ABikeState.IDLE;
                    }
                }
            }
        } catch (IOException ignored) {
            System.out.println(ignored.getMessage());
            System.out.println("Event ignored: " + event + value);
        }
    }

    @Serdeable
    public record BikeUpdateMessage(String id, int battery, V2d position) {
        public String toJson() {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("id", id);
            rootNode.put("battery", battery);
            var pos = mapper.createObjectNode();
            pos.put("x", position.x());
            pos.put("y", position.y());
            rootNode.set("position", pos);
            return rootNode.toPrettyString();
        }
    }
}
