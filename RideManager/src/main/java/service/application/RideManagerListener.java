package service.application;

import service.domain.Ride;

import java.util.Date;

/**
 * Listener for Ride Manager events
 */
public interface RideManagerListener {
    void onConnect(Ride newRide);
    void onDisconnect(Ride oldRide, Date endTime);
}
