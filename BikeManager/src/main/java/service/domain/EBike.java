package service.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;

/**
 * Entity object. Represents an EBike.
 */
@Serdeable
public class EBike implements java.io.Serializable {

    private final String id;
    private int battery;
    private V2d position;

    @JsonCreator
    public EBike(String id, int battery, V2d position) {
        this.id = id;
        updateBatteryLevel(battery);
        updatePosition(position);
    }
    public String getID() {
        return this.id;
    }
    public int getBatteryLevel() {
        return this.battery;
    }
    public V2d getPosition() {
        return this.position;
    }
    public void updateBatteryLevel(int newValue) {
        this.battery = Math.clamp(newValue, 0, 100);
    }
    public void updatePosition(V2d newPosition) {
        this.position = newPosition;
    }
}
