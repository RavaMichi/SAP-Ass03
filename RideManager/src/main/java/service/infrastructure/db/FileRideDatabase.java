package service.infrastructure.db;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import service.application.RideDatabase;
import service.domain.Ride;
import service.infrastructure.Config;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adapter file-based for Ride Database
 */
@Singleton
public class FileRideDatabase implements RideDatabase {
    private final File db;
    private final List<Ride> rides;

    @Inject
    public FileRideDatabase() {
        this(Config.databasePath);
    }

    public FileRideDatabase(String path) {
        URL url = this.getClass().getResource("/" + path);
        try {
            assert url != null;
            this.db = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        this.rides = readAllRides();
    }

    @Override
    public void saveRide(Ride ride) {
        rides.add(ride);
        writeAllRides();
    }

    @Override
    public void deleteRide(Ride ride) {
        rides.remove(ride);
        writeAllRides();
    }

    @Override
    public Optional<Ride> getRide(String userId, String bikeId) {
        return rides.stream().filter(r -> Objects.equals(r.userId(), userId) && Objects.equals(r.bikeId(), bikeId)).findFirst();
    }

    @Override
    public List<Ride> getAllRides() {
        return List.copyOf(rides);
    }

    private List<Ride> readAllRides() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(db))) {
            return (List<Ride>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
    private void writeAllRides() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(db))) {
            oos.writeObject(rides);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
