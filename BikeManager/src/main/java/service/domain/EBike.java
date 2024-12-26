package service.domain;

import io.micronaut.core.annotation.Introspected;

/**
 * Entity object. Represents an EBike.
 */
@Introspected
public class EBike implements java.io.Serializable {

    private final String id;
    private int battery;
    private V2d position;

    public EBike(String id, int batteryLevel, V2d position) {
        this.id = id;
        updateBatteryLevel(batteryLevel);
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
