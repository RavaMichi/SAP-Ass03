package service.application;

import service.domain.EBike;
import service.domain.Station;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Used to manage bike events
 */
public interface EventController {
    void whenBikeAdded(Consumer<EBike> handler);
    void sendBikeAdded(EBike bike);

    void whenBikeUpdated(Consumer<EBike> handler);
    void sendBikeUpdated(EBike bike);

    void whenBikeCalled(Consumer<EBike> handler);
    void sendBikeCalled(EBike bike);

    void whenStationAdded(Consumer<Station> handler);
    void sendStationAdded(Station station);
}
