package service.infrastructure.db;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import service.application.AccountDatabase;
import service.infrastructure.Config;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter file-based for Ride Database
 */
@Singleton
public class FileAccountDatabase implements AccountDatabase {
    private final File db;
    private final Map<String, Integer> users;

    @Inject
    public FileAccountDatabase() {
        this(Config.accountDatabasePath);
    }

    public FileAccountDatabase(String path) {
        URL url = this.getClass().getResource("/" + path);
        try {
            assert url != null;
            this.db = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        this.users = readAllUsers();
    }

    private Map<String, Integer> readAllUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(db))) {
            return (Map<String, Integer>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }
    private void writeAllUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(db))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveUser(String id, int credits) {
        users.put(id, credits);
        writeAllUsers();
    }

    @Override
    public int getCredits(String id) {
        return users.get(id);
    }

    @Override
    public boolean doesUserExist(String username) {
        return users.containsKey(username);
    }

    @Override
    public boolean doesUserHaveEnoughCredits(String username, int amount) {
        return getCredits(username) >= amount;
    }

    @Override
    public void deductCreditsFromUser(String username, int amount) {
        int credits = getCredits(username);
        saveUser(username, credits - amount);
    }
}
