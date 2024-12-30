package agent.abike;

import agent.core.AbstractAgent;
import agent.core.AgentAction;
import agent.core.Environment;
import agent.core.V2d;
import io.micronaut.serde.annotation.Serdeable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ABikeAgent extends AbstractAgent {

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

    public ABikeAgent(String id, V2d startingPosition, Environment environment, long pollingRate) {
        super(environment, pollingRate);
        this.id = id;
        this.position = startingPosition;
        this.battery = 100;
        this.speed = 1;
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
                        state = ABikeState.IDLE;
                    } else {
                        moveTo(targetPosition);
                    }
                });
                break;
            case NEED_RECHARGE:
                // moving to nearest station
                actions.add(() -> {
                    var st = nearestStation().orElse(new V2d(0,0));
                    double distance = position.distance(st);
                    if (distance < 0.1) {
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

    @Serdeable
    public record BikeUpdateMessage(String id, int battery, V2d position) {}
}
