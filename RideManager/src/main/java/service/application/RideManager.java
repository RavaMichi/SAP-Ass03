package service.application;

import service.domain.*;

import java.util.List;
import java.util.Optional;

/** Inbound port
 * Service object. Represents the rental service
 */
public interface RideManager {
    void connectUserToBike(String userId, String bikeId) throws RideManagerException;
    void disconnectUserFromBike(String userId, String bikeId) throws RideManagerException;
    List<Ride> getAllRides();
    Optional<Ride> getRide(String userId, String bikeId);
}
