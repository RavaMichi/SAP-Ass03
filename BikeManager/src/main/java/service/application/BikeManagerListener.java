package service.application;

import service.domain.EBike;

/**
 * Listener for Bike Manager Events like adding or updating
 */
public interface BikeManagerListener {
    void onBikeAdd(EBike newBike);
    void onBikeUpdate(EBike previousBike, EBike updatedBike);
}
