package service.application;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import jakarta.inject.Singleton;
import service.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of both BikeManager and BikeEndpoint
 */
@Singleton
public class BikeManagerLogic implements BikeManager, BikeEndpoint {

    private BikeDatabase bikeDatabase;
    private StationDatabase stationDatabase;
    private EventController eventController;

    public BikeManagerLogic(BikeDatabase bikeDatabase, StationDatabase stationDatabase, EventController eventController) {
        this.bikeDatabase = bikeDatabase;
        this.stationDatabase = stationDatabase;
        this.eventController = eventController;

        // event handlers
        eventController.whenBikeUpdated((old, newer) -> {
            try {
                updateBike(newer.getID(), newer.getBatteryLevel(), newer.getPosition());
            } catch (BikeOperationException ignored) {}
        });
    }

    @Override
    public Optional<EBike> getBike(String id) {
        return bikeDatabase.get(id);
    }

    @Override
    public List<EBike> getAllBikes() {
        return bikeDatabase.getAll();
    }

    @Override
    public List<Station> getAllStations() {
        return stationDatabase.getAll();
    }

    @Override
    public void addBike(String id, int battery, V2d position) throws BikeOperationException {
        var bike = bikeDatabase.get(id);
        if (bike.isPresent()) {
            throw new BikeOperationException("EBike " + id + " already exists");
        } else {
            var newBike = new EBike(id, battery, position);
            bikeDatabase.add(newBike);
            // dispatch event
            eventController.sendBikeAdded(newBike);
        }
    }

    @Override
    public void callBike(String id, V2d position) throws BikeOperationException {
        // TODO
    }

    @Override
    public void addStation(String id, V2d position) throws BikeOperationException {
        var s = getAllStations().stream().filter(station -> station.id() == id || station.position() == position).findFirst();
        if (s.isPresent()) {
            throw new BikeOperationException("Station " + id + " already exists");
        } else {
            var newStation = new Station(id, position);
            stationDatabase.add(newStation);
            // dispatch event
            eventController.sendStationAdded(newStation);
        }
    }

    @Override
    public void updateBike(String id, int battery, V2d position) throws BikeOperationException {
        var bike = bikeDatabase.get(id);
        if (bike.isPresent()) {
            bikeDatabase.setBatteryAndPosition(bike.get(), battery, position);
        } else {
            throw new BikeOperationException("EBike " + id + " does not exist");
        }
    }
}