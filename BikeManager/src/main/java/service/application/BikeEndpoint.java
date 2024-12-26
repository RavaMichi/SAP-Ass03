package service.application;

import service.domain.*;

/** Inbound Port
 * Service object. Represents the endpoint for all the bikes that need to notify their current state.
 */
public interface BikeEndpoint {
    void updateBike(String id, int currentBattery, V2d currentPosition) throws BikeOperationException;
}
