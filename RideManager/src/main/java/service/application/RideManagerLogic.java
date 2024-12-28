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
    private BikeDatabase bikeDatabase;
    private AccountDatabase accountDatabase;
    private final FeeCalculator feeCalculator;
    private EventController eventController;

    public RideManagerLogic(RideDatabase database, BikeDatabase bikeDatabase, AccountDatabase accountDatabase, FeeCalculator feeCalculator, EventController eventController) {
        this.database = database;
        this.bikeDatabase = bikeDatabase;
        this.accountDatabase = accountDatabase;
        this.feeCalculator = feeCalculator;
        this.eventController = eventController;

        // register handlers
        eventController.whenRideStarted(database::saveRide);
        eventController.whenRideEnded((ride, date) -> {
            database.deleteRide(ride);
            // compute fee
            int fee = feeCalculator.calculateFee(ride.startingTime(), date);
            int startCredits = accountDatabase.getCredits(ride.userId());
            accountDatabase.deductCreditsFromUser(ride.userId(), fee);
            int endCredits = accountDatabase.getCredits(ride.userId());

            eventController.sendUserUpdated(ride.userId(), startCredits, endCredits);
        });
        eventController.whenBikeAdded(bikeDatabase::saveBike);
        eventController.whenUserAdded(id -> accountDatabase.saveUser(id, 0));
        eventController.whenUserUpdated(accountDatabase::saveUser);
    }

    @Override
    public void connectUserToBike(String userId, String bikeId) throws RideManagerException {
        if (!bikeDatabase.doesBikeExist(bikeId)) throw new RideManagerException("Bike does not exist");
        if (!accountDatabase.doesUserExist(userId)) throw new RideManagerException("User does not exist");
        if (database.getAllRides().stream().anyMatch(r -> Objects.equals(r.bikeId(), bikeId) || Objects.equals(r.userId(), userId)))
            throw new RideManagerException("User or Bike already in use");
        if (!accountDatabase.doesUserHaveEnoughCredits(userId, feeCalculator.minimumAmountForConnection()))
            throw new RideManagerException("User has not enough credits");

        Ride newRide = new Ride(userId, bikeId, new Date());

        // dispatch event
        eventController.sendRideStarted(newRide);
    }

    @Override
    public void disconnectUserFromBike(String userId, String bikeId) throws RideManagerException {
        if (!bikeDatabase.doesBikeExist(bikeId)) throw new RideManagerException("Bike does not exist");
        if (!accountDatabase.doesUserExist(userId)) throw new RideManagerException("User does not exist");
        if (database.getRide(userId, bikeId).isEmpty()) throw new RideManagerException("User is not currently renting Bike");

        Date endTime = new Date();
        Ride ride = database.getRide(userId, bikeId).get();

        // dispatch event
        eventController.sendRideEnded(ride, endTime);
    }

    @Override
    public List<Ride> getAllRides() {
        return database.getAllRides();
    }

    @Override
    public Optional<Ride> getRide(String userId, String bikeId) {
        return database.getRide(userId, bikeId);
    }
}
