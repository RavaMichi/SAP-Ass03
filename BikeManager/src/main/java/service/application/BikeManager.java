package service.application;

import service.domain.*;

import java.util.List;
import java.util.Optional;

/** Inbound Port
 * Service object. Represents the ebike manager system, used by other services.
 */
public interface BikeManager {
    Optional<EBike> getBike(String id);
    List<EBike> getAllBikes();
    void addBike(String id, int battery, V2d position) throws BikeOperationException;

    void addStation(String id, V2d position) throws BikeOperationException;
    List<Station> getAllStations();

    void addListener(BikeManagerListener listener);
}
