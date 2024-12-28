package service.infrastructure.db;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import service.application.BikeDatabase;
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
public class FileBikeDatabase implements BikeDatabase {
    private final File db;
    private final List<String> bikes;

    @Inject
    public FileBikeDatabase() {
        this(Config.bikeDatabasePath);
    }

    public FileBikeDatabase(String path) {
        URL url = this.getClass().getResource("/" + path);
        try {
            assert url != null;
            this.db = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        this.bikes = readAllBikes();
    }

    private List<String> readAllBikes() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(db))) {
            return (List<String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
    private void writeAllBikes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(db))) {
            oos.writeObject(bikes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveBike(String id) {
        bikes.add(id);
        writeAllBikes();
    }

    @Override
    public boolean doesBikeExist(String bikeId) {
        return bikes.contains(bikeId);
    }
}
