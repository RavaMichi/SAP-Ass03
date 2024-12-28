package service.application;

import service.domain.Ride;

import java.util.List;
import java.util.Optional;

/** Outbound port
 * Repository object. Persists the data regarding rides
 */
public interface RideDatabase {
    void saveRide(Ride ride);
    void deleteRide(Ride ride);
    Optional<Ride> getRide(String userId, String bikeId);
    List<Ride> getAllRides();
}
