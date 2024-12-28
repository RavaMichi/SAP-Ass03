package service.infrastructure.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import service.application.RideDatabase;
import service.domain.Ride;
import service.infrastructure.Config;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Adapter file-based for Ride Database
 * Based on Event Sourcing
 */
@Singleton
public class FileRideDatabase implements RideDatabase {
    private final File db;
    private final List<Ride> ridesQuery = new ArrayList<>(); // for queries
    private static ObjectMapper mapper = new ObjectMapper();
    private static final String sep = "@@";

    @Inject
    public FileRideDatabase() {
        this(Config.rideDatabasePath);
    }

    public FileRideDatabase(String path) {
        URL url = this.getClass().getResource("/" + path);
        try {
            assert url != null;
            this.db = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        // initialize
        restoreHistory();
    }

    @Override
    public void saveRide(Ride ride) {
        ridesQuery.add(ride);
        appendRideEntry(new RideEntry(RideEvent.STARTED, ride, new Date()));
    }

    @Override
    public void deleteRide(Ride ride) {
        ridesQuery.remove(ride);
        appendRideEntry(new RideEntry(RideEvent.ENDED, ride, new Date()));
    }

    @Override
    public Optional<Ride> getRide(String userId, String bikeId) {
        return ridesQuery.stream().filter(r -> Objects.equals(r.userId(), userId) && Objects.equals(r.bikeId(), bikeId)).findFirst();
    }

    @Override
    public List<Ride> getAllRides() {
        return List.copyOf(ridesQuery);
    }

    private void restoreHistory() {
        this.ridesQuery.clear();
        var hist = readAllRidesHistory();
        for (RideEntry entry : hist) {
            switch (entry.type) {
                case STARTED -> ridesQuery.add(entry.ride);
                case ENDED -> ridesQuery.remove(entry.ride);
            }
        }
    }

    // read all text in a file
    private String readFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(db))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return "";
        }
    }
    // read the events stored
    public List<RideEntry> readAllRidesHistory() {
        return Arrays.stream(readFile().split(sep))
                .filter(s -> !Objects.equals(s, ""))
                .map(RideEntry::fromJson)
                .toList();
    }
    public void writeAllRidesHistory() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(db))) {
            oos.writeObject(ridesQuery);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void appendRideEntry(RideEntry ride) {
        try (FileOutputStream fos = new FileOutputStream(db, true)) {
            fos.write(sep.getBytes());
            fos.write(ride.toJson().getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public enum RideEvent {
        STARTED,
        ENDED
    }
    @Serdeable
    public record RideEntry(RideEvent type, Ride ride, Date timestamp) implements Serializable {
        static public RideEntry fromJson(String json) {
            try {
                return mapper.readValue(json, RideEntry.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
        public String toJson() {
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
    }
}
