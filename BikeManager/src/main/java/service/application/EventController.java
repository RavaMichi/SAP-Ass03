package service.application;

import service.domain.EBike;
import service.domain.Station;
import service.domain.V2d;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Used to manage bike events
 */
public interface EventController {
    void whenBikeAdded(Consumer<EBike> handler);
    void sendBikeAdded(EBike bike);

    void whenBikeUpdated(BiConsumer<EBike, EBike> handler);
    void sendBikeUpdated(EBike old, EBike newer);

    void whenBikeCalled(BiConsumer<EBike, V2d> handler);
    void sendBikeCalled(EBike bike, V2d target);

    void whenStationAdded(Consumer<Station> handler);
    void sendStationAdded(Station station);
}
