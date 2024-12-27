package service.infrastructure.db;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import service.application.BikeDatabase;
import service.application.StationDatabase;
import service.domain.EBike;
import service.domain.Station;
import service.domain.V2d;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * File-based adapter of Stations Database port
 */
@Singleton
public class FileStationDatabase implements StationDatabase {

    private File db;
    private List<Station> stations;

    @Inject
    public FileStationDatabase() {
        this(Config.stationDatabasePath);
    }

    public FileStationDatabase(String path) {
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
        this.stations = readAllStations();
    }

    @Override
    public void add(Station station) {
        this.stations.add(station);
        writeAllStations();
    }

    @Override
    public List<Station> getAll() {
        return List.copyOf(stations);
    }

    private List<Station> readAllStations() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(db))) {
            return (List<Station>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
    private void writeAllStations() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(db))) {
            oos.writeObject(stations);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
