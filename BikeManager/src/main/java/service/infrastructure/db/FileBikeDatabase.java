package service.infrastructure.db;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import service.application.BikeDatabase;
import service.domain.EBike;
import service.domain.V2d;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * File-based adapter of Bike Database port
 */
@Singleton
public class FileBikeDatabase implements BikeDatabase {

    private File db;
    private List<EBike> bikes;

    @Inject
    public FileBikeDatabase() {
        this(Config.bikeDatabasePath);
    }

    public FileBikeDatabase(String path) {
        init(path);
    }

    public void init(String path) {
        URL url = this.getClass().getResource("/" + path);
        try {
            assert url != null;
            this.db = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        this.bikes = readAllBikes();
    }

    @Override
    public Optional<EBike> get(String id) {
        return bikes.stream().filter(b -> Objects.equals(b.getID(), id)).findFirst();
    }

    @Override
    public List<EBike> getAll() {
        return List.copyOf(bikes);
    }

    @Override
    public void add(EBike bike) {
        this.bikes.add(bike);
        writeAllBikes();
    }

    @Override
    public void setBatteryAndPosition(EBike bike, int battery, V2d position) {
        bikes.stream().filter(b -> Objects.equals(b.getID(), bike.getID())).findFirst().ifPresent(b -> {
            b.updateBatteryLevel(battery);
            b.updatePosition(position);
        });
        writeAllBikes();
    }

    private List<EBike> readAllBikes() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(db))) {
            return (List<EBike>) ois.readObject();
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
}
