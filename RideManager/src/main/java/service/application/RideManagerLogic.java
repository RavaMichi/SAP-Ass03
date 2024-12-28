package service.application;

import jakarta.inject.Singleton;
import service.domain.Ride;
import service.domain.RideManagerException;

import java.util.*;

/**
 * Application logic. Manages all rentals
 */
@Singleton
public class RideManagerLogic implements RideManager {
    private final RideDatabase database;
    private final BikeManager bikeManager;
    private final AccountManager accountManager;
    private final FeeCalculator feeCalculator;
    private final List<RideManagerListener> listeners = new ArrayList<>();

    public RideManagerLogic(RideDatabase database, BikeManager bikeManager, AccountManager accountManager, FeeCalculator feeCalculator) {
        this.database = database;
        this.bikeManager = bikeManager;
        this.accountManager = accountManager;
        this.feeCalculator = feeCalculator;
    }

    @Override
    public void connectUserToBike(String userId, String bikeId) throws RideManagerException {
        if (!bikeManager.doesBikeExist(bikeId)) throw new RideManagerException("Bike does not exist");
        if (!accountManager.doesUserExist(userId)) throw new RideManagerException("User does not exist");
        if (database.getAllRides().stream().anyMatch(r -> Objects.equals(r.bikeId(), bikeId) || Objects.equals(r.userId(), userId)))
            throw new RideManagerException("User or Bike already in use");
        if (!accountManager.doesUserHaveEnoughCredits(userId, feeCalculator.minimumAmountForConnection()))
            throw new RideManagerException("User has not enough credits");

        Ride newRide = new Ride(userId, bikeId, new Date());
        database.saveRide(newRide);

        listeners.forEach(l -> l.onConnect(newRide));
    }

    @Override
    public void disconnectUserFromBike(String userId, String bikeId) throws RideManagerException {
        if (!bikeManager.doesBikeExist(bikeId)) throw new RideManagerException("Bike does not exist");
        if (!accountManager.doesUserExist(userId)) throw new RideManagerException("User does not exist");
        if (database.getRide(userId, bikeId).isEmpty()) throw new RideManagerException("User is not currently renting Bike");

        Date endTime = new Date();
        Ride ride = database.getRide(userId, bikeId).get();
        database.deleteRide(ride);
        // compute fee
        int fee = feeCalculator.calculateFee(ride.startingTime(), endTime);
        accountManager.deductCreditsFromUser(userId, fee);

        listeners.forEach(l -> l.onDisconnect(ride, endTime));
    }

    @Override
    public List<Ride> getAllRides() {
        return database.getAllRides();
    }

    @Override
    public Optional<Ride> getRide(String userId, String bikeId) {
        return database.getRide(userId, bikeId);
    }

    @Override
    public void addListener(RideManagerListener listener) {
        listeners.add(listener);
    }
}
