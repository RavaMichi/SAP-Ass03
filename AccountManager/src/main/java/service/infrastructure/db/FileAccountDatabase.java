package service.infrastructure.db;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import service.application.AccountDatabase;
import service.domain.User;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * File-based adapter of AccountDatabase port
 */
@Singleton
public class FileAccountDatabase implements AccountDatabase {

    private final File db;
    private final List<User> users;

    @Inject
    public FileAccountDatabase() {
        this(Config.databasePath);
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

    @Override
    public Optional<User> get(String username) {
        return users.stream().filter(u -> Objects.equals(u.getUsername(), username)).findFirst();
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(users);
    }

    @Override
    public void add(User user) {
        this.users.add(user);
        writeAllUsers();
    }

    @Override
    public void updateCredit(User user, int credit) {
        users.stream().filter(u -> Objects.equals(u.getUsername(), u.getUsername())).findFirst().ifPresent(u -> {
            u.setCredits(credit);
        });
        writeAllUsers();
    }

    private List<User> readAllUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(db))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
    private void writeAllUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(db))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
